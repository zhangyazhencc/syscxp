package com.syscxp.alarm.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.syscxp.alarm.AlarmUtil;
import com.syscxp.alarm.header.contact.*;
import com.syscxp.alarm.header.log.*;
import com.syscxp.alarm.header.resourcePolicy.MonitorTargetVO;
import com.syscxp.alarm.header.resourcePolicy.PolicyVO;
import com.syscxp.alarm.header.resourcePolicy.RegulationVO;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.db.UpdateQuery;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.thread.ChainTask;
import com.syscxp.core.thread.SyncTaskChain;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.billing.ProductType;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.RESTConstant;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.sms.MailService;
import com.syscxp.sms.SmsService;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestHeader;

import java.sql.Timestamp;
import java.util.*;

public class AlarmLogManagerImpl extends AbstractService implements ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(AlarmLogManagerImpl.class);
    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private ThreadFacade thf;
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private RESTFacade restf;
    @Autowired
    private SmsService smsService;
    @Autowired
    private MailService mailService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @MessageSafe
    public void handleMessage(Message msg) {
        if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            handleLocalMessage(msg);
        }
    }

    private void handleApiMessage(Message msg) {
        bus.dealWithUnknownMessage(msg);
    }

    private void handleLocalMessage(Message msg) {
        if (msg instanceof HandleAlarmMsg) {
            handle((HandleAlarmMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(HandleAlarmMsg msg) {
        //logger.debug(java.lang.String.format("[HandleAlarmMsg] event:p0 content: %s",msg.getAlarmValue()));

        String uuid = Platform.getUuid();
        thf.chainSubmit(new ChainTask(msg) {
            @Override
            public String getSyncSignature() {
                return String.format("Alarm - %s", uuid);
            }

            @Override
            public void run(final SyncTaskChain chain) {
                AlarmEventVO eventVO = JSON.parseObject(msg.getAlarmValue(), AlarmEventVO.class);

                eventVO.setUuid(uuid);
                eventVO.setExpressionUuid(eventVO.getExpression().getId());
                eventVO.setResourceUuid(eventVO.getExpression().getResourceUuid());
                eventVO.setRegulationId(eventVO.getExpression().getRegulationId());
                // reset eventTime
                JSONObject jsonObject = JSON.parseObject(msg.getAlarmValue());
                long eventTime = Long.valueOf(jsonObject.get("eventTime").toString()) * 1000;
                eventVO.setEventTime(new Timestamp(eventTime));

                // HandleALarmReply
                handleAlarm(eventVO);
            }

            @Override
            protected int getSyncLevel() {
                return 5;
            }

            @Override
            public String getName() {
                return getSyncSignature();
            }
        });
    }

    private void handleAlarm(AlarmEventVO eventVO) {
        if (eventVO != null) {
            RegulationVO regulationVO = dbf.findByUuid(eventVO.getExpression().getRegulationId(), RegulationVO.class);
            PolicyVO policyVO;
            if (regulationVO != null) {
                policyVO = dbf.findByUuid(regulationVO.getPolicyUuid(), PolicyVO.class);
                if (policyVO == null) {
                    logger.error(String.format("fail to get policy [%s]!", regulationVO.getPolicyUuid()));
                    return;
                }
            } else {
                logger.error(String.format("fail to get regulation [%s]!", eventVO.getExpression().getRegulationId()));
                return;
            }

            eventVO.setPolicyVO(policyVO);
            eventVO.setRegulationVO(regulationVO);
            if (policyVO.getProductType() == ProductType.TUNNEL)
                handleTunnelAlarm(eventVO);
            else
                handleStandardAlarm(eventVO);
        }
    }

    private void handleTunnelAlarm(AlarmEventVO eventVO) {
        if (eventVO.getStatus() == AlarmStatus.PROBLEM)
            tunnelAlarm(eventVO);
        else if (eventVO.getStatus() == AlarmStatus.OK)
            tunnelRecover(eventVO);
    }

    private void tunnelAlarm(AlarmEventVO eventVO) {
        logger.info(String.format("[Tunnel Alarm] EventId: [%s]", eventVO.getId()));

        AlarmEventVO existedEvent = getExistedEvents(eventVO);
        if (existedEvent == null)
            dbf.persistAndRefresh(eventVO);

        AlarmLogVO existedLog = getExistedAlarmLog(eventVO);
        if (existedLog == null) {
            processTunnelEvent(eventVO);
            AlarmLogVO logVO = generateAlarmLog(eventVO);

            dbf.persistAndRefresh(logVO);

            sendMessage(logVO);
        }
    }

    private void tunnelRecover(AlarmEventVO eventVO) {
        logger.info(String.format("[Tunnel Recover] EventId: [%s]", eventVO.getId()));

        AlarmEventVO existedEvent = getExistedEvents(eventVO);
        if (existedEvent != null) {
            existedEvent.setStatus(AlarmStatus.OK);
            dbf.updateAndRefresh(existedEvent);
        } else
            dbf.persist(eventVO);

        AlarmLogVO existedLog = getExistedAlarmLog(eventVO);
        if (existedLog != null) {
            List<AlarmEventVO> problemEvents = Q.New(AlarmEventVO.class)
                    .eq(AlarmEventVO_.status, AlarmStatus.PROBLEM)
                    .eq(AlarmEventVO_.resourceUuid, eventVO.getResourceUuid())
                    .eq(AlarmEventVO_.regulationId, eventVO.getRegulationId())
                    .list();

            if (problemEvents.isEmpty()) {
                processTunnelEvent(eventVO);

                existedLog.setResumeTime(eventVO.getEventTime());
                existedLog.setDuration((System.currentTimeMillis() - existedLog.getAlarmTime().getTime()) / 1000);
                existedLog.setStatus(AlarmStatus.OK);
                existedLog.setAlarmContent(getAlarmContent(eventVO));
                String alarmMessage = getMessageContent(eventVO, eventVO.getResourceName());
                existedLog.setSmsContent(alarmMessage);
                existedLog.setMailContent(alarmMessage);
                existedLog.setAccountUuid(eventVO.getAccountUuid());

                dbf.updateAndRefresh(existedLog);

                sendMessage(existedLog);
            }
        }



        /*
        或按endpoint、resourceUuid 、regulationUuid、status=PROBLEM查询EventVO
            存在
                更新status = OK
            不存在
                插入

        按 alarmEventVO.getExpression().getResourceUuid()、alarmEventVO.getStatus()=PROBLEM查询AlarmLogVO数据
            存在
                更新status = OK
                发送恢复短信
            不存在
                无操作
        */
    }

    private void handleStandardAlarm(AlarmEventVO alarmEventVO) {
        if (alarmEventVO.getStatus() == AlarmStatus.PROBLEM)
            standardAlarm(alarmEventVO);
        else if (alarmEventVO.getStatus() == AlarmStatus.OK)
            standardRecover(alarmEventVO);
    }

    private void standardAlarm(AlarmEventVO alarmEventVO) {
        logger.info(String.format("[standardAlarm] evendId: [%s]", alarmEventVO.getId()));

    }

    private void standardRecover(AlarmEventVO alarmEventVO) {
        logger.info(String.format("[standardRecover] evendId: [%s]", alarmEventVO.getId()));
    }

    private AlarmLogVO generateAlarmLog(AlarmEventVO eventVO) {
        AlarmLogVO logVO = new AlarmLogVO();
        logVO.setUuid(Platform.getUuid());
        logVO.setEventId(eventVO.getId());
        logVO.setAlarmTime(eventVO.getEventTime());
        logVO.setStatus(eventVO.getStatus());
        logVO.setProductType(eventVO.getPolicyVO().getProductType());
        logVO.setProductUuid(eventVO.getResourceUuid());
        logVO.setPolicyUuid(eventVO.getPolicyVO().getUuid());
        logVO.setRegulationUuid(eventVO.getRegulationId());

        logVO.setAlarmContent(getAlarmContent(eventVO));
        String alarmMessage = getMessageContent(eventVO, eventVO.getResourceName());
        logVO.setSmsContent(alarmMessage);
        logVO.setMailContent(alarmMessage);
        logVO.setAccountUuid(eventVO.getAccountUuid());

        return logVO;
    }

    private List<TunnelAlarmCmd.TunnelInfo> getTunnelInfos(ProductType productType, String tunnelUuid) {

        String url = AlarmUtil.getProductCommandUrl(productType);

        Map<String, String> header = new HashMap<>();
        header.put(RESTConstant.COMMAND_PATH, "FalconTunnel");

        Map params = new HashMap();
        params.put("tunnelUuid", tunnelUuid);

        TunnelAlarmCmd.TunnelAlarmResponse response;
        try {
            response = restf.syncJsonPost(url, JSONObjectUtil.toJsonString(params), header, TunnelAlarmCmd.TunnelAlarmResponse.class);

            if (!response.isSuccess()) {
                throw new IllegalArgumentException(String.format("failed to get tunnel info [tunnelUuid:%s]! Error: %s"
                        , tunnelUuid, response.getMsg()));
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("failed to get tunnel info [tunnelUuid:%s]! Error: %s"
                    , tunnelUuid, e.getMessage()));
        }

        return response.getTunnelInfos();
    }

    private String getMessageContent(AlarmEventVO eventVO, String productName) {
        AlarmTemplateVO templateVO = getAlarmTemplate(eventVO);
        String content = null;
        if (templateVO != null) {
            MonitorTargetVO monitorTargetVO = eventVO.getRegulationVO().getMonitorTargetVO();
            String leftValue = eventVO.getLeftValue() + monitorTargetVO.getUnit();
            String rightValue = eventVO.getExpression().getRightValue() + monitorTargetVO.getUnit();

            if (eventVO.getStatus() == AlarmStatus.PROBLEM)
                content = String.format(templateVO.getTemplate(), productName
                        , monitorTargetVO.getTargetName(), leftValue, rightValue);
            else if (eventVO.getStatus() == AlarmStatus.OK)
                content = String.format(templateVO.getTemplate(), productName
                        , monitorTargetVO.getTargetName(), leftValue, rightValue);
        }

        if (content == null)
            throw new RuntimeException(String.format("failed to generate alarm message! " +
                    "ResourceUuid: %s Status: %s ", eventVO.getResourceUuid(), eventVO.getStatus()));

        return content;
    }

    private String getAlarmContent(AlarmEventVO eventVO) {
        String targetName = eventVO.getRegulationVO().getMonitorTargetVO().getTargetName();
        String content = String.format("%s超出告警阀值[%s]，达到[%s]"
                , targetName, eventVO.getExpression().getRightValue(), eventVO.getLeftValue());

        return content;
    }

    private void processTunnelEvent(AlarmEventVO eventVO){
        List<TunnelAlarmCmd.TunnelInfo> tunnelInfos = getTunnelInfos(
                eventVO.getPolicyVO().getProductType(), eventVO.getResourceUuid());
        TunnelAlarmCmd.TunnelInfo tunnelInfo = new TunnelAlarmCmd.TunnelInfo();
        for (TunnelAlarmCmd.TunnelInfo tunnel : tunnelInfos) {
            if (tunnelInfo.getTunnelUuid().equals(eventVO.getResourceUuid())) {
                tunnelInfo = tunnel;
            }
        }

        if (tunnelInfo == null)
            throw new RuntimeException(String.format("failed to generate alarm log [TunnelUuid: %s]"
                    , eventVO.getResourceUuid()));

        //带宽占用率计算
        recalculateBandwidth(eventVO, tunnelInfos);
        eventVO.setResourceName(tunnelInfo.getTunnelName());
        eventVO.setAccountUuid(tunnelInfo.getAccountUuid());
    }

    private void recalculateBandwidth(AlarmEventVO eventVO, List<TunnelAlarmCmd.TunnelInfo> tunnelInfos) {
        String metric = eventVO.getExpression().getMetric();
        try {
            if ("switch.if.In".equals(metric)) {
                long sumBandwidth = 0;
                for (TunnelAlarmCmd.TunnelInfo tunnelInfo : tunnelInfos) {
                    sumBandwidth += tunnelInfo.getBandwidth();
                }
                long rightValue = (Long.valueOf(eventVO.getExpression().getRightValue()) / sumBandwidth * 100);
                eventVO.getExpression().setRightValue(String.valueOf(rightValue));

                long leftValue = Long.valueOf(eventVO.getLeftValue()) / sumBandwidth * 100;
                if (leftValue > 100)
                    leftValue = 100;
                eventVO.setLeftValue(String.valueOf(leftValue));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("failed to recalculate bandwidth! " +
                            "[EventId: %s Metric: %s RightValue: %s LeftValue: %s]"
                    , eventVO.getId(), eventVO.getExpression().getMetric()
                    , eventVO.getExpression().getRightValue(), eventVO.getLeftValue()));
        }
    }

    private AlarmTemplateVO getAlarmTemplate(AlarmEventVO eventVO) {
        List<AlarmTemplateVO> templateVOS = Q.New(AlarmTemplateVO.class)
                .eq(AlarmTemplateVO_.productType, eventVO.getPolicyVO().getProductType())
                .eq(AlarmTemplateVO_.monitorTargetUuid, eventVO.getRegulationVO().getMonitorTargetVO().getUuid())
                .eq(AlarmTemplateVO_.status, eventVO.getStatus())
                .list();

        if (!templateVOS.isEmpty())
            return templateVOS.get(0);
        else
            throw new RuntimeException(String.format("no template existed! " +
                            "productType: %s monitorTargetUuid: %s status: %s"
                    , eventVO.getPolicyVO().getProductType()
                    , eventVO.getRegulationVO().getMonitorTargetVO().getUuid()
                    , eventVO.getStatus()));
    }

    public void saveAlarmLog(APIHandleTunnelAlarmMsg msg) {
        AlarmLogVO alarmLogVO = new AlarmLogVO();
        alarmLogVO.setUuid(Platform.getUuid());
        alarmLogVO.setProductUuid(msg.getTunnelUuid());
        alarmLogVO.setProductType(ProductType.TUNNEL);
        alarmLogVO.setAlarmContent(msg.getProblem());
        alarmLogVO.setStatus(msg.getStatus());
        alarmLogVO.setAccountUuid(msg.getAccountUuid());
        alarmLogVO.setSmsContent(msg.getSmsContent());
        alarmLogVO.setMailContent(msg.getMailContent());
        alarmLogVO.setRegulationUuid(msg.getRegulationUuid());
        RegulationVO regulationVO = dbf.findByUuid(msg.getRegulationUuid(), RegulationVO.class);
        if (regulationVO != null) {
            //持续时间
//            alarmLogVO.setDuration((long) regulationVO.getDetectPeriod() * regulationVO.getTriggerPeriod());
            alarmLogVO.setDuration(0);

            PolicyVO policyVO = dbf.findByUuid(regulationVO.getPolicyUuid(), PolicyVO.class);
            if (policyVO != null) {
                alarmLogVO.setPolicyUuid(policyVO.getUuid());
            }
        }
        alarmLogVO.setEventId(msg.getEventId());
        alarmLogVO.setAlarmTime(new Timestamp(System.currentTimeMillis()));

        alarmLogVO.setCount(1);
        dbf.persistAndRefresh(alarmLogVO);
    }

    private void sendMessage(APIHandleTunnelAlarmMsg msg) {

        List<String> smsDatas = new ArrayList<String>();
        smsDatas.add(msg.getSmsContent());
        List<String> phoneList = new ArrayList<>();
        List<String> emailList = new ArrayList<>();

        SimpleQuery<ContactVO> query = dbf.createQuery(ContactVO.class);
        query.add(ContactVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
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
        if (emailList.size() > 0) {
            mailService.sendAlarmMonitorMsg(emailList, "监控报警信息", "【犀思云】服务器预警信息如下:\n  "
                    + msg.getMailContent());
        }
        if (phoneList.size() > 0) {
            smsService.sendAlarmMonitorMsg(phoneList, smsDatas);
        }

    }

    private void sendMessage(AlarmLogVO logVO) {
        try {
            List<String> phoneList = new ArrayList<>();
            List<String> emailList = new ArrayList<>();

            SimpleQuery<ContactVO> query = dbf.createQuery(ContactVO.class);
            query.add(ContactVO_.accountUuid, SimpleQuery.Op.EQ, logVO.getAccountUuid());
            List<ContactVO> contactVOS = query.list();
            for (ContactVO contactVO : contactVOS) {
                Set<NotifyWayVO> notifyWayVOs = contactVO.getNotifyWayVOs();
                for (NotifyWayVO notifyWayVO : notifyWayVOs) {
                    if ("email".equals(notifyWayVO.getCode()))
                        emailList.add(contactVO.getEmail());
                    else if ("mobile".equals(notifyWayVO.getCode()))
                        phoneList.add(contactVO.getMobile());
                }
            }

            // send mail
            if (emailList.size() > 0)
                mailService.sendAlarmMonitorMsg(emailList, "监控报警信息"
                        , String.format("【犀思云】服务器预警信息如下: %s)", logVO.getMailContent()));

            // send smss
            List<String> smsDatas = new ArrayList<String>();
            smsDatas.add(logVO.getSmsContent());
            if (phoneList.size() > 0)
                smsService.sendAlarmMonitorMsg(phoneList, smsDatas);
        } catch (Exception e) {
            throw new RuntimeException(String.format("failed to send alarm message [AccountUuid: %s] Error: %s"
                    , logVO.getAccountUuid(), e.getMessage()));
        }

    }

    /***
     * 获取已存在的告警事件
     * @param eventVO
     * @return
     */
    private AlarmEventVO getExistedEvents(AlarmEventVO eventVO) {
        List<AlarmEventVO> existedEvents = Q.New(AlarmEventVO.class)
                .eq(AlarmEventVO_.endpoint, eventVO.getEndpoint())
                .eq(AlarmEventVO_.resourceUuid, eventVO.getResourceUuid())
                .eq(AlarmEventVO_.regulationId, eventVO.getRegulationId())
                .eq(AlarmEventVO_.status, AlarmStatus.PROBLEM)
                .list();

        if (!existedEvents.isEmpty())
            return existedEvents.get(0);
        else
            return null;
    }

    /***
     * 获取已存在的告警纪录
     * @param eventVO
     * @return
     */
    private AlarmLogVO getExistedAlarmLog(AlarmEventVO eventVO) {
        List<AlarmLogVO> alarmLogs = Q.New(AlarmLogVO.class)
                .eq(AlarmLogVO_.productUuid, eventVO.getResourceUuid())
                .eq(AlarmLogVO_.regulationUuid, eventVO.getRegulationId())
                .eq(AlarmLogVO_.status, AlarmStatus.PROBLEM)
                .list();

        if (!alarmLogs.isEmpty())
            return alarmLogs.get(0);
        else
            return null;
    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(AlarmConstant.SERVICE_ID_ALARM_LOG);
    }

    @Override
    public boolean start() {
        restf.registerSyncHttpCallHandler("tunnelAlarmLog", APIHandleTunnelAlarmMsg.class,
                msg -> {
                    TunnelAlarmCmd.TunnelAlarmResponse response = new TunnelAlarmCmd.TunnelAlarmResponse();
                    try {
                        //RedisTemplate redisTemplate = new RedisTemplate();
                        logger.info("redis template test ");
                        redisTemplate.opsForValue().set("name", "tom");

                        //String name = redisTemplate.opsForValue().get("name").toString();
                        //logger.info("redis name: " + name);


                        SimpleQuery<AlarmLogVO> query = dbf.createQuery(AlarmLogVO.class);
                        query.add(AlarmLogVO_.productUuid, SimpleQuery.Op.EQ, msg.getTunnelUuid());
                        query.add(AlarmLogVO_.regulationUuid, SimpleQuery.Op.EQ, msg.getRegulationUuid());
                        query.orderBy(AlarmLogVO_.createDate, SimpleQuery.Od.DESC);
                        List<AlarmLogVO> alarmLogVOS = query.list();
                        AlarmLogVO log = null;
                        if (alarmLogVOS != null && alarmLogVOS.size() > 0) {
                            log = alarmLogVOS.get(0);
                        }

                        if (log == null) {
                            if (AlarmStatus.PROBLEM.equals(msg.getStatus())) {
                                saveAlarmLog(msg);
                                //sendMessage(msg);
                            }
                        } else {
                            if (log.getStatus() == AlarmStatus.OK) {
                                if (AlarmStatus.PROBLEM.equals(msg.getStatus())) {
                                    saveAlarmLog(msg);
                                    //sendMessage(msg);
                                }
                            } else {
                                if (AlarmStatus.PROBLEM.equals(msg.getStatus())) {
                                    if (log.getAlarmTime().before(new Timestamp(dbf.getCurrentSqlTime().getTime() - 3600 * 1000))) {
                                        saveAlarmLog(msg);
                                        //sendMessage(msg);
                                    } else {
                                        if (log.getCount() < 2) {
                                            log.setCount(log.getCount() + 1);
                                            dbf.updateAndRefresh(log);
                                        }
                                    }
                                } else {
                                    log.setCount(log.getCount() - 1);
                                    if (log.getCount() == 0) {
                                        log.setResumeTime(new Timestamp(System.currentTimeMillis()));
                                        log.setStatus(msg.getStatus());
                                        long time = (System.currentTimeMillis() - log.getAlarmTime().getTime()) / 1000 + log.getDuration();
                                        log.setDuration(time);
                                        //sendMessage(msg);
                                    }
                                    dbf.updateAndRefresh(log);
                                }

                            }
                        }

                        response.setSuccess(false);
                        response.setMsg("fail to send message!");

                    } catch (Exception e) {
                        response.setSuccess(false);
                        response.setMsg(String.format("Failed to handle alarm data: %s", e.getMessage()));
                    }
                    return JSONObjectUtil.toJsonString(response);
                });

        // todo: 测试待删除
        //从redis获取数据采用异步调用消息
//        restf.registerSyncHttpCallHandler("tunnelAlarmLog", APIHandleTunnelAlarmMsg.class,
//                amsg -> {
//                    TunnelAlarmCmd.TunnelAlarmResponse response = new TunnelAlarmCmd.TunnelAlarmResponse();
//
//                    try {
//                        bus.makeLocalServiceId(amsg,AlarmConstant.SERVICE_ID_ALARM_LOG);
//                        bus.send(amsg, new CloudBusCallBack(null) {
//                            @Override
//                            public void run(MessageReply reply) {
//                                if (reply.isSuccess()) {
//                                    response.setSuccess(true);
//                                    response.setMsg("success");
//                                }else {
//                                    response.setSuccess(false);
//                                    response.setMsg(reply.getError().getDetails());
//                                }
//                            }
//                        });
//                    } catch (Exception e) {
//                        response.setSuccess(false);
//                        response.setMsg(String.format("[TunnelAlarmLog] send alarm msg failed! " +
//                                "TunnelUuid:  %s, Error: %s", amsg.getTunnelUuid(), e.getMessage()));
//                        logger.error(response.getMsg());
//                    }
//
//                    return JSONObjectUtil.toJsonString(response);
//                });

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
