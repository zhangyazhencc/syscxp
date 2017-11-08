package com.syscxp.alarm.log;

import com.syscxp.alarm.header.contact.*;
import com.syscxp.alarm.header.log.*;
import com.syscxp.alarm.header.resourcePolicy.PolicyVO;
import com.syscxp.alarm.header.resourcePolicy.RegulationVO;
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
import com.syscxp.header.rest.SyncHttpCallHandler;
import com.syscxp.sms.MailService;
import com.syscxp.sms.SmsGlobalProperty;
import com.syscxp.sms.SmsService;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
        bus.dealWithUnknownMessage(msg);
    }

    private void sendMessage(AlarmLogCallbackCmd cmd) throws Exception {
        SimpleQuery<ContactVO> query = dbf.createQuery(ContactVO.class);
        query.add(ContactVO_.accountUuid, SimpleQuery.Op.EQ, cmd.getAccountUuid());
        List<ContactVO> contactVOS = query.list();
        for (ContactVO contactVO : contactVOS) {
            Set<NotifyWayVO> notifyWayVOs = contactVO.getNotifyWayVOs();
            for (NotifyWayVO notifyWayVO : notifyWayVOs) {
                if (notifyWayVO.getCode().equals("email")) {
                    String email = contactVO.getEmail();
                    mailService.mailSend(email, "监控报警信息", "【犀思云】服务器预警信息如下:\n             " + cmd.getMailContent());
                }

                if (notifyWayVO.getCode().equals("mobile")) {
                    String phone = contactVO.getMobile();
                    smsService.sendMsg(null, phone, SmsGlobalProperty.ALARM_APPID, SmsGlobalProperty.SMS_AlARM_TEMPLATEID
                            , new String[]{cmd.getSmsContent()}, "");

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

        restf.registerSyncHttpCallHandler("alarmLog", AlarmLogCallbackCmd.class,
                new SyncHttpCallHandler<AlarmLogCallbackCmd>() {
                    @Override
                    public String handleSyncHttpCall(AlarmLogCallbackCmd cmd) {

                        if (AlarmStatus.OK.equals(cmd.getStatus())) {
                            SimpleQuery<AlarmLogVO> query = dbf.createQuery(AlarmLogVO.class);
                            query.add(AlarmLogVO_.productUuid, SimpleQuery.Op.EQ, cmd.getTunnelUuid());
                            query.add(AlarmLogVO_.regulationUuid, SimpleQuery.Op.EQ, cmd.getRegulationUuid());
                            query.add(AlarmLogVO_.status, SimpleQuery.Op.EQ, AlarmStatus.PROBLEM);
                            List<AlarmLogVO> alarmLogVOS = query.list();
                            for (AlarmLogVO vo : alarmLogVOS) {
                                vo.setResumeTime(new Timestamp(System.currentTimeMillis()));
                                vo.setStatus(cmd.getStatus());
                                long time = (System.currentTimeMillis() - vo.getAlarmTime().getTime()) / 1000 + vo.getDuration();
                                vo.setDuration(time);
                                dbf.updateAndRefresh(vo);
                            }

                        } else {

                            AlarmLogVO alarmLogVO = new AlarmLogVO();
                            alarmLogVO.setUuid(Platform.getUuid());
                            alarmLogVO.setProductUuid(cmd.getTunnelUuid());
                            alarmLogVO.setProductType(ProductType.TUNNEL);
                            alarmLogVO.setAlarmContent(cmd.getProblem());
                            alarmLogVO.setStatus(cmd.getStatus());
                            alarmLogVO.setAccountUuid(cmd.getAccountUuid());
                            alarmLogVO.setSmsContent(cmd.getSmsContent());
                            alarmLogVO.setMailContent(cmd.getMailContent());
                            alarmLogVO.setRegulationUuid(cmd.getRegulationUuid());
                            RegulationVO regulationVO = dbf.findByUuid(cmd.getRegulationUuid(), RegulationVO.class);
                            if (regulationVO != null) {
                                PolicyVO policyVO = dbf.findByUuid(regulationVO.getPolicyUuid(), PolicyVO.class);
                                if (policyVO != null) {
                                    alarmLogVO.setPolicyName(policyVO.getName());
                                }
                            }
                            alarmLogVO.setEventId(cmd.getEventId());
                            alarmLogVO.setAlarmTime(new Timestamp(System.currentTimeMillis()));
                            //持续时间
                            alarmLogVO.setDuration((long) regulationVO.getDetectPeriod() * regulationVO.getTriggerPeriod());
                            dbf.persistAndRefresh(alarmLogVO);

                        }

                        //foreach发送短信和邮件
                        try {
                            sendMessage(cmd);
                        } catch (Exception e) {
                            //保存失败信息
                            e.printStackTrace();
                            throw new OperationFailureException(operr("message:" + e.getMessage()));
                        }

                        return null;
                    }
                });

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
