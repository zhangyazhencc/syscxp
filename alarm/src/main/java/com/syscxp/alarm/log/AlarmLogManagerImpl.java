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
import com.syscxp.sms.SmsService;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
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

    private void sendMessage(AlarmLogCallbackCmd cmd) {

        List<String> smsDatas = new ArrayList<String>();
        smsDatas.add(cmd.getSmsContent());
        List<String> phoneList = new ArrayList<>();
        List<String> emailList = new ArrayList<>();

        SimpleQuery<ContactVO> query = dbf.createQuery(ContactVO.class);
        query.add(ContactVO_.accountUuid, SimpleQuery.Op.EQ, cmd.getAccountUuid());
        List<ContactVO> contactVOS = query.list();
        for (ContactVO contactVO : contactVOS) {


            Set<NotifyWayVO> notifyWayVOs = contactVO.getNotifyWayVOs();
            for (NotifyWayVO notifyWayVO : notifyWayVOs) {
                if (notifyWayVO.getCode().equals("email")) {
                    String email = contactVO.getEmail();
                    emailList.add(email);
                }

                if (notifyWayVO.getCode().equals("mobile")) {
                    String phone = contactVO.getMobile();
                    phoneList.add(phone);
                }

            }

        }
        if(emailList.size()>0){
            mailService.sendAlarmMonitorMsg(emailList,"监控报警信息","【犀思云】服务器预警信息如下:\n          " + cmd.getMailContent());
        }
        if(phoneList.size()>0){
            smsService.sendAlarmMonitorMsg(phoneList, smsDatas);
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
                        SimpleQuery<AlarmLogVO> query = dbf.createQuery(AlarmLogVO.class);
                        query.add(AlarmLogVO_.productUuid, SimpleQuery.Op.EQ, cmd.getTunnelUuid());
                        query.add(AlarmLogVO_.regulationUuid, SimpleQuery.Op.EQ, cmd.getRegulationUuid());
                        query.orderBy(AlarmLogVO_.createDate, SimpleQuery.Od.DESC);
                        List<AlarmLogVO> alarmLogVOS = query.list();
                        AlarmLogVO log = null;
                        if (alarmLogVOS != null && alarmLogVOS.size() > 0) {
                             log = alarmLogVOS.get(0);
                        }


                        if (log == null){
                            if (AlarmStatus.PROBLEM.equals(cmd.getStatus())) {
                                saveAlarmLog(cmd);
                                sendMessage(cmd);
                            }
                        }else {
                            if (log.getStatus() == AlarmStatus.OK) {
                                if (AlarmStatus.PROBLEM.equals(cmd.getStatus())) {
                                    saveAlarmLog(cmd);
                                    sendMessage(cmd);
                                }
                            } else {
                                if (AlarmStatus.PROBLEM.equals(cmd.getStatus())) {
                                    if (log.getAlarmTime().before(new Timestamp(dbf.getCurrentSqlTime().getTime() - 3600 * 1000))){
                                        saveAlarmLog(cmd);
                                        sendMessage(cmd);
                                    }else{
                                        if (log.getCount() < 2) {
                                            log.setCount(log.getCount() + 1);
                                            dbf.updateAndRefresh(log);
                                        }
                                    }
                                } else {
                                    log.setCount(log.getCount() - 1);
                                    if (log.getCount() == 0) {
                                        log.setResumeTime(new Timestamp(System.currentTimeMillis()));
                                        log.setStatus(cmd.getStatus());
                                        long time = (System.currentTimeMillis() - log.getAlarmTime().getTime()) / 1000 + log.getDuration();
                                        log.setDuration(time);
                                        sendMessage(cmd);
                                    }
                                    dbf.updateAndRefresh(log);
                                }

                            }
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

    public void saveAlarmLog(AlarmLogCallbackCmd cmd){
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
            //持续时间
            alarmLogVO.setDuration((long) regulationVO.getDetectPeriod() * regulationVO.getTriggerPeriod());

            PolicyVO policyVO = dbf.findByUuid(regulationVO.getPolicyUuid(), PolicyVO.class);
            if (policyVO != null) {
                alarmLogVO.setPolicyVO(policyVO);
            }
        }
        alarmLogVO.setEventId(cmd.getEventId());
        alarmLogVO.setAlarmTime(new Timestamp(System.currentTimeMillis()));

        alarmLogVO.setCount(1);
        dbf.persistAndRefresh(alarmLogVO);
    }


}
