package com.syscxp.alarm.log;

import com.syscxp.alarm.header.contact.*;
import com.syscxp.alarm.header.log.*;
import com.syscxp.alarm.header.resourcePolicy.PolicyVO;
import com.syscxp.alarm.header.resourcePolicy.RegulationVO;
import com.syscxp.alarm.header.resourcePolicy.RegulationVO_;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.DbEntityLister;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.billing.ProductType;
import com.syscxp.header.errorcode.OperationFailureException;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.tunnel.TunnelState;
import com.syscxp.sms.MailService;
import com.syscxp.sms.SmsGlobalProperty;
import com.syscxp.sms.SmsService;
import com.syscxp.sms.header.SmsVO;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import java.sql.Timestamp;
import java.util.*;

import static com.syscxp.core.Platform.operr;

public class AlarmLogManagerImpl extends AbstractService implements ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(AlarmLogManagerImpl.class);
    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private DbEntityLister dl;
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private RESTFacade restf;
    @Autowired
    private SmsService smsService;
    @Autowired
    private MailService mailService;

    @Override
    public void handleMessage(Message msg) {
        if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            handleLocalMessage(msg);
        }
    }

    private void handleApiMessage(APIMessage msg) {
        if (msg instanceof APICreateAlarmLogMsg) {
            handle((APICreateAlarmLogMsg) msg);
        }else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APICreateAlarmLogMsg msg) {

        AlarmLogVO alarmLogVO = new AlarmLogVO();
        alarmLogVO.setUuid(Platform.getUuid());
        alarmLogVO.setAccountUuid(msg.getAccountUuid());
        alarmLogVO.setProductUuid(msg.getTunnelUuid());
        alarmLogVO.setProductName(msg.getTunnelName());
        alarmLogVO.setAlarmContent(msg.getProblem());
        alarmLogVO.setStatus(msg.getStatus());
        alarmLogVO.setProductType(ProductType.TUNNEL);//todo  where should acquire

        alarmLogVO.setSmsContent(msg.getSmsContent());
        alarmLogVO.setMailContent(msg.getMailContent());
        alarmLogVO.setEventId(msg.getEventId());
        RegulationVO regulationVO = dbf.findByUuid(msg.getRuleUuid(),RegulationVO.class);
        if(regulationVO != null){
            PolicyVO policyVO = dbf.findByUuid(regulationVO.getPolicyUuid(),PolicyVO.class);
            if(policyVO != null){
                alarmLogVO.setRuleName(policyVO.getName());
            }
        }

        //计算持续时间
        SimpleQuery<AlarmTimeRecordVO> query = dbf.createQuery(AlarmTimeRecordVO.class);
        query.add(AlarmTimeRecordVO_.tunnelUuid, SimpleQuery.Op.EQ, msg.getTunnelUuid());
        query.add(AlarmTimeRecordVO_.eventId, SimpleQuery.Op.EQ, msg.getEventId());
        AlarmTimeRecordVO alarmRecord = query.find();
        if(alarmRecord == null){
            AlarmTimeRecordVO alarmTimeRecordVO = new AlarmTimeRecordVO();
            alarmTimeRecordVO.setUuid(Platform.getUuid());
            alarmTimeRecordVO.setTunnelUuid(msg.getTunnelUuid());
            alarmTimeRecordVO.setEventId(msg.getEventId());
            alarmTimeRecordVO.setProductType(ProductType.TUNNEL);
            alarmTimeRecordVO.setStatus(msg.getStatus());

            dbf.persistAndRefresh(alarmTimeRecordVO);

            int detectPeriod = regulationVO.getDetectPeriod();
            int triggerPeriod = regulationVO.getTriggerPeriod();
            alarmLogVO.setDuration((long) detectPeriod * triggerPeriod);
        }else{
            long duration = System.currentTimeMillis() - alarmRecord.getCreateDate().getTime();
            alarmLogVO.setDuration(duration/1000);

            if("OK".equals(msg.getStatus())){
                alarmRecord.setStatus(msg.getStatus());
                dbf.updateAndRefresh(alarmRecord);
            }
        }

        dbf.persistAndRefresh(alarmLogVO);

        //foreach发送短信和邮件
        try {
            sendMessage(msg);
        } catch (Exception e) {
            //保存失败信息
            e.printStackTrace();
            throw new OperationFailureException(operr("message:" + e.getMessage()));
        }


        APICreateALarmLogEvent evt = new APICreateALarmLogEvent(msg.getId());
        evt.setInventory(AlarmLogInventory.valueOf(alarmLogVO));
        bus.publish(evt);


    }

    private void sendMessage(APICreateAlarmLogMsg msg) throws Exception{
        SimpleQuery<ContactVO> query = dbf.createQuery(ContactVO.class);
        query.add(ContactVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        List<ContactVO> contactVOS = query.list();
        for (ContactVO contactVO : contactVOS) {
            Set<NotifyWayVO> notifyWayVOs = contactVO.getNotifyWayVOs();
            for (NotifyWayVO notifyWayVO : notifyWayVOs) {
                if (notifyWayVO.getCode().equals("email")) {
                    String email = contactVO.getEmail();
                    mailService.mailSend(email, "监控报警信息", msg.getMailContent());
                }

                if (notifyWayVO.getCode().equals("mobile")) {
                    String phone = contactVO.getMobile();
                    smsService.sendMsg(msg.getSession(), phone, SmsGlobalProperty.ALARM_VERIFICATION_CODE_APPID, SmsGlobalProperty.SMS_VERIFICATION_CODE_AlARM
                            , new String[]{msg.getSmsContent()}, msg.getIp());

                }

            }
        }

    }

    private void handleLocalMessage(Message msg) {
        bus.dealWithUnknownMessage(msg);
    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(AlarmConstant.SERVICE_ID_ALARM_LOG);
    }

    @Override
    public boolean start() {
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        return msg;
    }


}
