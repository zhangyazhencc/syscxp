package com.syscxp.alarm.resourcePolicy;

import com.syscxp.alarm.AlarmGlobalProperty;
import com.syscxp.alarm.header.resourcePolicy.*;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.DbEntityLister;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.identity.InnerMessageHelper;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.header.AbstractService;
import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.billing.ProductType;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.query.QueryCondition;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.RestAPIResponse;
import com.syscxp.header.rest.RestAPIState;
import com.syscxp.header.tunnel.APIQueryTunnelForAlarmMsg;
import com.syscxp.header.tunnel.APIQueryTunnelForAlarmReply;
import com.syscxp.header.tunnel.TunnelForAlarmInventory;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
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
        }else if (msg instanceof APICreateRegulationMsg) {
            handle((APICreateRegulationMsg) msg);
        }else if (msg instanceof APIGetRegulationByPolicyMsg) {
            handle((APIGetRegulationByPolicyMsg) msg);
        }else if (msg instanceof APIUpdateRegulationMsg) {
            handle((APIUpdateRegulationMsg) msg);
        }else if (msg instanceof APIDeleteRegulationMsg) {
            handle((APIDeleteRegulationMsg) msg);
        }else if (msg instanceof APIGetPoliciesMsg) {
            handle((APIGetPoliciesMsg) msg);
        }else if (msg instanceof APIUpdatePolicyMsg) {
            handle((APIUpdatePolicyMsg) msg);
        }else if (msg instanceof APIDeletePolicyMsg) {
            handle((APIDeletePolicyMsg) msg);
        }else if (msg instanceof APIGetResourcesBindByPolicyMsg) {
            handle((APIGetResourcesBindByPolicyMsg) msg);
        }else if (msg instanceof APIAttachPolicyByResourcesMsg) {
            handle((APIAttachPolicyByResourcesMsg) msg);
        }else if (msg instanceof APIAttachResourceByPoliciesMsg) {
            handle((APIAttachResourceByPoliciesMsg) msg);
        }else if (msg instanceof APIGetResourcesByProductTypeMsg) {
            handle((APIGetResourcesByProductTypeMsg) msg);
        }else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private String getProductUrl(ProductType productType){
        String productServerUrl = AlarmGlobalProperty.TUNNEL_SERVER_RUL;
        switch (productType){
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
        PolicyVO policyVo = dbf.findByUuid(msg.getPolicyUuid(),PolicyVO.class);
        String productServerUrl = getProductUrl( policyVo.getProductType());

        SimpleQuery.Op op = SimpleQuery.Op.IN;
        if(!msg.isBind()){
            op = SimpleQuery.Op.NOT_IN;
        }

        SimpleQuery<ResourcePolicyRefVO> query = dbf.createQuery(ResourcePolicyRefVO.class);
        query.add(ResourcePolicyRefVO_.policyUuid, SimpleQuery.Op.EQ, msg.getPolicyUuid());
        List<ResourcePolicyRefVO> resourcePolicyRefVOS =query.list();
        String[] values = new String[resourcePolicyRefVOS.size()];
        for(int i = 0; i<resourcePolicyRefVOS.size();i++){
            values[i] = resourcePolicyRefVOS.get(i).getPolicyUuid();
        }

        if (policyVo.getProductType()==ProductType.TUNNEL){
            APIQueryTunnelForAlarmMsg aMsg = new APIQueryTunnelForAlarmMsg();
            List<TunnelForAlarmInventory> inventories = null;
            long count = 0;
            QueryCondition condition = new QueryCondition();
            condition.setName("status");
            condition.setOp("=");
            condition.setValue("Connected");
            QueryCondition condition2 = new QueryCondition();
            condition2.setName("uuid");
            condition2.setOp(op.name());
            condition2.setValues( values);
            List<QueryCondition> conditions = new ArrayList<>();
            conditions.add(condition);
            conditions.add(condition2);
            aMsg.setLimit(msg.getLimit());
            aMsg.setStart(msg.getStart());
            aMsg.setConditions(conditions);
            aMsg.setReplyWithCount(true);
            aMsg.setCount(false);
            InnerMessageHelper.setMD5(aMsg);
            String gstr = RESTApiDecoder.dump(aMsg);
            RestAPIResponse rsp = restf.syncJsonPost(productServerUrl, gstr, RestAPIResponse.class);
            if (rsp.getState().equals(RestAPIState.Done.toString())) {
                APIQueryTunnelForAlarmReply productReply = (APIQueryTunnelForAlarmReply) RESTApiDecoder.loads(rsp.getResult());//todo rename reply name and refactor field for other product
                if(productReply instanceof  APIQueryTunnelForAlarmReply){
                    inventories = productReply.getInventories();
                    count = productReply.getTotal();
                }
            }
            APIGetResourcesBindByPolicyReply reply = new APIGetResourcesBindByPolicyReply();
            reply.setCount(count);
            reply.setInventories(inventories);
            bus.reply(msg,reply);
        } else if(policyVo.getProductType()==ProductType.VPN){

        }

    }

    private void handle(APIGetResourcesByProductTypeMsg msg) {

        String productServerUrl = getProductUrl( msg.getProductType());

        APIQueryTunnelForAlarmMsg aMsg = new APIQueryTunnelForAlarmMsg();
        List<ResourceInventory> inventories = null;
        QueryCondition condition = new QueryCondition();
        condition.setName("status");
        condition.setOp("=");
        condition.setValue("Connected");
        List<QueryCondition> conditions = new ArrayList<>();
        if(msg.getConditions()!=null && msg.getConditions().size()>0){
            conditions.addAll(msg.getConditions());
        }
        conditions.add(condition);
        aMsg.setConditions(conditions);
        aMsg.setReplyWithCount(true);
        aMsg.setStart(msg.getStart());
        aMsg.setLimit(msg.getLimit());
        InnerMessageHelper.setMD5(aMsg);
        String gstr = RESTApiDecoder.dump(aMsg);
        RestAPIResponse rsp = restf.syncJsonPost(productServerUrl, gstr, RestAPIResponse.class);
        if (rsp.getState().equals(RestAPIState.Done.toString())) {
            APIQueryTunnelForAlarmReply productReply = (APIQueryTunnelForAlarmReply) RESTApiDecoder.loads(rsp.getResult());//todo rename reply name and refactor field for other product
            if(productReply instanceof  APIQueryTunnelForAlarmReply){
                List<TunnelForAlarmInventory> tunnelList  = productReply.getInventories();
                for(TunnelForAlarmInventory inventory: tunnelList){
                    ResourceInventory resourceInventory = new ResourceInventory();
                    resourceInventory.setAccountUuid(inventory.getAccountUuid());
                    resourceInventory.setProductUuid(inventory.getUuid());
                    resourceInventory.setProductType(msg.getProductType());
                    resourceInventory.setDescription(inventory.getDescription());
                    resourceInventory.setProductName(inventory.getName());
                    resourceInventory.setCreateDate(inventory.getCreateDate());
                    resourceInventory.setLastOpDate(inventory.getLastOpDate());
                    SimpleQuery<ResourcePolicyRefVO> query = dbf.createQuery(ResourcePolicyRefVO.class);
                    query.add(ResourcePolicyRefVO_.resourceUuid, SimpleQuery.Op.EQ,inventory.getUuid());
                    List<ResourcePolicyRefVO> resourcePolicyRefVOS = query.list();
                    List<PolicyInventory> policyInventories = new ArrayList<>();
                    for(ResourcePolicyRefVO resourcePolicyRefVO: resourcePolicyRefVOS){
                        policyInventories.add(PolicyInventory.valueOf(dbf.findByUuid(resourcePolicyRefVO.getPolicyUuid(),PolicyVO.class)));
                    }
                    resourceInventory.setPolicies(policyInventories);
                    inventories.add(resourceInventory);
                }

            }
        }
        APIGetResourcesByProductTypeReply reply = new APIGetResourcesByProductTypeReply();
        reply.setInventories(inventories);
        bus.reply(msg,reply);
        }

    private void handle(APIAttachResourceByPoliciesMsg msg) {
        List<ResourcePolicyRefInventory> list = new ArrayList<>();
        if(msg.isAttach()){
           for(String policyUuid : msg.getPolicyUuids()){
               SimpleQuery<ResourcePolicyRefVO> query = dbf.createQuery(ResourcePolicyRefVO.class);
               query.add(ResourcePolicyRefVO_.policyUuid, SimpleQuery.Op.EQ, policyUuid);
               query.add(ResourcePolicyRefVO_.resourceUuid, SimpleQuery.Op.EQ,msg.getResourceUuid());
               ResourcePolicyRefVO resourcePolicyRefVO = query.find();
               dbf.getEntityManager().persist(resourcePolicyRefVO);
               list.add(ResourcePolicyRefInventory.valueOf(resourcePolicyRefVO));
           }
        }else {
            for(String policyUuid : msg.getPolicyUuids()){
              list.add(ResourcePolicyRefInventory.valueOf(deleteResourcePolicyRef(policyUuid,msg.getResourceUuid()))) ;
            }

        }
        dbf.getEntityManager().flush();
        APIAttachResourceByPoliciesEvent event = new APIAttachResourceByPoliciesEvent(msg.getId());
        event.setInventory(list);
        bus.publish(event);
    }

    @Transactional
    private ResourcePolicyRefVO deleteResourcePolicyRef(String policyUuid,String resourceUuid){
        SimpleQuery<ResourcePolicyRefVO> query = dbf.createQuery(ResourcePolicyRefVO.class);
        query.add(ResourcePolicyRefVO_.policyUuid, SimpleQuery.Op.EQ, policyUuid);
        query.add(ResourcePolicyRefVO_.resourceUuid, SimpleQuery.Op.EQ,resourceUuid);
        ResourcePolicyRefVO resourcePolicyRefVO = query.find();
        if(resourcePolicyRefVO!=null){
            dbf.getEntityManager().remove(resourcePolicyRefVO);
        }
        return resourcePolicyRefVO;
    }

    @Transactional
    private void handle(APIAttachPolicyByResourcesMsg msg) {
        List<ResourcePolicyRefInventory> list = new ArrayList<>();
        if(msg.isAttach()){
            for(String resourceUuid : msg.getResourceUuids()) {
                ResourcePolicyRefVO resourcePolicyRefVO = new ResourcePolicyRefVO();
                resourcePolicyRefVO.setUuid(Platform.getUuid());
                resourcePolicyRefVO.setPolicyUuid(msg.getPolicyUuid());
                resourcePolicyRefVO.setResourceUuid(resourceUuid);
                dbf.getEntityManager().persist(resourcePolicyRefVO);
                list.add(ResourcePolicyRefInventory.valueOf(resourcePolicyRefVO));
            }
        } else{
            for(String resourceUuid : msg.getResourceUuids()) {
                list.add(ResourcePolicyRefInventory.valueOf(deleteResourcePolicyRef(msg.getPolicyUuid(), resourceUuid)));
            }
        }
        APIAttachPolicyByResourcesEvent event = new APIAttachPolicyByResourcesEvent(msg.getId());
        event.setInventories(list);
        bus.publish(event);

    }



    private void handle(APIDeletePolicyMsg msg) {
        PolicyVO vo = dbf.findByUuid(msg.getUuid(),PolicyVO.class);
        if(vo!=null){
            dbf.remove(vo);
        }
        APIDeletePolicyEvent event = new APIDeletePolicyEvent(msg.getId());
        event.setInventory(PolicyInventory.valueOf(vo));
        bus.publish(event);
    }

    private void handle(APIUpdatePolicyMsg msg) {
        PolicyVO vo = dbf.findByUuid(msg.getUuid(),PolicyVO.class);
        if(vo != null){
            if(msg.getName()!=null){
                vo.setName(msg.getName());
            }
            if(msg.getDescription()!=null){
                vo.setDescription(vo.getDescription());
            }
        }
        dbf.updateAndRefresh(vo);
        APIUpdatePolicyEvent event  = new APIUpdatePolicyEvent(msg.getId());
        event.setInventory(PolicyInventory.valueOf(vo));
        bus.publish(event);
    }

    private void handle(APIGetPoliciesMsg msg) {
        SimpleQuery<PolicyVO> query = dbf.createQuery(PolicyVO.class);

        List<QueryCondition> conditions = msg.getConditions();
        if(conditions!=null && conditions.size()>0){
            for(QueryCondition condition: conditions){
                if(!StringUtils.isEmpty(condition.getName())){
                    if(condition.getName().equals("productType")){
                        query.add(PolicyVO_.productType, SimpleQuery.Op.EQ, condition.getValue());
                    } else if(condition.getName().equals("bindResources")){
                        String sql = "select policyUuid, count(*) as bindingResources from ResourcePolicyRefVO group by policyUuid ";
                        Query q = dbf.getEntityManager().createNativeQuery(sql);
                        List<Object[]> objs = q.getResultList();
                        List<PolicyBindResource> vos = objs.stream().map(PolicyBindResource::new).collect(Collectors.toList());
                        List<String> uuids = new ArrayList<>();
                        for(PolicyBindResource p : vos){
                            if(p.getBindingResources() == Long.parseLong(condition.getValue())){
                                uuids.add(p.getPolicyUuid());
                            }

                        }
                        query.add(PolicyVO_.uuid, SimpleQuery.Op.IN, uuids);
                    }
                }
            }
        }
        long count = query.count();
        query.setLimit(msg.getLimit());
        query.setStart(msg.getStart());

        List<PolicyVO> policyVOS = query.list();
        for(PolicyVO vo : policyVOS){
            SimpleQuery<ResourcePolicyRefVO> dbfQuery = dbf.createQuery(ResourcePolicyRefVO.class);
            dbfQuery.add(ResourcePolicyRefVO_.policyUuid, SimpleQuery.Op.EQ, vo.getUuid());
            vo.setBindResources(dbfQuery.count());
        }
        APIGetPoliciesReply reply = new APIGetPoliciesReply();
        reply.setInventories(PolicyInventory.valueOf(policyVOS));
        reply.setCount(count);
        bus.reply(msg,reply);

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

        SimpleQuery<ResourcePolicyRefVO> resourcepolicyrefvoquery = dbf.createQuery(ResourcePolicyRefVO.class);
        resourcepolicyrefvoquery.add(ResourcePolicyRefVO_.policyUuid, SimpleQuery.Op.EQ, msg.getPolicyUuid());
        if(resourcepolicyrefvoquery.isExists()){
            TunnelParameter tunnelparameter = null;
            Rule rule = null;
            List<Rule> rulelist = null;
            List<TunnelParameter> tunnelparameterlist = new ArrayList<>();
            List<RegulationVO> regulationvolist = null;
            List<ResourcePolicyRefVO> resourcepolicyrefvolist = resourcepolicyrefvoquery.list();

            for(ResourcePolicyRefVO resourcepolicyrefvo : resourcepolicyrefvolist){
                tunnelparameter = new TunnelParameter();
                tunnelparameter.setTunnel_id(resourcepolicyrefvo.getResourceUuid());
                /**
                 * post获取tunnel信息
                 * tunnelparameter.setEndpointA_ip();
                 * tunnelparameter.setEndpointA_vlan();
                 * tunnelparameter.setEndpointZ_ip();
                 * tunnelparameter.setEndpointZ_vlan();
                */
                rulelist = new ArrayList<>();
                SimpleQuery<RegulationVO> regulationvoquery = dbf.createQuery(RegulationVO.class);
                regulationvoquery.add(RegulationVO_.policyUuid, SimpleQuery.Op.EQ,msg.getPolicyUuid());
                regulationvolist = regulationvoquery.list();
                for(RegulationVO regulationvo : regulationvolist){
                    rule = new Rule();
                    rule.setRight_value(regulationvo.getAlarmThreshold());
                    rule.setStay_time(regulationvo.getTriggerPeriod());
                    rule.setOp(regulationvo.getComparisonRuleVO().getComparisonValue());
                    rule.setStrategy_type(regulationvo.getMonitorTargetVO().getTargetValue());
                    rulelist.add(rule);
                }
                tunnelparameter.setRules(rulelist);
                tunnelparameterlist.add(tunnelparameter);
            }

            /**
             * post 下发 python
             * 参数: JSONObjectUtil.toJsonString(tunnelparameterlist)
             */


        }

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
}
