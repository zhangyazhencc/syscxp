package com.syscxp.alarm.resourcePolicy;

import com.syscxp.alarm.header.resourcePolicy.*;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.DbEntityLister;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ResourcePolicyManagerImpl  extends AbstractService implements ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(ResourcePolicyManagerImpl.class);

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

    @Override
    @MessageSafe
    public void handleMessage(Message msg) {
        if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            handleLocalMessage(msg);
        }
    }

    private void handleLocalMessage(Message msg) {
        bus.dealWithUnknownMessage(msg);
    }

    private void handleApiMessage(APIMessage msg) {
        if (msg instanceof APICreatePolicyMsg) {
            handle((APICreatePolicyMsg) msg);
        }else if (msg instanceof APICreateRegulationMsg) {
            handle((APICreateRegulationMsg) msg);
        }else if (msg instanceof APIGetRegulationByPolicyMsg) {
            handle((APIGetRegulationByPolicyMsg) msg);
        }else if (msg instanceof APIUpdateRegulationMsg) {
            handle((APIUpdateRegulationMsg) msg);
        }else if (msg instanceof APIDeleteRegulationMsg) {
            handle((APIDeleteRegulationMsg) msg);
        }else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIDeleteRegulationMsg msg) {
        RegulationVO regulationVO = dbf.findByUuid(msg.getUuid(),RegulationVO.class);
        if(regulationVO!=null){
            dbf.remove(regulationVO);
        }
        APIDeleteRegulationEvent event = new APIDeleteRegulationEvent(msg.getId());
        bus.publish(event);
    }

    private void handle(APIUpdateRegulationMsg msg) {

        RegulationVO regulationVO = dbf.findByUuid(msg.getUuid(),RegulationVO.class);
        if(msg.getAlarmThreshold()!=regulationVO.getAlarmThreshold()){
            regulationVO.setAlarmThreshold(msg.getAlarmThreshold());
        }
        if(msg.getComparisonRuleUuid()!=null){
            regulationVO.setComparisonRuleVO(dbf.findByUuid(msg.getComparisonRuleUuid(),ComparisonRuleVO.class));
        }
        if(msg.getDetectPeriod()!= regulationVO.getDetectPeriod()){
            regulationVO.setDetectPeriod(msg.getDetectPeriod());
        }
        if(msg.getTriggerPeriod()!= regulationVO.getTriggerPeriod()){
            regulationVO.setTriggerPeriod(msg.getTriggerPeriod());
        }
        dbf.updateAndRefresh(regulationVO);
        APIUpdateRegulationEvent event = new APIUpdateRegulationEvent(msg.getId());
        event.setInventory(RegulationInventory.valueOf(regulationVO));
        bus.publish(event);
    }

    private void handle(APIGetRegulationByPolicyMsg msg) {
        SimpleQuery<RegulationVO> query = dbf.createQuery(RegulationVO.class);
        query.add(RegulationVO_.policyUuid, SimpleQuery.Op.EQ, msg.getPolicyUuid());
//        query.setStart(msg.getStart());
//        query.setLimit(msg.getLimit());
        List<RegulationVO> regulationVOList = query.list();
        APIGetRegulationByPolicyReply reply = new APIGetRegulationByPolicyReply();
        reply.setInventories(RegulationInventory.valueOf(regulationVOList));
        bus.reply(msg,reply);
    }

    private void handle(APICreateRegulationMsg msg) {
        RegulationVO regulationVO = new RegulationVO();
        regulationVO.setUuid(Platform.getUuid());
        regulationVO.setAlarmThreshold(msg.getAlarmThreshold());
        regulationVO.setComparisonRuleVO(dbf.findByUuid(msg.getComparisonRuleUuid(),ComparisonRuleVO.class));
        regulationVO.setMonitorTargetVO(dbf.findByUuid(msg.getMonitorTargetUuid(),MonitorTargetVO.class));
        regulationVO.setDetectPeriod(msg.getDetectPeriod());
        regulationVO.setPolicyUuid(msg.getPolicyUuid());
        regulationVO.setTriggerPeriod(msg.getTriggerPeriod());
        dbf.persistAndRefresh(regulationVO);
        APICreateRegulationEvent event = new APICreateRegulationEvent(msg.getId());
        event.setInventory(RegulationInventory.valueOf(regulationVO));
        bus.publish(event);
    }

    private void handle(APICreatePolicyMsg msg) {
        PolicyVO policyVO = new PolicyVO();
        policyVO.setUuid(Platform.getUuid());
        policyVO.setName(msg.getName());
        policyVO.setDescription(msg.getDescription());
        policyVO.setBindResources(0);
        policyVO.setProductType(msg.getProductType());
        dbf.persistAndRefresh(policyVO);
        APICreatePolicyEvent event = new APICreatePolicyEvent(msg.getId());
        event.setInventory(PolicyInventory.valueOf(policyVO));
        bus.publish(event);
    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(AlarmConstant.SERVICE_ID_RESOURCE_POLICY);
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
