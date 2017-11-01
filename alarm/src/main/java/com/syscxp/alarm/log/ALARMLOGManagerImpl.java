package com.syscxp.alarm.log;

import com.cloopen.rest.sdk.CCPRestSDK;
import com.syscxp.alarm.header.contact.*;
import com.syscxp.alarm.header.log.*;
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
import com.syscxp.header.identity.SessionInventory;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.sms.MailService;
import com.syscxp.sms.SmsGlobalProperty;
import com.syscxp.sms.SmsService;
import com.syscxp.sms.SmsServiceImpl;
import com.syscxp.sms.header.SmsVO;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import groovy.ui.SystemOutputInterceptor;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.*;

import static com.syscxp.core.Platform.operr;

public class ALARMLOGManagerImpl  extends AbstractService implements ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(ALARMLOGManagerImpl.class);
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
        alarmLogVO.setProductUuid(msg.getTunnel_id());
        alarmLogVO.setProductName(msg.getTunnel_name());
        alarmLogVO.setAlarmContent(msg.getProblem());
        alarmLogVO.setResumeTime(msg.getResumed());
        alarmLogVO.setAlarmTime(msg.getCreated());
        alarmLogVO.setStatus(msg.getStatus());
        alarmLogVO.setAccountUuid(msg.getUser_id());
        alarmLogVO.setProductType(ProductType.TUNNEL);//todo  where should acquire
        SimpleQuery<AlarmLogVO> queryTime = dbf.createQuery(AlarmLogVO.class);
        queryTime.add(AlarmLogVO_.productUuid, SimpleQuery.Op.EQ, msg.getTunnel_id());
        queryTime.orderBy(AlarmLogVO_.createDate, SimpleQuery.Od.DESC);
        List<AlarmLogVO> alarmLogList = queryTime.list();
        int count = 0;
        int countSize = alarmLogList.size();
        Timestamp time = null;
        for(AlarmLogVO vo: alarmLogList){
            time = vo.getCreateDate();
          if(msg.getStatus().equals("RESUME")){
              if(vo.getStatus().equals("RESUME")&& time != null){
                      long times = time.getTime();
                      long currentTime = System.currentTimeMillis();
                      long diffTime = currentTime-times;
                      alarmLogVO.setDuration((int)diffTime/1000);
                      break;
              }
              if(count == countSize-1){
                  long diffTime = System.currentTimeMillis() - vo.getCreateDate().getTime();
                  alarmLogVO.setDuration((int)diffTime/1000);
              }

          }else if(msg.getStatus().equals("ALARM")){
              if(vo.getStatus().equals("RESUME")){
                  if(time != null){
                      long times = time.getTime();
                      long currentTime = System.currentTimeMillis();
                      long diffTime = currentTime-times;
                      alarmLogVO.setDuration((int)diffTime/1000);
                      break;
                  }
              }
              if(count == countSize-1){
                  long diffTime = System.currentTimeMillis() - vo.getCreateDate().getTime();
                  alarmLogVO.setDuration((int)diffTime/1000);
              }
          }
          count++;
        }

        //alarmLogVO.setDuration(0);//持续时间
        //alarmLogVO.setDurationTimeUnit(TimeUnit.MILLISECONDS);
        dbf.persistAndRefresh(alarmLogVO);
        SimpleQuery<ContactVO> query = dbf.createQuery(ContactVO.class);
        query.add(ContactVO_.accountUuid, SimpleQuery.Op.EQ,msg.getUser_id());
        List<ContactVO> groups = query.list();
            for(ContactVO contactVO: groups){
                Set<NotifyWayVO> notifyWayVOs = contactVO.getNotifyWayVOs();
                for(NotifyWayVO notifyWayVO: notifyWayVOs){
                    if( notifyWayVO.getCode().equals("email")){
                        String email = contactVO.getEmail();
                        try{
                            Boolean result = mailService.mailSend(email,"监控报警信息",msg.getProblem());
                        }catch (Exception e){
                            //保存失败信息
                            e.printStackTrace();
                            throw new OperationFailureException(operr("message:" + e.getMessage()));
                        }
                        logger.debug("发送告警的内容是：" + msg.getProblem() +
                                "[邮件:"+email+"]");

                    }

                    if( notifyWayVO.getCode().equals("mobile")){
                        String phone = contactVO.getMobile();
                        SmsVO sms = smsService.sendMsg(msg.getSession(), phone, SmsGlobalProperty.ALARM_VERIFICATION_CODE_APPID, SmsGlobalProperty.SMS_VERIFICATION_CODE_AlARM
                                , new String[]{msg.getProblem(), "10"}, msg.getIp());
//                        if(sms.getStatusCode() != "000000"){
//                            //保存失败信息
//
//                        }
                        logger.debug("发送告警的内容是：" + msg.getProblem() +
                                "[手机号码:"+phone+"]");
                    }

                }
            }
        //foreach发送短信和邮件
        APICreateALarmLogEvent evt = new APICreateALarmLogEvent(msg.getId());
        evt.setInventory(AlarmLogInventory.valueOf(alarmLogVO));
        bus.publish(evt);


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
