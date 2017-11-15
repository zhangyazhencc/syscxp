package com.syscxp.alarm.resourcePolicy;

import com.syscxp.alarm.AlarmGlobalProperty;
import com.syscxp.alarm.header.resourcePolicy.*;
import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.db.UpdateQuery;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.header.AbstractService;
import com.syscxp.header.alarm.APIUpdateTunnelInfoForFalconMsg;
import com.syscxp.header.alarm.APIUpdateTunnelInfoForFalconReply;
import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.billing.ProductType;
import com.syscxp.header.errorcode.OperationFailureException;
import com.syscxp.header.falconapi.FalconApiCommands;
import com.syscxp.header.falconapi.FalconApiRestConstant;
import com.syscxp.header.identity.SessionInventory;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.RestAPIResponse;
import com.syscxp.header.rest.RestAPIState;
import com.syscxp.header.tunnel.tunnel.APIQueryTunnelDetailForAlarmMsg;
import com.syscxp.header.tunnel.tunnel.APIQueryTunnelDetailForAlarmReply;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;


public class ResourcePolicyManagerImpl extends AbstractService implements ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(ResourcePolicyManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
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
        } else if (msg instanceof APICreateRegulationMsg) {
            handle((APICreateRegulationMsg) msg);
        } else if (msg instanceof APIGetRegulationByPolicyMsg) {
            handle((APIGetRegulationByPolicyMsg) msg);
        } else if (msg instanceof APIUpdateRegulationMsg) {
            handle((APIUpdateRegulationMsg) msg);
        } else if (msg instanceof APIDeleteRegulationMsg) {
            handle((APIDeleteRegulationMsg) msg);
        } else if (msg instanceof APIUpdatePolicyMsg) {
            handle((APIUpdatePolicyMsg) msg);
        } else if (msg instanceof APIDeletePolicyMsg) {
            handle((APIDeletePolicyMsg) msg);
        } else if (msg instanceof APIDeleteResourceMsg) {
            handle((APIDeleteResourceMsg) msg);
        } else if (msg instanceof APIUpdateTunnelInfoForFalconMsg) {
            handle((APIUpdateTunnelInfoForFalconMsg) msg);
        } else if (msg instanceof APIGetComparisonRuleListMsg) {
            handle((APIGetComparisonRuleListMsg) msg);
        } else if (msg instanceof APIGetMonitorTargetListMsg) {
            handle((APIGetMonitorTargetListMsg) msg);
        }  else if (msg instanceof APIAttachPolicyToResourceMsg) {
            handle((APIAttachPolicyToResourceMsg) msg);
        }  else if (msg instanceof APIGetPolicyByResourceUuidMsg) {
            handle((APIGetPolicyByResourceUuidMsg) msg);
        }  else if (msg instanceof APIGetResourceUuidsByPolicyMsg) {
            handle((APIGetResourceUuidsByPolicyMsg) msg);
        }   else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIGetResourceUuidsByPolicyMsg msg) {

        APIGetResourceUuidsByPolicyReply reply = new APIGetResourceUuidsByPolicyReply();
        SimpleQuery<ResourcePolicyRefVO> query = dbf.createQuery(ResourcePolicyRefVO.class);
        query.add(ResourcePolicyRefVO_.policyUuid, SimpleQuery.Op.EQ, msg.getPolicyUuid());
        query.select(ResourcePolicyRefVO_.resourceUuid);
        List<String> resourceUuids = query.listValue();
        reply.setResourceUuids(resourceUuids);
        bus.reply(msg,reply);

    }

    private void handle(APIGetPolicyByResourceUuidMsg msg) {

        List<ResourcePolicyRefInventory> lists = new ArrayList<>();
        for(String resourceUuid: msg.getResourceUuids()){
            SimpleQuery<ResourcePolicyRefVO> query = dbf.createQuery(ResourcePolicyRefVO.class);
            query.add(ResourcePolicyRefVO_.resourceUuid, SimpleQuery.Op.EQ, resourceUuid);
            ResourcePolicyRefVO resourcePolicyRefVO = query.find();
            ResourcePolicyRefInventory inventory = new ResourcePolicyRefInventory();
            inventory.setResourceUuid(resourceUuid);
            if(resourcePolicyRefVO != null){
                PolicyVO policyVO = dbf.findByUuid(resourcePolicyRefVO.getPolicyUuid(), PolicyVO.class);
                if(policyVO!=null){
                    inventory.setPolicyInventory(PolicyInventory.valueOf(policyVO));
                }

            }
            lists.add(inventory);
        }
        APIGetPolicyByResourceUuidReply reply = new APIGetPolicyByResourceUuidReply();
        reply.setInventories(lists);
        bus.reply(msg,reply);
    }

    private void handle(APIAttachPolicyToResourceMsg msg) {

        UpdateQuery q = UpdateQuery.New(ResourcePolicyRefVO.class);
        q.condAnd(ResourcePolicyRefVO_.resourceUuid, SimpleQuery.Op.EQ, msg.getResourceUuid());
        q.delete();

        APIAttachPolicyToResourceEvent event = new APIAttachPolicyToResourceEvent(msg.getId());

        if(!StringUtils.isEmpty(msg.getPolicyUuid())){
            ResourcePolicyRefVO resourcePolicyRefVO = new ResourcePolicyRefVO();
            resourcePolicyRefVO.setPolicyUuid(msg.getPolicyUuid());
            resourcePolicyRefVO.setResourceUuid(msg.getResourceUuid());
            dbf.persistAndRefresh(resourcePolicyRefVO);
            event.setInventory(PolicyInventory.valueOf(dbf.findByUuid(msg.getPolicyUuid(),PolicyVO.class)));
        }

        if(!updatePoliciesByResources(msg.getSession().getAccountUuid(),msg.getResourceUuid())){
//            throw new OperationFailureException(Platform.operr("资源不存在！"));
        }

        bus.publish(event);

    }

    private boolean updatePoliciesByResources(String userid, String ResourceUuid){
        TunnelParameter tunnelparameter = new TunnelParameter();
        List<Rule> rulelist = new ArrayList<>();
        List<TunnelParameter> tunnelparameterlist = new ArrayList<>();

        List<String> list =  new ArrayList<>();
        list.add(ResourceUuid);
        Map<String, Object> map = getTunnelInfo(list);

        tunnelparameter.setUser_id(userid);
        tunnelparameter.setTunnel_id(ResourceUuid);
        if(map == null || map.get(ResourceUuid) == null){
            return false;
        }else{
            FalconApiCommands.Tunnel tunnel = (FalconApiCommands.Tunnel)map.get(ResourceUuid);
            tunnelparameter.setEndpointA_vid(tunnel.getEndpointA_vid());
            tunnelparameter.setEndpointB_vid(tunnel.getEndpointB_vid());
            tunnelparameter.setEndpointA_ip(tunnel.getEndpointA_ip());
            tunnelparameter.setEndpointB_ip(tunnel.getEndpointB_ip());
            tunnelparameter.setBandwidth(tunnel.getBandwidth());
        }


        SimpleQuery<ResourcePolicyRefVO> policyrquery = dbf.createQuery(ResourcePolicyRefVO.class);
        policyrquery.add(ResourcePolicyRefVO_.resourceUuid, SimpleQuery.Op.EQ, ResourceUuid);
        List<ResourcePolicyRefVO> policylist = policyrquery.list();
        Rule rule = null;
        List<RegulationVO> regulationvolist = null;
        for (ResourcePolicyRefVO policy : policylist) {
            SimpleQuery<RegulationVO> regulationvoquery = dbf.createQuery(RegulationVO.class);
            regulationvoquery.add(RegulationVO_.policyUuid, SimpleQuery.Op.EQ, policy.getPolicyUuid());
            regulationvolist = regulationvoquery.list();
            for (RegulationVO regulationvo : regulationvolist) {
                rule = new Rule();
                rule.setAlarm_rule_id(regulationvo.getUuid());
                rule.setStrategy_type(regulationvo.getMonitorTargetVO().getTargetValue());
                rule.setOp(regulationvo.getComparisonRuleVO().getComparisonValue());
                rule.setRight_value(String.valueOf(regulationvo.getAlarmThreshold()));
                rule.setStay_time(regulationvo.getTriggerPeriod());
                rulelist.add(rule);
            }
        }

        tunnelparameter.setRules(rulelist);
        tunnelparameterlist.add(tunnelparameter);

        String url = CoreGlobalProperty.FALCON_API_IP + ":" + CoreGlobalProperty.FALCON_API_PORT +
                FalconApiRestConstant.STRATEGY_SYNC;
        String commandParam = JSONObjectUtil.toJsonString(tunnelparameterlist);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.setContentLength(commandParam.length());
        HttpEntity<String> req = new HttpEntity<>(commandParam, requestHeaders);
        ResponseEntity<FalconApiCommands.RestResponse> rsp = restf.getRESTTemplate().postForEntity(url, req, FalconApiCommands.RestResponse.class);
        FalconApiCommands.RestResponse res = rsp.getBody();

        if (!res.isSuccess()) {
            System.out.println(rsp.getBody());
            throw new OperationFailureException(Platform.operr("falcon fail "));
        }
        return true;
    }

    private void handle(APIGetMonitorTargetListMsg msg) {
        SimpleQuery<MonitorTargetVO> query = dbf.createQuery(MonitorTargetVO.class);
        query.add(MonitorTargetVO_.productType, SimpleQuery.Op.EQ, msg.getType());
        List<MonitorTargetVO> monitorTargetVOS = query.list();
        APIGetMonitorTargeListReply reply = new APIGetMonitorTargeListReply();
        reply.setInventories(MonitorTargetInventory.valueOf(monitorTargetVOS));
        bus.reply(msg, reply);
    }

    private void handle(APIGetComparisonRuleListMsg msg) {
        APIGetComparisonRuleListReply reply = new APIGetComparisonRuleListReply();
        reply.setInventories(ComparisonRuleInventory.valueOf(dbf.listAll(ComparisonRuleVO.class)));
        bus.reply(msg, reply);
    }

    private void handle(APIUpdateTunnelInfoForFalconMsg msg) {

        TunnelParameter tunnelparameter = new TunnelParameter();
        tunnelparameter.setTunnel_id(msg.getTunnel_id());
        tunnelparameter.setUser_id(msg.getSession().getAccountUuid());
        tunnelparameter.setEndpointA_vid(msg.getEndpointA_vid());
        tunnelparameter.setEndpointB_vid(msg.getEndpointB_vid());
        tunnelparameter.setEndpointA_ip(msg.getEndpointA_ip());
        tunnelparameter.setEndpointB_ip(msg.getEndpointB_ip());
        tunnelparameter.setBandwidth(msg.getBandwidth());

        List<Rule> rulelist = rulelist = new ArrayList<>();

        List<TunnelParameter> tunnelparameterlist = new ArrayList<>();
        List<RegulationVO> regulationvolist = null;
        Rule rule = null;

        SimpleQuery<ResourcePolicyRefVO> policyrquery = dbf.createQuery(ResourcePolicyRefVO.class);
        policyrquery.add(ResourcePolicyRefVO_.resourceUuid, SimpleQuery.Op.EQ, msg.getTunnel_id());
        List<ResourcePolicyRefVO> policylist = policyrquery.list();
        for (ResourcePolicyRefVO policy : policylist) {
            SimpleQuery<RegulationVO> regulationvoquery = dbf.createQuery(RegulationVO.class);
            regulationvoquery.add(RegulationVO_.policyUuid, SimpleQuery.Op.EQ, policy.getPolicyUuid());
            regulationvolist = regulationvoquery.list();
            for (RegulationVO regulationvo : regulationvolist) {
                rule = new Rule();
                rule.setOp(regulationvo.getComparisonRuleVO().getComparisonValue());
                rule.setStrategy_type(regulationvo.getMonitorTargetVO().getTargetValue());
                rule.setRight_value(String.valueOf(regulationvo.getAlarmThreshold()));
                rule.setAlarm_rule_id(regulationvo.getUuid());
                rule.setStay_time(regulationvo.getTriggerPeriod());
                rulelist.add(rule);
            }
        }

        tunnelparameter.setRules(rulelist);
        tunnelparameterlist.add(tunnelparameter);

        String url = CoreGlobalProperty.FALCON_API_IP + ":" + CoreGlobalProperty.FALCON_API_PORT +
                FalconApiRestConstant.STRATEGY_SYNC;
        String commandParam = JSONObjectUtil.toJsonString(tunnelparameterlist);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.setContentLength(commandParam.length());
        HttpEntity<String> req = new HttpEntity<>(commandParam, requestHeaders);
        ResponseEntity<FalconApiCommands.RestResponse> rsp = restf.getRESTTemplate().postForEntity(url, req, FalconApiCommands.RestResponse.class);
        FalconApiCommands.RestResponse res = rsp.getBody();
        if (!res.isSuccess()) {
            System.out.println(rsp.getBody());
            throw new OperationFailureException(Platform.operr("falcon fail "));
        }

        APIUpdateTunnelInfoForFalconReply reply = new APIUpdateTunnelInfoForFalconReply();
        bus.reply(msg, reply);

    }

    private void handle(APIDeleteResourceMsg msg) {


        String url = CoreGlobalProperty.FALCON_API_IP + ":" + CoreGlobalProperty.FALCON_API_PORT;
        Map<String, String> tunnelIdMap = new HashMap<>();
        tunnelIdMap.put("tunnel_id", msg.getTunnel_id());
        FalconApiCommands.RestResponse response = new FalconApiCommands.RestResponse();
        try {
            response = restf.syncJsonPost(url+ FalconApiRestConstant.STRATEGY_DELETE,JSONObjectUtil.toJsonString(tunnelIdMap),FalconApiCommands.RestResponse.class);
        }catch (Exception e){
            e.printStackTrace();
            response.setSuccess(false);
            response.setMsg(String.format("unable to post %s. %s", url, e.getMessage()));
        }

        APIDeleteResourceEvent evt = new APIDeleteResourceEvent(msg.getId());
        evt.setSuccess(response.isSuccess());
        if(!response.isSuccess()){
            evt.setError(Platform.operr(response.getMsg()));
        }

        if(response.isSuccess()){
            UpdateQuery q = UpdateQuery.New(ResourcePolicyRefVO.class);
            q.condAnd(ResourcePolicyRefVO_.resourceUuid, SimpleQuery.Op.EQ, msg.getTunnel_id());
            q.delete();

        }
        bus.publish(evt);

    }

    private String getProductUrl(ProductType productType) {
        String productServerUrl = AlarmGlobalProperty.TUNNEL_SERVER_RUL;
        switch (productType) {
            case TUNNEL:
                productServerUrl = AlarmGlobalProperty.TUNNEL_SERVER_RUL;
                break;
            case VPN:
                productServerUrl = "";
                break;
        }
        return productServerUrl;
    }

    @Transactional
    private void handle(APIDeletePolicyMsg msg) {
        SimpleQuery<ResourcePolicyRefVO> query = dbf.createQuery(ResourcePolicyRefVO.class);
        query.add(ResourcePolicyRefVO_.policyUuid, SimpleQuery.Op.EQ,msg.getUuid());
        List<ResourcePolicyRefVO> list = query.list();
        if(list!=null && list.size()>0){
            throw new IllegalArgumentException("the policy can remove till no resource bind to this policy");
        }
        PolicyVO vo = dbf.findByUuid(msg.getUuid(), PolicyVO.class);
        APIDeletePolicyEvent event = new APIDeletePolicyEvent(msg.getId());
        if (vo != null) {
            dbf.remove(vo);
        }
        event.setInventory(PolicyInventory.valueOf(vo));
        bus.publish(event);
    }

    private void handle(APIUpdatePolicyMsg msg) {
        PolicyVO vo = dbf.findByUuid(msg.getUuid(), PolicyVO.class);
        if (vo != null) {
            if (msg.getName() != null) {
                vo.setName(msg.getName());
            }
            if (msg.getDescription() != null) {
                vo.setDescription(vo.getDescription());
            }
        }
        dbf.updateAndRefresh(vo);
        APIUpdatePolicyEvent event = new APIUpdatePolicyEvent(msg.getId());
        event.setInventory(PolicyInventory.valueOf(vo));
        bus.publish(event);
    }

    @Transactional()
    private void handle(APIDeleteRegulationMsg msg) {
        RegulationVO regulationVO = dbf.findByUuid(msg.getUuid(), RegulationVO.class);
        APIDeleteRegulationEvent event = new APIDeleteRegulationEvent(msg.getId());
        if (regulationVO != null) {
            dbf.remove(regulationVO);
        }
        if(!updateFalcon(msg.getSession(), regulationVO.getPolicyUuid())){
//            throw new OperationFailureException(Platform.operr("资源不存在！"));
        }

        bus.publish(event);
    }

    @Transactional
    private void handle(APIUpdateRegulationMsg msg) {

        RegulationVO regulationVO = dbf.findByUuid(msg.getUuid(), RegulationVO.class);
        if (msg.getAlarmThreshold() != regulationVO.getAlarmThreshold()) {
            regulationVO.setAlarmThreshold(msg.getAlarmThreshold());
        }
        if (msg.getComparisonRuleUuid() != null) {
            regulationVO.setComparisonRuleVO(dbf.findByUuid(msg.getComparisonRuleUuid(), ComparisonRuleVO.class));
        }
        if (msg.getDetectPeriod() != regulationVO.getDetectPeriod()) {
            regulationVO.setDetectPeriod(msg.getDetectPeriod());
        }
        if (msg.getTriggerPeriod() != regulationVO.getTriggerPeriod()) {
            regulationVO.setTriggerPeriod(msg.getTriggerPeriod());
        }
        dbf.updateAndRefresh(regulationVO);

        if(!updateFalcon(msg.getSession(), regulationVO.getPolicyUuid())){
            throw new OperationFailureException(Platform.operr("资源不存在！"));
        }

        APIUpdateRegulationEvent event = new APIUpdateRegulationEvent(msg.getId());
        event.setInventory(RegulationInventory.valueOf(regulationVO));
        bus.publish(event);
    }

    private void handle(APIGetRegulationByPolicyMsg msg) {
        SimpleQuery<RegulationVO> query = dbf.createQuery(RegulationVO.class);
        query.add(RegulationVO_.policyUuid, SimpleQuery.Op.EQ, msg.getPolicyUuid());
        List<RegulationVO> regulationVOList = query.list();
        APIGetRegulationByPolicyReply reply = new APIGetRegulationByPolicyReply();
        reply.setInventories(RegulationInventory.valueOf(regulationVOList));
        bus.reply(msg, reply);
    }

    @Transactional
    private void handle(APICreateRegulationMsg msg) {
        SimpleQuery<RegulationVO> query = dbf.createQuery(RegulationVO.class);
        query.add(RegulationVO_.policyUuid, SimpleQuery.Op.EQ,msg.getPolicyUuid());
        long count = query.count();
        if(count>5){
            throw new IllegalArgumentException("every policy can build 5 regulations");
        }
        RegulationVO regulationVO = new RegulationVO();
        regulationVO.setUuid(Platform.getUuid());
        regulationVO.setAlarmThreshold(msg.getAlarmThreshold());
        regulationVO.setComparisonRuleVO(dbf.findByUuid(msg.getComparisonRuleUuid(), ComparisonRuleVO.class));
        regulationVO.setMonitorTargetVO(dbf.findByUuid(msg.getMonitorTargetUuid(), MonitorTargetVO.class));
        regulationVO.setDetectPeriod(msg.getDetectPeriod());
        regulationVO.setPolicyUuid(msg.getPolicyUuid());
        regulationVO.setTriggerPeriod(msg.getTriggerPeriod());
        dbf.persistAndRefresh(regulationVO);

        if(!updateFalcon(msg.getSession(), regulationVO.getPolicyUuid())){
//            throw new OperationFailureException(Platform.operr("资源不存在！"));
        }

        APICreateRegulationEvent event = new APICreateRegulationEvent(msg.getId());
        event.setInventory(RegulationInventory.valueOf(regulationVO));
        bus.publish(event);
    }

    private void handle(APICreatePolicyMsg msg) {
        SimpleQuery<PolicyVO> query = dbf.createQuery(PolicyVO.class);
        query.add(PolicyVO_.accountUuid, SimpleQuery.Op.EQ,msg.getAccountUuid());
        query.add(PolicyVO_.productType, SimpleQuery.Op.EQ,msg.getProductType());
        long count = query.count();
        if(count>10){
            throw new IllegalArgumentException("everyone can build 10 policies on one productType");
        }

        PolicyVO policyVO = new PolicyVO();
        policyVO.setUuid(Platform.getUuid());
        policyVO.setName(msg.getName());
        policyVO.setDescription(msg.getDescription());
        policyVO.setProductType(msg.getProductType());
        policyVO.setAccountUuid(msg.getAccountUuid());
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

    private boolean updateFalcon(SessionInventory session, String policyUuid) {

        SimpleQuery<ResourcePolicyRefVO> resourcequery = dbf.createQuery(ResourcePolicyRefVO.class);
        resourcequery.add(ResourcePolicyRefVO_.policyUuid, SimpleQuery.Op.EQ, policyUuid);
        if (resourcequery.isExists()) {
            TunnelParameter tunnelparameter = null;
            Rule rule = null;
            List<Rule> rulelist = null;
            List<TunnelParameter> tunnelparameterlist = new ArrayList<>();
            List<RegulationVO> regulationvolist = null;
            List<ResourcePolicyRefVO> resourcelist = resourcequery.list();

            List<String> prameterlist = new ArrayList<>();
            for (ResourcePolicyRefVO rvo : resourcelist) {
                prameterlist.add(rvo.getResourceUuid());
            }

            Map<String, Object> map = getTunnelInfo(prameterlist);

            for (ResourcePolicyRefVO resource : resourcelist) {
                tunnelparameter = new TunnelParameter();
                tunnelparameter.setUser_id(session.getAccountUuid());
                tunnelparameter.setTunnel_id(resource.getResourceUuid());

                if(map == null || map.get(resource.getResourceUuid()) == null){
                    return false;
                }else{
                    FalconApiCommands.Tunnel tunnel = (FalconApiCommands.Tunnel)map.get(resource.getResourceUuid());
                    tunnelparameter.setEndpointA_vid(tunnel.getEndpointA_vid());
                    tunnelparameter.setEndpointB_vid(tunnel.getEndpointB_vid());
                    tunnelparameter.setEndpointA_ip(tunnel.getEndpointA_ip());
                    tunnelparameter.setEndpointB_ip(tunnel.getEndpointB_ip());
                    tunnelparameter.setBandwidth(tunnel.getBandwidth());
                }

//                tunnelparameter.setEndpointA_vid(192264588);
//                tunnelparameter.setEndpointB_vid(192264588);
//                tunnelparameter.setEndpointA_ip("192264588");
//                tunnelparameter.setEndpointB_ip("192264588");
//                tunnelparameter.setBandwidth(1922L);

                rulelist = new ArrayList<>();
                SimpleQuery<ResourcePolicyRefVO> policyrquery = dbf.createQuery(ResourcePolicyRefVO.class);
                policyrquery.add(ResourcePolicyRefVO_.resourceUuid, SimpleQuery.Op.EQ, resource.getResourceUuid());
                List<ResourcePolicyRefVO> policylist = policyrquery.list();
                for (ResourcePolicyRefVO policy : policylist) {
                    SimpleQuery<RegulationVO> regulationvoquery = dbf.createQuery(RegulationVO.class);
                    regulationvoquery.add(RegulationVO_.policyUuid, SimpleQuery.Op.EQ, policy.getPolicyUuid());
                    regulationvolist = regulationvoquery.list();
                    for (RegulationVO regulationvo : regulationvolist) {
                        rule = new Rule();
                        rule.setAlarm_rule_id(regulationvo.getUuid());
                        rule.setRight_value(String.valueOf(regulationvo.getAlarmThreshold()));
                        rule.setStay_time(regulationvo.getTriggerPeriod());
                        rule.setOp(regulationvo.getComparisonRuleVO().getComparisonValue());
                        rule.setStrategy_type(regulationvo.getMonitorTargetVO().getTargetValue());
                        rulelist.add(rule);
                    }
                }

                tunnelparameter.setRules(rulelist);
                tunnelparameterlist.add(tunnelparameter);
            }

            try {
                String url = CoreGlobalProperty.FALCON_API_IP + ":" + CoreGlobalProperty.FALCON_API_PORT +
                        FalconApiRestConstant.STRATEGY_SYNC;
                FalconApiCommands.RestResponse response = new FalconApiCommands.RestResponse();
                try {
                    response = restf.syncJsonPost(url, JSONObjectUtil.toJsonString(JSONObjectUtil.toJsonString(tunnelparameterlist)), FalconApiCommands.RestResponse.class);
                } catch (Exception e) {
                    response.setSuccess(false);
                    response.setMsg(String.format("unable to post %s. %s", url, e.getMessage()));
                }

                if (!response.isSuccess()) {
                    System.out.println(response);
                    throw new OperationFailureException(Platform.operr("falcon fail "));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return true;
    }

    private Map getTunnelInfo(List<String> Resources){

        APIQueryTunnelDetailForAlarmMsg tunnelMsg = new APIQueryTunnelDetailForAlarmMsg();
        tunnelMsg.setTunnelUuidList(Resources);

        try{
            RestAPIResponse raps = restf.syncJsonPost(AlarmGlobalProperty.TUNNEL_SERVER_RUL,
                    RESTApiDecoder.dump(tunnelMsg), RestAPIResponse.class);

            if (raps.getState().equals(RestAPIState.Done.toString())) {
                APIQueryTunnelDetailForAlarmReply tunnelReply = JSONObjectUtil.toObject(raps.getResult(),
                        APIQueryTunnelDetailForAlarmReply.class);
                if(tunnelReply.isSuccess()){
                    return tunnelReply.getMap();
                }else{
                    throw new OperationFailureException(Platform.operr(tunnelReply.getError().toString()));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
