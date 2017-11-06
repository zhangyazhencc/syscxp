package com.syscxp.alarm.resourcePolicy;

import com.syscxp.alarm.AlarmGlobalProperty;
import com.syscxp.alarm.header.resourcePolicy.*;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.DbEntityLister;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.db.UpdateQuery;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.identity.InnerMessageHelper;
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
import com.syscxp.header.identity.AccountType;
import com.syscxp.header.identity.SessionInventory;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.query.QueryCondition;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.RestAPIResponse;
import com.syscxp.header.rest.RestAPIState;
import com.syscxp.header.tunnel.*;
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

import javax.persistence.Query;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

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
        } else if (msg instanceof APICreateRegulationMsg) {
            handle((APICreateRegulationMsg) msg);
        } else if (msg instanceof APIGetRegulationByPolicyMsg) {
            handle((APIGetRegulationByPolicyMsg) msg);
        } else if (msg instanceof APIUpdateRegulationMsg) {
            handle((APIUpdateRegulationMsg) msg);
        } else if (msg instanceof APIDeleteRegulationMsg) {
            handle((APIDeleteRegulationMsg) msg);
        } else if (msg instanceof APIGetPoliciesMsg) {
            handle((APIGetPoliciesMsg) msg);
        } else if (msg instanceof APIUpdatePolicyMsg) {
            handle((APIUpdatePolicyMsg) msg);
        } else if (msg instanceof APIDeletePolicyMsg) {
            handle((APIDeletePolicyMsg) msg);
        } else if (msg instanceof APIGetResourcesBindByPolicyMsg) {
            handle((APIGetResourcesBindByPolicyMsg) msg);
        } else if (msg instanceof APIAttachPolicyByResourcesMsg) {
            handle((APIAttachPolicyByResourcesMsg) msg);
        } else if (msg instanceof APIAttachResourceByPoliciesMsg) {
            handle((APIAttachResourceByPoliciesMsg) msg);
        } else if (msg instanceof APIGetResourcesByProductTypeMsg) {
            handle((APIGetResourcesByProductTypeMsg) msg);
        } else if (msg instanceof APIDeleteResourceMsg) {
            handle((APIDeleteResourceMsg) msg);
        } else if (msg instanceof APIUpdateTunnelInfoForFalconMsg) {
            handle((APIUpdateTunnelInfoForFalconMsg) msg);
        }else if (msg instanceof APIGetPoliciesByResourceMsg) {
            handle((APIGetPoliciesByResourceMsg) msg);
        }

        else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIGetPoliciesByResourceMsg msg) {
        List<PolicyVO> policies = null;

        SimpleQuery<ResourcePolicyRefVO> query = dbf.createQuery(ResourcePolicyRefVO.class);
        query.add(ResourcePolicyRefVO_.resourceUuid, SimpleQuery.Op.EQ, msg.getResourceUuid());
        query.select(ResourcePolicyRefVO_.policyUuid);
        List<String> policyUuids =query.listValue();
        SimpleQuery.Op op = SimpleQuery.Op.IN;
        if(!msg.isBind()){
            op = SimpleQuery.Op.NOT_IN;
        }
        SimpleQuery<PolicyVO> q = dbf.createQuery(PolicyVO.class);
        q.add(PolicyVO_.uuid, op,policyUuids);
        q.add(PolicyVO_.accountUuid, op,msg.getAccountUuid());
        policies  = q .list();
        APIGetPoliciesByResourceReply reply = new APIGetPoliciesByResourceReply();
        if(policies!=null)reply.setInventories(PolicyInventory.valueOf(policies));
        bus.reply(msg,reply);
    }

    private void handle(APIUpdateTunnelInfoForFalconMsg msg) {

        TunnelParameter tunnelparameter = new TunnelParameter();
        tunnelparameter.setTunnel_id(msg.getTunnel_id());
        tunnelparameter.setUser_id(msg.getSession().getAccountUuid());
        tunnelparameter.setEndpointA_vid(msg.getEndpointA_vlan());
        tunnelparameter.setEndpointB_vid(msg.getEndpointZ_vlan());
        tunnelparameter.setEndpointA_ip(msg.getEndpointA_ip());
        tunnelparameter.setEndpointB_ip(msg.getEndpointZ_ip());
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

        String url = AlarmGlobalProperty.FALCON_URL_SAVE;
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
        bus.reply(msg,reply);

    }

    private void handle(APIDeleteResourceMsg msg) {

        String url = AlarmGlobalProperty.FALCON_URL_DELETE;

        Map<String,String> tunnelIdMap = new HashMap<>();
        tunnelIdMap.put("tunnel_id", msg.getTunnel_id());

        String commandParam = JSONObjectUtil.toJsonString(tunnelIdMap);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.setContentLength(commandParam.length());
        HttpEntity<String> req = new HttpEntity<String>(commandParam, requestHeaders);
        ResponseEntity<FalconApiCommands.RestResponse> rsp = restf.getRESTTemplate().postForEntity(url, req, FalconApiCommands.RestResponse.class);
        FalconApiCommands.RestResponse res = rsp.getBody();

        if (!res.isSuccess()) {
            System.out.println(rsp.getBody());
            throw new OperationFailureException(Platform.operr("falcon delete fail "));
        }

        APIDeleteResourceEvent evt = new APIDeleteResourceEvent();
        if(res.isSuccess()){
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

    private void handle(APIGetResourcesBindByPolicyMsg msg) {
        PolicyVO policyVo = dbf.findByUuid(msg.getPolicyUuid(), PolicyVO.class);
        String productServerUrl = getProductUrl(policyVo.getProductType());

        SimpleQuery.Op op = SimpleQuery.Op.IN;
        if (!msg.isBind()) {
            op = SimpleQuery.Op.NOT_IN;
        }

        SimpleQuery<ResourcePolicyRefVO> query = dbf.createQuery(ResourcePolicyRefVO.class);
        query.add(ResourcePolicyRefVO_.policyUuid, SimpleQuery.Op.EQ, msg.getPolicyUuid());
        List<ResourcePolicyRefVO> resourcePolicyRefVOS = query.list();
        query.select(ResourcePolicyRefVO_.resourceUuid);
        List<String> uuids = query.listValue();

        if (policyVo.getProductType() == ProductType.TUNNEL) {
            APIQueryTunnelForAlarmMsg aMsg = new APIQueryTunnelForAlarmMsg();
            aMsg.setAccountUuid(msg.getAccountUuid());
            List<TunnelForAlarmInventory> inventories = null;
            long count = 0;
            aMsg.setLimit(msg.getLimit());
            aMsg.setStart(msg.getStart());
            aMsg.setProductUuids(uuids);
            InnerMessageHelper.setMD5(aMsg);
            String gstr = RESTApiDecoder.dump(aMsg);
            try{
                RestAPIResponse rsp = restf.syncJsonPost(productServerUrl, gstr, RestAPIResponse.class);
                if (rsp.getState().equals(RestAPIState.Done.toString())) {
                    APIQueryTunnelForAlarmReply productReply = (APIQueryTunnelForAlarmReply) RESTApiDecoder.loads(rsp.getResult());//todo rename reply name and refactor field for other product
                    if (productReply instanceof APIQueryTunnelForAlarmReply) {
                        inventories = productReply.getInventories();
                        count = productReply.getCount();
                    }
                }
            }catch (Exception e){
                throw new RuntimeException("please check your network");
            }

            APIGetResourcesBindByPolicyReply reply = new APIGetResourcesBindByPolicyReply();
            reply.setCount(count);
            reply.setInventories(inventories);
            bus.reply(msg, reply);
        } else if (policyVo.getProductType() == ProductType.VPN) {

        }

    }

    private void handle(APIGetResourcesByProductTypeMsg msg) {

        String productServerUrl = getProductUrl(msg.getProductType());

        APIQueryTunnelForAlarmMsg aMsg = new APIQueryTunnelForAlarmMsg();
        List<ResourceInventory> inventories = new ArrayList<>();
        aMsg.setAccountUuid(msg.getAccountUuid());
        aMsg.setProductName(msg.getProductName());
        aMsg.setStart(msg.getStart());
        aMsg.setLimit(msg.getLimit());
        InnerMessageHelper.setMD5(aMsg);
        String gstr = RESTApiDecoder.dump(aMsg);
        long count = 0;
        RestAPIResponse rsp = restf.syncJsonPost(productServerUrl, gstr, RestAPIResponse.class);
        if (rsp.getState().equals(RestAPIState.Done.toString())) {
            APIQueryTunnelForAlarmReply productReply = (APIQueryTunnelForAlarmReply) RESTApiDecoder.loads(rsp.getResult());//todo rename reply name and refactor field for other product
            if (productReply instanceof APIQueryTunnelForAlarmReply) {
                List<TunnelForAlarmInventory> tunnelList = productReply.getInventories();
                count = productReply.getCount();
                for (TunnelForAlarmInventory inventory : tunnelList) {
                    ResourceInventory resourceInventory = new ResourceInventory();
                    resourceInventory.setAccountUuid(inventory.getAccountUuid());
                    resourceInventory.setProductUuid(inventory.getUuid());
                    resourceInventory.setProductType(msg.getProductType());
                    resourceInventory.setDescription(inventory.getDescription());
                    resourceInventory.setProductName(inventory.getName());
                    resourceInventory.setCreateDate(inventory.getCreateDate());
                    resourceInventory.setLastOpDate(inventory.getLastOpDate());
                    SimpleQuery<ResourcePolicyRefVO> query = dbf.createQuery(ResourcePolicyRefVO.class);
                    query.add(ResourcePolicyRefVO_.resourceUuid, SimpleQuery.Op.EQ, inventory.getUuid());
                    List<ResourcePolicyRefVO> resourcePolicyRefVOS = query.list();
                    List<PolicyInventory> policyInventories = new ArrayList<>();
                    for (ResourcePolicyRefVO resourcePolicyRefVO : resourcePolicyRefVOS) {
                        policyInventories.add(PolicyInventory.valueOf(dbf.findByUuid(resourcePolicyRefVO.getPolicyUuid(), PolicyVO.class)));
                    }
                    resourceInventory.setPolicies(policyInventories);
                    inventories.add(resourceInventory);
                }

            }
        }
        APIGetResourcesByProductTypeReply reply = new APIGetResourcesByProductTypeReply();
        reply.setInventories(inventories);
        reply.setCount(count);
        bus.reply(msg, reply);
    }

    @Transactional
    private void handle(APIAttachResourceByPoliciesMsg msg) {


        SimpleQuery<ResourcePolicyRefVO> query = dbf.createQuery(ResourcePolicyRefVO.class);
        query.add(ResourcePolicyRefVO_.resourceUuid, SimpleQuery.Op.EQ, msg.getResourceUuid());
        query.select(ResourcePolicyRefVO_.policyUuid);
        List<String> hadAttachPolicyUuids = query.listValue();
        List<String> newAttachPolicyUuids = msg.getPolicyUuids();

        List<PolicyVO> policyVOList = dbf.listByPrimaryKeys(hadAttachPolicyUuids,PolicyVO.class);
        List<PolicyInventory> list =  new ArrayList<>();
        if(!isEmptyList(policyVOList))list = PolicyInventory.valueOf(policyVOList);
        if(isEmptyList(hadAttachPolicyUuids) && isEmptyList(newAttachPolicyUuids)){
            throw new IllegalArgumentException("please select a policy");
        }

        if(isEmptyList(hadAttachPolicyUuids) && !isEmptyList(newAttachPolicyUuids)){
            attachPolcies(msg.getPolicyUuids(),msg.getResourceUuid(), list);
        } else if(!isEmptyList(hadAttachPolicyUuids) && isEmptyList(newAttachPolicyUuids)){
            List<String> needDettachPolicyUuids = substractList(hadAttachPolicyUuids,newAttachPolicyUuids);
            for (String policyUuid : needDettachPolicyUuids) {
                deleteResourcePolicyRef(policyUuid, msg.getResourceUuid());
                list.remove(PolicyInventory.valueOf(dbf.findByUuid(policyUuid,PolicyVO.class)));
            }
        } else {
            List<String> needDettachPolicyUuids = substractList(hadAttachPolicyUuids,newAttachPolicyUuids);
            for (String policyUuid : needDettachPolicyUuids) {
                deleteResourcePolicyRef(policyUuid, msg.getResourceUuid());
                list.remove(PolicyInventory.valueOf(dbf.findByUuid(policyUuid,PolicyVO.class)));
            }

            List<String> needAttachPolicyUuids = substractList(newAttachPolicyUuids,hadAttachPolicyUuids);
               attachPolcies(needAttachPolicyUuids,msg.getResourceUuid(), list);
        }
        dbf.getEntityManager().flush();

        APIQueryTunnelDetailForAlarmMsg tunnelMsg = new APIQueryTunnelDetailForAlarmMsg();
        List<String> lis = new ArrayList<>();
        lis.add(msg.getResourceUuid());
        tunnelMsg.setTunnelUuidList(lis);
        Map<String, Object> map = null;
        try{
            RestAPIResponse raps = restf.syncJsonPost(AlarmGlobalProperty.TUNNEL_SERVER_RUL,
                    RESTApiDecoder.dump(tunnelMsg), RestAPIResponse.class);

            if (raps.getState().equals(RestAPIState.Done.toString())) {
                APIQueryTunnelDetailForAlarmReply tunnelReply = JSONObjectUtil.toObject(raps.getResult(),APIQueryTunnelDetailForAlarmReply.class);
                if(tunnelReply.isSuccess()){
                    map = tunnelReply.getMap();
                }else{
                    throw new OperationFailureException(Platform.operr(tunnelReply.getError().toString()));
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        TunnelParameter tunnelparameter = new TunnelParameter();
        tunnelparameter.setTunnel_id(msg.getResourceUuid());
        tunnelparameter.setUser_id(msg.getSession().getAccountUuid());
        FalconApiCommands.Tunnel tunnel = (FalconApiCommands.Tunnel)map.get(msg.getResourceUuid());
        tunnelparameter.setEndpointA_vid(tunnel.getEndpointA_vid().toString());
        tunnelparameter.setEndpointB_vid(tunnel.getEndpointB_vid().toString());
        tunnelparameter.setEndpointA_ip(tunnel.getEndpointA_ip());
        tunnelparameter.setEndpointB_ip(tunnel.getEndpointB_ip());
        tunnelparameter.setBandwidth(tunnel.getBandwidth().toString());

        List<Rule> rulelist = rulelist = new ArrayList<>();

        List<TunnelParameter> tunnelparameterlist = new ArrayList<>();
        List<RegulationVO> regulationvolist = null;
        Rule rule = null;

        SimpleQuery<ResourcePolicyRefVO> policyrquery = dbf.createQuery(ResourcePolicyRefVO.class);
        policyrquery.add(ResourcePolicyRefVO_.resourceUuid, SimpleQuery.Op.EQ, msg.getResourceUuid());
        List<ResourcePolicyRefVO> policylist = policyrquery.list();
        for (ResourcePolicyRefVO policy : policylist) {
            SimpleQuery<RegulationVO> regulationvoquery = dbf.createQuery(RegulationVO.class);
            regulationvoquery.add(RegulationVO_.policyUuid, SimpleQuery.Op.EQ, policy.getPolicyUuid());
            regulationvolist = regulationvoquery.list();
            for (RegulationVO regulationvo : regulationvolist) {
                rule = new Rule();
                rule.setAlarm_rule_id(regulationvo.getUuid());
                rule.setOp(regulationvo.getComparisonRuleVO().getComparisonValue());
                rule.setStrategy_type(regulationvo.getMonitorTargetVO().getTargetValue());
                rule.setRight_value(String.valueOf(regulationvo.getAlarmThreshold()));
                rule.setStay_time(regulationvo.getTriggerPeriod());
                rulelist.add(rule);
            }
        }

        tunnelparameter.setRules(rulelist);
        tunnelparameterlist.add(tunnelparameter);

        try{
            FalconApiCommands.RestResponse response = new FalconApiCommands.RestResponse();
            String url = AlarmGlobalProperty.FALCON_URL_SAVE;
            try {
                response = restf.syncJsonPost(url,JSONObjectUtil.toJsonString(JSONObjectUtil.toJsonString(tunnelparameterlist)),FalconApiCommands.RestResponse.class);
            }catch (Exception e){
                response.setSuccess(false);
                response.setMsg(String.format("unable to post %s. %s", url, e.getMessage()));
            }

            if (!response.isSuccess()) {
                System.out.println(response);
                throw new OperationFailureException(Platform.operr("falcon fail "));
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        APIAttachResourceByPoliciesEvent event = new APIAttachResourceByPoliciesEvent(msg.getId());
        event.setInventories(list);
        bus.publish(event);
    }

    private void attachPolcies(List<String> policyUuids,String resourceUuid, List<PolicyInventory> list) {
        for (String policyUuid : policyUuids) {
            ResourcePolicyRefVO resourcePolicyRefVO = new ResourcePolicyRefVO();
            resourcePolicyRefVO.setPolicyUuid(policyUuid);
            resourcePolicyRefVO.setResourceUuid(resourceUuid);
            resourcePolicyRefVO.setUuid(Platform.getUuid());
            dbf.getEntityManager().persist(resourcePolicyRefVO);
            list.add(PolicyInventory.valueOf(dbf.findByUuid(policyUuid,PolicyVO.class)));
        }
    }

    @Transactional
    private ResourcePolicyRefVO deleteResourcePolicyRef(String policyUuid, String resourceUuid) {
        SimpleQuery<ResourcePolicyRefVO> query = dbf.createQuery(ResourcePolicyRefVO.class);
        query.add(ResourcePolicyRefVO_.policyUuid, SimpleQuery.Op.EQ, policyUuid);
        query.add(ResourcePolicyRefVO_.resourceUuid, SimpleQuery.Op.EQ, resourceUuid);
        ResourcePolicyRefVO resourcePolicyRefVO = query.find();
        if (resourcePolicyRefVO != null) {
            dbf.getEntityManager().remove(dbf.getEntityManager().merge(resourcePolicyRefVO));
        }
        return resourcePolicyRefVO;
    }

    @Transactional
    private void handle(APIAttachPolicyByResourcesMsg msg) {
        List<ResourcePolicyRefInventory> list = new ArrayList<>();
        if (msg.isAttach()) {
            for (String resourceUuid : msg.getResourceUuids()) {
                ResourcePolicyRefVO resourcePolicyRefVO = new ResourcePolicyRefVO();
                resourcePolicyRefVO.setUuid(Platform.getUuid());
                resourcePolicyRefVO.setPolicyUuid(msg.getPolicyUuid());
                resourcePolicyRefVO.setResourceUuid(resourceUuid);
                dbf.getEntityManager().persist(resourcePolicyRefVO);
                list.add(ResourcePolicyRefInventory.valueOf(resourcePolicyRefVO));
            }
        } else {
            for (String resourceUuid : msg.getResourceUuids()) {
                list.add(ResourcePolicyRefInventory.valueOf(deleteResourcePolicyRef(msg.getPolicyUuid(), resourceUuid)));
            }
        }


        TunnelParameter tunnelparameter = null;
        Rule rule = null;
        List<Rule> rulelist = null;
        List<TunnelParameter> tunnelparameterlist = new ArrayList<>();
        List<RegulationVO> regulationvolist = null;

        APIQueryTunnelDetailForAlarmMsg tunnelMsg = new APIQueryTunnelDetailForAlarmMsg();
        tunnelMsg.setTunnelUuidList(msg.getResourceUuids());
        Map<String, Object> map = null;
        try{
            RestAPIResponse raps = restf.syncJsonPost(AlarmGlobalProperty.TUNNEL_SERVER_RUL,
                    RESTApiDecoder.dump(tunnelMsg), RestAPIResponse.class);

            if (raps.getState().equals(RestAPIState.Done.toString())) {
                APIQueryTunnelDetailForAlarmReply tunnelReply = JSONObjectUtil.toObject(raps.getResult(),APIQueryTunnelDetailForAlarmReply.class);
                if(tunnelReply.isSuccess()){
                    map = tunnelReply.getMap();
                }else{
                    throw new OperationFailureException(Platform.operr(tunnelReply.getError().toString()));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        for (String resourceid : msg.getResourceUuids()) {
            tunnelparameter.setUser_id(msg.getSession().getAccountUuid());
            tunnelparameter.setTunnel_id(resourceid);
            FalconApiCommands.Tunnel tunnel = (FalconApiCommands.Tunnel)map.get(resourceid);
            tunnelparameter.setEndpointA_vid(tunnel.getEndpointA_vid().toString());
            tunnelparameter.setEndpointB_vid(tunnel.getEndpointB_vid().toString());
            tunnelparameter.setEndpointA_ip(tunnel.getEndpointA_ip());
            tunnelparameter.setEndpointB_ip(tunnel.getEndpointB_ip());
            tunnelparameter.setBandwidth(tunnel.getBandwidth().toString());

            rulelist = new ArrayList<>();
            SimpleQuery<ResourcePolicyRefVO> policyrquery = dbf.createQuery(ResourcePolicyRefVO.class);
            policyrquery.add(ResourcePolicyRefVO_.resourceUuid, SimpleQuery.Op.EQ, resourceid);
            List<ResourcePolicyRefVO> policylist = policyrquery.list();
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
        }

        String url = AlarmGlobalProperty.FALCON_URL_SAVE;
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


        APIAttachPolicyByResourcesEvent event = new APIAttachPolicyByResourcesEvent(msg.getId());
        event.setInventories(list);
        bus.publish(event);

    }

    @Transactional
    private void handle(APIDeletePolicyMsg msg) {
        PolicyVO vo = dbf.findByUuid(msg.getUuid(), PolicyVO.class);
        if (vo != null) {
            dbf.remove(vo);
            updateFalcon(msg.getSession(), vo.getUuid());
            dbf.removeCollection(dbf.createQuery(ResourcePolicyRefVO.class)
                    .add(ResourcePolicyRefVO_.policyUuid,SimpleQuery.Op.EQ,vo.getUuid()).list(),ResourcePolicyRefVO.class);

        }

        APIDeletePolicyEvent event = new APIDeletePolicyEvent(msg.getId());
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

    private void handle(APIGetPoliciesMsg msg) {
        SimpleQuery<PolicyVO> query = dbf.createQuery(PolicyVO.class);
        if(msg.getSession().getType()!= AccountType.SystemAdmin){
            query.add(PolicyVO_.accountUuid, SimpleQuery.Op.EQ, msg.getSession().getAccountUuid());
        }else if(!StringUtils.isEmpty(msg.getAccountUuid())){
            query.add(PolicyVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        }
        List<QueryCondition> conditions = msg.getConditions();
        if (conditions != null && conditions.size() > 0) {
            for (QueryCondition condition : conditions) {
                if (!StringUtils.isEmpty(condition.getName())) {
                    if (condition.getName().equals("productType")) {
                        query.add(PolicyVO_.productType, SimpleQuery.Op.EQ, condition.getValue());
                    } else if (condition.getName().equals("bindResources")) {
                        String sql = "select policyUuid, count(*) as bindingResources from ResourcePolicyRefVO group by policyUuid ";
                        Query q = dbf.getEntityManager().createNativeQuery(sql);
                        List<Object[]> objs = q.getResultList();
                        List<PolicyBindResource> vos = objs.stream().map(PolicyBindResource::new).collect(Collectors.toList());
                        List<String> uuids = new ArrayList<>();
                        for (PolicyBindResource p : vos) {
                            if (p.getBindingResources().compareTo(new BigInteger(condition.getValue()))==0){
                                uuids.add(p.getPolicyUuid());
                            }

                        }
                        if(uuids.size()==0){
                            throw new IllegalArgumentException("there is no one suitable");
                        }
                        query.add(PolicyVO_.uuid, SimpleQuery.Op.IN, uuids);
                    } else{

                    }

                }
            }
        }
        long count = query.count();
        query.setLimit(msg.getLimit());
        query.setStart(msg.getStart());

        List<PolicyVO> policyVOS = query.list();
        for (PolicyVO vo : policyVOS) {
            SimpleQuery<ResourcePolicyRefVO> dbfQuery = dbf.createQuery(ResourcePolicyRefVO.class);
            dbfQuery.add(ResourcePolicyRefVO_.policyUuid, SimpleQuery.Op.EQ, vo.getUuid());
            vo.setBindResources(dbfQuery.count());
        }
        APIGetPoliciesReply reply = new APIGetPoliciesReply();
        reply.setInventories(PolicyInventory.valueOf(policyVOS));
        reply.setCount(count);
        bus.reply(msg, reply);

    }

    @Transactional
    private void handle(APIDeleteRegulationMsg msg) {
        RegulationVO regulationVO = dbf.findByUuid(msg.getUuid(), RegulationVO.class);
        if (regulationVO != null) {
            dbf.remove(regulationVO);
        }

        updateFalcon(msg.getSession(), regulationVO.getPolicyUuid());

        APIDeleteRegulationEvent event = new APIDeleteRegulationEvent(msg.getId());
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

        updateFalcon(msg.getSession(), regulationVO.getPolicyUuid());

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
        bus.reply(msg, reply);
    }

    @Transactional
    private void handle(APICreateRegulationMsg msg) {
        RegulationVO regulationVO = new RegulationVO();
        regulationVO.setUuid(Platform.getUuid());
        regulationVO.setAlarmThreshold(msg.getAlarmThreshold());
        regulationVO.setComparisonRuleVO(dbf.findByUuid(msg.getComparisonRuleUuid(), ComparisonRuleVO.class));
        regulationVO.setMonitorTargetVO(dbf.findByUuid(msg.getMonitorTargetUuid(), MonitorTargetVO.class));
        regulationVO.setDetectPeriod(msg.getDetectPeriod());
        regulationVO.setPolicyUuid(msg.getPolicyUuid());
        regulationVO.setTriggerPeriod(msg.getTriggerPeriod());
        dbf.persistAndRefresh(regulationVO);

        updateFalcon(msg.getSession(), msg.getPolicyUuid());

        APICreateRegulationEvent event = new APICreateRegulationEvent(msg.getId());
        event.setInventory(RegulationInventory.valueOf(regulationVO));
        bus.publish(event);
    }

    private void handle(APICreatePolicyMsg msg) {
        PolicyVO policyVO = new PolicyVO();
        policyVO.setUuid(Platform.getUuid());
        policyVO.setName(msg.getName());
        policyVO.setDescription(msg.getDescription());
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
            for(ResourcePolicyRefVO rvo : resourcelist){
                prameterlist.add(rvo.getResourceUuid());
            }
            APIQueryTunnelDetailForAlarmMsg tunnelMsg = new APIQueryTunnelDetailForAlarmMsg();
            tunnelMsg.setTunnelUuidList(prameterlist);
            tunnelMsg.setSession(session);
            Map<String, Object> map = null;
            try{
                RestAPIResponse raps = restf.syncJsonPost(AlarmGlobalProperty.TUNNEL_SERVER_RUL,
                        RESTApiDecoder.dump(tunnelMsg), RestAPIResponse.class);

                if (raps.getState().equals(RestAPIState.Done.toString())) {
                    APIQueryTunnelDetailForAlarmReply tunnelReply = JSONObjectUtil.toObject(raps.getResult(),APIQueryTunnelDetailForAlarmReply.class);
                    if(tunnelReply.isSuccess()){
                        map = tunnelReply.getMap();
                    }else{
                        throw new OperationFailureException(Platform.operr(tunnelReply.getError().toString()));
                    }

                }
            }catch (Exception e){
                e.printStackTrace();
            }



            for (ResourcePolicyRefVO resource : resourcelist) {
                tunnelparameter = new TunnelParameter();
                tunnelparameter.setUser_id(session.getAccountUuid());
                tunnelparameter.setTunnel_id(resource.getResourceUuid());

                FalconApiCommands.Tunnel tunnel = (FalconApiCommands.Tunnel)map.get(resource.getResourceUuid());
                tunnelparameter.setEndpointA_vid(tunnel.getEndpointA_vid().toString());
                tunnelparameter.setEndpointB_vid(tunnel.getEndpointB_vid().toString());
                tunnelparameter.setEndpointA_ip(tunnel.getEndpointA_ip());
                tunnelparameter.setEndpointB_ip(tunnel.getEndpointB_ip());
                tunnelparameter.setBandwidth(tunnel.getBandwidth().toString());

//                tunnelparameter.setEndpointA_vid("192264588");
//                tunnelparameter.setEndpointB_vid("192264588");
//                tunnelparameter.setEndpointA_ip("192264588");
//                tunnelparameter.setEndpointB_ip("192264588");
//                tunnelparameter.setBandwidth("192264588");


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

            try{
                String url = AlarmGlobalProperty.FALCON_URL_SAVE;
                FalconApiCommands.RestResponse response = new FalconApiCommands.RestResponse();
                try {
                    response = restf.syncJsonPost(url,JSONObjectUtil.toJsonString(JSONObjectUtil.toJsonString(tunnelparameterlist)),FalconApiCommands.RestResponse.class);
                }catch (Exception e){
                    response.setSuccess(false);
                    response.setMsg(String.format("unable to post %s. %s", url, e.getMessage()));
                }

                if (!response.isSuccess()) {
                    System.out.println(response);
                    throw new OperationFailureException(Platform.operr("falcon fail "));
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return true;
    }


    private  <T>  boolean isEmptyList(Collection<T> c){
        return c==null ||c.size()==0;
    }

    private <T> Map<T,T> list2Map(List<T> list){
        Map<T,T> map = new HashMap<>();
        for(T t : list){
            map.put(t,t);
        }
        return map;
    }

    private <T> List<T> substractList(List<T> c1,List<T> c2){
        if(isEmptyList(c1)) return null;
        if(isEmptyList(c2)) return c1;
        Map<T,T> map2 = list2Map(c2);
        List<T> resultList = new ArrayList<>();
        for(T t: c1){
            if(map2.get(t)==null){
                resultList.add(t);
            }
        }
        return resultList;

    }


}
