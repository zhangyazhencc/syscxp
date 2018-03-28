package com.syscxp.alarm.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.syscxp.alarm.AlarmGlobalConfig;
import com.syscxp.alarm.AlarmUtil;
import com.syscxp.alarm.header.contact.ContactVO;
import com.syscxp.alarm.header.contact.ContactVO_;
import com.syscxp.alarm.header.contact.NotifyWayVO;
import com.syscxp.alarm.header.log.*;
import com.syscxp.alarm.header.resourcePolicy.MonitorTargetVO;
import com.syscxp.alarm.header.resourcePolicy.PolicyVO;
import com.syscxp.alarm.header.resourcePolicy.RegulationVO;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.GLock;
import com.syscxp.core.db.Q;
import com.syscxp.core.db.SimpleQuery;
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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

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
    private RESTFacade restf;
    @Autowired
    private SmsService smsService;
    @Autowired
    private MailService mailService;

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
        logger.debug(java.lang.String.format("[HandleAlarmMsg] event:p0 content: %s", msg.getAlarmValue()));

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
                eventVO.setProductUuid(eventVO.getExpression().getResourceUuid());
                eventVO.setRegulationUuid(eventVO.getExpression().getRegulationId());
                // reset eventTime
                JSONObject jsonObject = JSON.parseObject(msg.getAlarmValue());
                long eventTime = Long.valueOf(jsonObject.get("eventTime").toString()) * 1000;
                eventVO.setEventTime(new Timestamp(eventTime));

                // HandleALarmReply
                handleAlarm(eventVO);

                chain.next();
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
        else
            throw new IllegalArgumentException(String.format("unknown alarm status [%s]", eventVO.getStatus()));
    }

    /**
     * tunnel告警
     *
     * @param eventVO
     */
    private void tunnelAlarm(AlarmEventVO eventVO) {
        logger.info(String.format("[Tunnel Alarm] TunnelUuid: [%s]", eventVO.getProductUuid()));

        GLock gLock = new GLock("TunnelAlarm.lock", 60);
        gLock.lock();
        try {
            AlarmEventVO existedEvent = getExistedEvents(eventVO);
            if (existedEvent == null)
                dbf.persistAndRefresh(eventVO);

            AlarmLogVO logVO = getExistedAlarmLog(eventVO);
            if (logVO == null) {
                processTunnelEvent(eventVO);

                logVO = generateAlarmLog(eventVO);
                dbf.persistAndRefresh(logVO);

                sendMessage(logVO);
            }
        } finally {
            gLock.unlock();
        }
    }

    /**
     * tunnel恢复
     *
     * @param eventVO
     */
    private void tunnelRecover(AlarmEventVO eventVO) {
        logger.info(String.format("[Tunnel Recover] TunnelUuid: [%s]", eventVO.getProductUuid()));

        GLock gLock = new GLock("TunnelRecover.lock", 60);
        gLock.lock();
        try {
            AlarmEventVO existedEvent = getExistedEvents(eventVO);
            if (existedEvent != null) {
                existedEvent.setStatus(AlarmStatus.OK);
                dbf.updateAndRefresh(existedEvent);
            } else
                dbf.persist(eventVO);

            AlarmLogVO logVO = getExistedAlarmLog(eventVO);
            if (logVO != null) {
                if (isRecovered(eventVO)) {
                    processTunnelEvent(eventVO);

                    logVO = generateAlarmLog(eventVO);
                    dbf.updateAndRefresh(logVO);

                    sendMessage(logVO);
                }
            }
        } finally {
            gLock.unlock();
        }
    }

    /**
     * 不存在status=problem的event为恢复
     *
     * @param eventVO
     * @return
     */
    private boolean isRecovered(AlarmEventVO eventVO) {
        List<AlarmEventVO> problemEvents = Q.New(AlarmEventVO.class)
                .eq(AlarmEventVO_.status, AlarmStatus.PROBLEM)
                .eq(AlarmEventVO_.productUuid, eventVO.getProductUuid())
                .eq(AlarmEventVO_.regulationUuid, eventVO.getRegulationUuid())
                .list();

        if (problemEvents.isEmpty())
            return true;
        else
            return false;
    }

    private void handleStandardAlarm(AlarmEventVO eventVO) {
        if (eventVO.getStatus() == AlarmStatus.PROBLEM)
            standardAlarm(eventVO);
        else if (eventVO.getStatus() == AlarmStatus.OK)
            standardRecover(eventVO);
        else
            throw new IllegalArgumentException(String.format("unknown alarm status [%s]", eventVO.getStatus()));
    }

    private void standardAlarm(AlarmEventVO eventVO) {
        logger.info(String.format("[standardAlarm] ProductUuid: [%s]", eventVO.getProductUuid()));

    }

    private void standardRecover(AlarmEventVO eventVO) {
        logger.info(String.format("[standardRecover] ProductUuid: [%s]", eventVO.getProductUuid()));
    }

    /**
     * 按告警event生成告警log
     *
     * @param eventVO
     * @return
     */
    private AlarmLogVO generateAlarmLog(AlarmEventVO eventVO) {
        AlarmLogVO logVO = new AlarmLogVO();
        if (eventVO.getStatus() == AlarmStatus.PROBLEM) {
            logVO.setUuid(Platform.getUuid());
            logVO.setEventId(eventVO.getId());
            logVO.setAlarmTime(eventVO.getEventTime());
            logVO.setAlarmContent(getAlarmContent(eventVO));
            logVO.setProductType(eventVO.getPolicyVO().getProductType());
            logVO.setProductUuid(eventVO.getProductUuid());
            logVO.setPolicyUuid(eventVO.getPolicyVO().getUuid());
            logVO.setRegulationUuid(eventVO.getRegulationUuid());
        } else if (eventVO.getStatus() == AlarmStatus.OK) {
            logVO = getExistedAlarmLog(eventVO);
            logVO.setResumeTime(eventVO.getEventTime());
            logVO.setDuration((eventVO.getEventTime().getTime() - logVO.getAlarmTime().getTime()) / 1000);
        }

        logVO.setStatus(eventVO.getStatus());
        logVO.setAccountUuid(eventVO.getAccountUuid());

        String alarmMessage = getMessageContent(eventVO, eventVO.getProductName());
        logVO.setSmsContent(alarmMessage);
        logVO.setMailContent(alarmMessage);

        return logVO;
    }

    /***
     * 从tunnel获取专线数据(含共点专线)
     * 查询条件: vlan,vsi,port && tunnel.statte = Enabled
     * @param tunnelUuid
     * @return
     */
    private List<TunnelAlarmCmd.TunnelInfo> getTunnelInfos(String tunnelUuid) {

        String url = AlarmUtil.getProductCommandUrl(ProductType.TUNNEL);

        Map<String, String> header = new HashMap<>();
        header.put(RESTConstant.COMMAND_PATH, "FalconTunnel");

        Map params = new HashMap();
        params.put("tunnelUuid", tunnelUuid);

        TunnelAlarmCmd.TunnelAlarmResponse response;
        try {
            response = restf.syncJsonPost(url, JSONObjectUtil.toJsonString(params), header, TunnelAlarmCmd.TunnelAlarmResponse.class);

            if (!response.isSuccess()) {
                throw new IllegalArgumentException(String.format("failed to get tunnel info [tunnelUuid: %s]! Error: %s"
                        , tunnelUuid, response.getMsg()));
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("failed to get tunnel info [tunnelUuid: %s]! Error: %s"
                    , tunnelUuid, e.getMessage()));
        }

        if (response.getInventories() == null || response.getInventories().size() == 0)
            throw new RuntimeException(String.format("no tunnel existed or tunnel is not Enabled [tunnelUuid: %s]! ", tunnelUuid));

        return response.getInventories();
    }

    /**
     * 获取告警短信、邮件内容
     *
     * @param eventVO
     * @param productName：产品名称
     * @return
     */
    private String getMessageContent(AlarmEventVO eventVO, String productName) {
        AlarmTemplateVO templateVO = getAlarmTemplate(eventVO);
        String content = null;
        if (templateVO != null) {
            MonitorTargetVO monitorTargetVO = eventVO.getRegulationVO().getMonitorTargetVO();
            String leftValue = eventVO.getLeftValue() + monitorTargetVO.getUnit();
            String rightValue = eventVO.getRegulationVO().getAlarmThreshold() + monitorTargetVO.getUnit();

            if (eventVO.getStatus() == AlarmStatus.PROBLEM)
                content = String.format(templateVO.getTemplate(), productName
                        , monitorTargetVO.getTargetName(), leftValue, rightValue);
            else if (eventVO.getStatus() == AlarmStatus.OK)
                content = String.format(templateVO.getTemplate(), productName
                        , monitorTargetVO.getTargetName(), rightValue);
        }

        if (content == null)
            throw new RuntimeException(String.format("failed to generate alarm message! " +
                    "ProductUuid: %s Status: %s ", eventVO.getProductUuid(), eventVO.getStatus()));

        return content;
    }

    /**
     * 获取告警内容
     *
     * @param eventVO
     * @return
     */
    private String getAlarmContent(AlarmEventVO eventVO) {
        String targetName = eventVO.getRegulationVO().getMonitorTargetVO().getTargetName();
        String content = String.format("%s超出告警阀值[%s]，达到[%s]"
                , targetName, eventVO.getRegulationVO().getAlarmThreshold(), eventVO.getLeftValue());

        return content;
    }

    /**
     * 获取告警内容模板
     *
     * @param eventVO
     * @return
     */
    private AlarmTemplateVO getAlarmTemplate(AlarmEventVO eventVO) {
        List<AlarmTemplateVO> templateVOS = Q.New(AlarmTemplateVO.class)
                .eq(AlarmTemplateVO_.productType, eventVO.getPolicyVO().getProductType())
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

    /**
     * 专线数据处理
     *
     * @param eventVO
     */
    private void processTunnelEvent(AlarmEventVO eventVO) {
        List<TunnelAlarmCmd.TunnelInfo> tunnelInfos = getTunnelInfos(eventVO.getProductUuid());
        //带宽占用率计算
        recalculateBandwidth(eventVO, tunnelInfos);

        TunnelAlarmCmd.TunnelInfo tunnelInfo = new TunnelAlarmCmd.TunnelInfo();
        for (TunnelAlarmCmd.TunnelInfo tunnel : tunnelInfos) {
            if (tunnel.getTunnelUuid().equals(eventVO.getProductUuid())) {
                tunnelInfo = tunnel;
                break;
            }
        }
        if (tunnelInfo == null)
            throw new RuntimeException(String.format("failed to process tunnel event! [TunnelUuid: %s]"
                    , eventVO.getProductUuid()));

        eventVO.setProductName(tunnelInfo.getTunnelName());
        eventVO.setAccountUuid(tunnelInfo.getAccountUuid());
    }

    /**
     * 重新计算带宽告警值与阈值（含共点专线带宽）
     *
     * @param eventVO
     * @param tunnelInfos
     */
    private void recalculateBandwidth(AlarmEventVO eventVO, List<TunnelAlarmCmd.TunnelInfo> tunnelInfos) {
        try {
            String metric = eventVO.getExpression().getMetric();

            if ("switch.if.In".equals(metric)) {
                long sumBandwidth = 0;
                for (TunnelAlarmCmd.TunnelInfo tunnelInfo : tunnelInfos) {
                    if (isSharePoint(eventVO, tunnelInfo))
                        sumBandwidth += tunnelInfo.getBandwidth();
                }

                if(sumBandwidth <= 0)
                    throw new RuntimeException("[recalculateBandwidth] 获取带宽异常！");

                float leftValue = NumberUtils.toFloat(eventVO.getLeftValue()) / sumBandwidth * 100;
                eventVO.setLeftValue(String.valueOf(leftValue > 100 ? 100 : (float)(Math.round(leftValue*100))/100));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("failed to recalculate bandwidth! " +
                            "[TunnelUuid: %s Metric: %s RightValue: %s LeftValue: %s]  Error: %s"
                    , eventVO.getProductUuid(), eventVO.getExpression().getMetric()
                    , eventVO.getExpression().getRightValue(), eventVO.getLeftValue(), e.getMessage()));
        }
    }

    /**
     * 共点判断
     * 同交换机、同vlan视为共点（同策略同步判断共点逻辑）
     *
     * @param eventVO
     * @param tunnelInfo
     * @return
     */
    private boolean isSharePoint(AlarmEventVO eventVO, TunnelAlarmCmd.TunnelInfo tunnelInfo) {
        boolean isSharePoint = false;
        try {
            Map tags = eventVO.getExpression().getTags();

            if (!tags.isEmpty()) {
                String endpoint = tags.get("endpoint").toString();
                String vlan = tags.get("ifName").toString();

                if (StringUtils.equals(endpoint, tunnelInfo.getEndpointAMip())) {
                    if (vlan.equals("Vlanif" + tunnelInfo.getEndpointAVlan()))
                        isSharePoint = true;
                } else if (StringUtils.equals(endpoint, tunnelInfo.getEndpointZMip())) {
                    if (vlan.equals("Vlanif" + tunnelInfo.getEndpointZVlan()))
                        isSharePoint = true;
                }
            } else
                throw new RuntimeException("[isSharePoint] eventVO.getExpression().getTags() 数据为空！");
        } catch (Exception e) {
            throw new RuntimeException(String.format("[isSharePoint] Error: %s", e.getMessage()));
        }

        return isSharePoint;
    }

    /**
     * 信息发送
     *
     * @param logVO
     */
    private void sendMessage(AlarmLogVO logVO) {
        try {
            List<String> phoneList = new ArrayList<>();
            List<String> emailList = new ArrayList<>();

            SimpleQuery<ContactVO> query = dbf.createQuery(ContactVO.class);
            query.add(ContactVO_.accountUuid, SimpleQuery.Op.EQ, logVO.getAccountUuid());
            List<ContactVO> contactVOS = query.list();
            for (ContactVO contactVO : contactVOS) {
                Set<NotifyWayVO> notifyWayVOs = contactVO.getNotifyWayVOs();
                if (notifyWayVOs != null) {
                    for (NotifyWayVO notifyWayVO : notifyWayVOs) {
                        if ("email".equals(notifyWayVO.getCode()) && AlarmGlobalConfig.ALARM_SEND_MAIL.value(Boolean.class))
                            emailList.add(contactVO.getEmail());
                        else if ("mobile".equals(notifyWayVO.getCode()) && AlarmGlobalConfig.ALARM_SEND_SMS.value(Boolean.class))
                            phoneList.add(contactVO.getMobile());
                    }
                }
            }

            // send mail
            if (emailList.size() > 0)
                mailService.sendAlarmMonitorMsg(emailList, "监控报警信息"
                        , String.format("【犀思云】服务预警信息如下: %s)", logVO.getMailContent()));

            // send smss
            List<String> smsDatas = new ArrayList<String>();
            smsDatas.add(logVO.getSmsContent());
            if (phoneList.size() > 0)
                smsService.sendAlarmMonitorMsg(phoneList, smsDatas);
        } catch (Exception e) {
            throw new RuntimeException(String.format("failed to send alarm message [ProductUuid: %s ,AccountUuid: %s] Error: %s"
                    , logVO.getProductUuid(), logVO.getAccountUuid(), e.getMessage()));
        }

    }

    /***
     * 获取已存在的告警事件
     * -
     * @param eventVO
     * @return
     */
    private AlarmEventVO getExistedEvents(AlarmEventVO eventVO) {
        List<AlarmEventVO> existedEvents = Q.New(AlarmEventVO.class)
                .eq(AlarmEventVO_.endpoint, eventVO.getEndpoint())
                .eq(AlarmEventVO_.productUuid, eventVO.getProductUuid())
                .eq(AlarmEventVO_.regulationUuid, eventVO.getRegulationUuid())
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
                .eq(AlarmLogVO_.productUuid, eventVO.getProductUuid())
                .eq(AlarmLogVO_.regulationUuid, eventVO.getRegulationUuid())
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
