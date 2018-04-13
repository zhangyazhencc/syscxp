package com.syscxp.alarm.resourcePolicy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.syscxp.alarm.AlarmGlobalProperty;
import com.syscxp.alarm.AlarmUtil;
import com.syscxp.alarm.header.log.AlarmLogVO;
import com.syscxp.alarm.header.log.AlarmLogVO_;
import com.syscxp.alarm.header.resourcePolicy.*;
import com.syscxp.alarm.quota.AlarmQuotaOperator;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.db.UpdateQuery;
import com.syscxp.core.identity.InnerMessageHelper;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.core.workflow.FlowChainBuilder;
import com.syscxp.header.AbstractService;
import com.syscxp.header.alarm.*;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.billing.ProductType;
import com.syscxp.header.core.workflow.*;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.falconapi.FalconApiCommands;
import com.syscxp.header.falconapi.FalconApiRestConstant;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.quota.Quota;
import com.syscxp.header.quota.QuotaConstant;
import com.syscxp.header.quota.ReportQuotaExtensionPoint;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.RestAPIResponse;
import com.syscxp.header.tunnel.tunnel.APIQueryTunnelDetailForAlarmMsg;
import com.syscxp.header.tunnel.tunnel.APIQueryTunnelDetailForAlarmReply;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.StoredProcedureQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.syscxp.utils.CollectionDSL.list;


public class ResourcePolicyManagerImpl extends AbstractService implements ApiMessageInterceptor, ReportQuotaExtensionPoint {

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
        } else if (msg instanceof APIDeleteResourcePolicyRefMsg) {
            handle((APIDeleteResourcePolicyRefMsg) msg);
        } else if (msg instanceof APIUpdateTunnelInfoForFalconMsg) {
            handle((APIUpdateTunnelInfoForFalconMsg) msg);
        } else if (msg instanceof APIStopResourceAlarmMsg) {
            handle((APIStopResourceAlarmMsg) msg);
        } else if (msg instanceof APIStartResourceAlarmMsg) {
            handle((APIStartResourceAlarmMsg) msg);
        } else if (msg instanceof APIGetComparisonRuleListMsg) {
            handle((APIGetComparisonRuleListMsg) msg);
        } else if (msg instanceof APIGetMonitorTargetListMsg) {
            handle((APIGetMonitorTargetListMsg) msg);
        } else if (msg instanceof APIAttachPolicyToResourceMsg) {
            handle((APIAttachPolicyToResourceMsg) msg);
        } else if (msg instanceof APIGetPolicyByResourceUuidMsg) {
            handle((APIGetPolicyByResourceUuidMsg) msg);
        } else if (msg instanceof APIGetResourceUuidsByPolicyMsg) {
            handle((APIGetResourceUuidsByPolicyMsg) msg);
        } else if (msg instanceof APIInitAlarmStrategyMsg) {
            handle((APIInitAlarmStrategyMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    /**
     * 初始化策略与规则
     * -
     * @param msg
     */
    private void handle(APIInitAlarmStrategyMsg msg) {
        APIInitAlarmStrategyEvent event = new APIInitAlarmStrategyEvent(msg.getId());

        StoredProcedureQuery q = dbf.getEntityManager().createStoredProcedureQuery("proc_tunnel_strategy_init");
        q.execute();
        List<PolicyVO> policyVOS = Q.New(PolicyVO.class)
                .eq(PolicyVO_.description, "init")
                .list();

        for (PolicyVO policyVO : policyVOS) {
            logger.info(String.format("updateFalconByPolicy Policy: [%s]", policyVO.getName()));
            updateFalconByPolicy(policyVO.getUuid());
        }

        bus.publish(event);
    }

    private void handle(APIGetResourceUuidsByPolicyMsg msg) {

        APIGetResourceUuidsByPolicyReply reply = new APIGetResourceUuidsByPolicyReply();
        SimpleQuery<ResourcePolicyRefVO> query = dbf.createQuery(ResourcePolicyRefVO.class);
        query.add(ResourcePolicyRefVO_.policyUuid, SimpleQuery.Op.EQ, msg.getPolicyUuid());
        query.select(ResourcePolicyRefVO_.resourceUuid);
        List<String> resourceUuids = query.listValue();
        reply.setResourceUuids(resourceUuids);
        bus.reply(msg, reply);

    }

    private void handle(APIGetPolicyByResourceUuidMsg msg) {

        List<ResourcePolicyRefInventory> lists = new ArrayList<>();
        for (String resourceUuid : msg.getResourceUuids()) {
            SimpleQuery<ResourcePolicyRefVO> query = dbf.createQuery(ResourcePolicyRefVO.class);
            query.add(ResourcePolicyRefVO_.resourceUuid, SimpleQuery.Op.EQ, resourceUuid);
            ResourcePolicyRefVO resourcePolicyRefVO = query.find();
            ResourcePolicyRefInventory inventory = new ResourcePolicyRefInventory();
            inventory.setResourceUuid(resourceUuid);
            if (resourcePolicyRefVO != null) {
                PolicyVO policyVO = dbf.findByUuid(resourcePolicyRefVO.getPolicyUuid(), PolicyVO.class);
                if (policyVO != null) {
                    inventory.setPolicyInventory(PolicyInventory.valueOf(policyVO));
                }

            }
            lists.add(inventory);
        }
        APIGetPolicyByResourceUuidReply reply = new APIGetPolicyByResourceUuidReply();
        reply.setInventories(lists);
        bus.reply(msg, reply);
    }

    @Transactional
    private void handle(APIAttachPolicyToResourceMsg msg) {
        APIAttachPolicyToResourceEvent event = new APIAttachPolicyToResourceEvent(msg.getId());

        if (!StringUtils.isEmpty(msg.getPolicyUuid())) {
            PolicyVO policyVO = resourceBindPolicy(msg);
            event.setInventory(PolicyInventory.valueOf(policyVO));
        } else
            resourceUnbindPolicy(msg);

        bus.publish(event);
    }

    @Transactional
    private PolicyVO resourceBindPolicy(APIAttachPolicyToResourceMsg msg) {
        ResourcePolicyRefVO refVO = Q.New(ResourcePolicyRefVO.class)
                .eq(ResourcePolicyRefVO_.resourceUuid, msg.getResourceUuid())
                .find();
        if (refVO == null) {
            refVO = new ResourcePolicyRefVO();
            refVO.setResourceUuid(msg.getResourceUuid());
            refVO.setPolicyUuid(msg.getPolicyUuid());
            refVO.setProductType(msg.getType());
            dbf.getEntityManager().persist(refVO);
        } else {
            refVO.setPolicyUuid(msg.getPolicyUuid());
            refVO.setProductType(msg.getType());
            dbf.getEntityManager().merge(refVO);
        }

        PolicyVO policyVO = dbf.findByUuid(msg.getPolicyUuid(), PolicyVO.class);
        policyVO.setBindResources(getCount(policyVO.getUuid()));
        policyVO = dbf.getEntityManager().merge(policyVO);

        updateFalconByResource(msg.getResourceUuid());

        return policyVO;
    }

    @Transactional
    private void resourceUnbindPolicy(APIAttachPolicyToResourceMsg msg) {
        ResourcePolicyRefVO refVO = Q.New(ResourcePolicyRefVO.class)
                .eq(ResourcePolicyRefVO_.resourceUuid, msg.getResourceUuid())
                .find();
        dbf.getEntityManager().remove(refVO);

        PolicyVO policyVO = dbf.findByUuid(refVO.getPolicyUuid(), PolicyVO.class);
        policyVO.setBindResources(getCount(policyVO.getUuid()));
        dbf.getEntityManager().merge(policyVO);

        updateFalconByResource(msg.getResourceUuid());
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

        FalconApiCommands.Tunnel tunnel = new FalconApiCommands.Tunnel();
        tunnel.setTunnel_id(msg.getTunnelUuid());
        tunnel.setUser_id(msg.getAccountUuid());
        tunnel.setEndpointA_vid(msg.getSwitchAVlan());
        tunnel.setEndpointB_vid(msg.getSwitchBVlan());
        tunnel.setEndpointA_ip(msg.getSwitchAIp());
        tunnel.setEndpointB_ip(msg.getSwitchBIp());
        tunnel.setBandwidth(msg.getBandwidth());

        List<FalconApiCommands.Rule> rulelist = rulelist = new ArrayList<>();

        List<FalconApiCommands.Tunnel> tunnelList = new ArrayList<>();
        List<RegulationVO> regulationVOList = null;
        FalconApiCommands.Rule rule = null;

        SimpleQuery<ResourcePolicyRefVO> policyrquery = dbf.createQuery(ResourcePolicyRefVO.class);
        policyrquery.add(ResourcePolicyRefVO_.resourceUuid, SimpleQuery.Op.EQ, msg.getTunnelUuid());
        List<ResourcePolicyRefVO> policylist = policyrquery.list();
        for (ResourcePolicyRefVO policy : policylist) {
            SimpleQuery<RegulationVO> regulationvoquery = dbf.createQuery(RegulationVO.class);
            regulationvoquery.add(RegulationVO_.policyUuid, SimpleQuery.Op.EQ, policy.getPolicyUuid());
            regulationVOList = regulationvoquery.list();
            for (RegulationVO regulationvo : regulationVOList) {
                rule = new FalconApiCommands.Rule();
                rule.setOp(regulationvo.getComparisonRuleVO().getComparisonValue());
                rule.setStrategy_type(regulationvo.getMonitorTargetVO().getTargetValue());
                rule.setRight_value(String.valueOf(regulationvo.getAlarmThreshold()));
                rule.setRegulation_id(regulationvo.getUuid());
                rule.setStay_time(regulationvo.getTriggerPeriod());
                rulelist.add(rule);
            }
        }

        tunnel.setRules(rulelist);
        tunnelList.add(tunnel);

        String url = getFalconUrl(FalconApiRestConstant.STRATEGY_SYNC);
        String commandParam = JSONObjectUtil.toJsonString(tunnelList);

        FalconApiCommands.RestResponse response = new FalconApiCommands.RestResponse();
        try {
            response = restf.syncJsonPost(url, commandParam, FalconApiCommands.RestResponse.class);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMsg(String.format("unable to post %s. %s", url, e.getMessage()));
        }

        APIUpdateTunnelInfoForFalconReply reply = new APIUpdateTunnelInfoForFalconReply();
        if (!response.isSuccess())
            reply.setError(Platform.argerr("fail to sync strategy to falcon! Error: %s", response.getMsg()));

        bus.reply(msg, reply);

    }

    private void handle(APIStopResourceAlarmMsg msg) {
        List<ResourcePolicyRefVO> refVOS = Q.New(ResourcePolicyRefVO.class)
                .eq(ResourcePolicyRefVO_.resourceUuid, msg.getTunnelUuid())
                .list();

        APIStopResourceAlarmReply reply = new APIStopResourceAlarmReply();
        for (ResourcePolicyRefVO refVO : refVOS) {
            String url = getFalconUrl(FalconApiRestConstant.STRATEGY_DELETE);
            Map<String, String> tunnelIdMap = new HashMap<>();
            tunnelIdMap.put("tunnel_id", msg.getTunnelUuid());
            FalconApiCommands.RestResponse response = new FalconApiCommands.RestResponse();
            try {
                response = restf.syncJsonPost(url, JSONObjectUtil.toJsonString(tunnelIdMap), FalconApiCommands.RestResponse.class);
            } catch (Exception e) {
                e.printStackTrace();
                response.setSuccess(false);
                response.setMsg(String.format("unable to post %s. %s", url, e.getMessage()));
            }

            if (!response.isSuccess()) {
                reply.setError(Platform.argerr("fail to delete falcon strategy![ResourceUuid: %s ,PolicyUuid: %s] ERROR:%s",
                        refVO.getResourceUuid(), refVO.getPolicyUuid(), response.getMsg()));
                break;
            }
        }

        bus.reply(msg, reply);
    }

    private void handle(APIStartResourceAlarmMsg msg) {
        List<ResourcePolicyRefVO> refVOS = Q.New(ResourcePolicyRefVO.class)
                .eq(ResourcePolicyRefVO_.resourceUuid, msg.getTunnelUuid())
                .list();

        APIStartResourceAlarmReply reply = new APIStartResourceAlarmReply();
        for (ResourcePolicyRefVO refVO : refVOS) {
            try {
                PolicyVO policyVO = dbf.findByUuid(refVO.getPolicyUuid(), PolicyVO.class);
                if (policyVO != null)
                    updateFalconByResource(msg.getTunnelUuid());
                else
                    throw new RuntimeException(String.format("no policy existed![ResourceUuid: %s]", msg.getTunnelUuid()));
            } catch (Exception e) {
                reply.setError(Platform.argerr("fail to sync falcon strategy![ResourceUuid: %s ,PolicyUuid: %s] ERROR:%s",
                        refVO.getResourceUuid(), refVO.getPolicyUuid(), e.getMessage()));
                break;
            }
        }

        bus.reply(msg, reply);
    }

    private void handle(APIDeleteResourcePolicyRefMsg msg) {
        String url = getFalconUrl(FalconApiRestConstant.STRATEGY_DELETE);
        Map<String, String> tunnelIdMap = new HashMap<>();
        tunnelIdMap.put("tunnel_id", msg.getTunnelUuid());
        FalconApiCommands.RestResponse response = new FalconApiCommands.RestResponse();
        try {
            response = restf.syncJsonPost(url, JSONObjectUtil.toJsonString(tunnelIdMap), FalconApiCommands.RestResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            response.setSuccess(false);
            response.setMsg(String.format("unable to post %s. %s", url, e.getMessage()));
        }

        APIDeleteResourceReply reply = new APIDeleteResourceReply();
        if (!response.isSuccess()) {
            reply.setError(Platform.operr(response.getMsg()));
        }

        if (response.isSuccess()) {
            SimpleQuery<ResourcePolicyRefVO> query = dbf.createQuery(ResourcePolicyRefVO.class);
            query.add(ResourcePolicyRefVO_.resourceUuid, SimpleQuery.Op.EQ, msg.getTunnelUuid());
            ResourcePolicyRefVO resourcePolicyRefVO = query.find();
            if (resourcePolicyRefVO != null) {
                String policyUuid = resourcePolicyRefVO.getPolicyUuid();
                dbf.remove(resourcePolicyRefVO);
                PolicyVO policyVO = dbf.findByUuid(policyUuid, PolicyVO.class);
                policyVO.setBindResources(getCount(policyUuid));
                dbf.updateAndRefresh(policyVO);

            }
            UpdateQuery q = UpdateQuery.New(AlarmLogVO.class);
            q.condAnd(AlarmLogVO_.productUuid, SimpleQuery.Op.EQ, msg.getTunnelUuid());
            q.delete();
        }
        bus.reply(msg, reply);

    }

    private long getCount(String policyUuid) {
        SimpleQuery<ResourcePolicyRefVO> query = dbf.createQuery(ResourcePolicyRefVO.class);
        query.add(ResourcePolicyRefVO_.policyUuid, SimpleQuery.Op.EQ, policyUuid);
        return query.count();
    }

    @Transactional
    private void handle(APIDeletePolicyMsg msg) {
        SimpleQuery<ResourcePolicyRefVO> query = dbf.createQuery(ResourcePolicyRefVO.class);
        query.add(ResourcePolicyRefVO_.policyUuid, SimpleQuery.Op.EQ, msg.getUuid());
        List<ResourcePolicyRefVO> list = query.list();
        if (list != null && list.size() > 0) {
            throw new IllegalArgumentException("the policy can remove till no resource bind to this policy");
        }
        PolicyVO vo = dbf.findByUuid(msg.getUuid(), PolicyVO.class);
        APIDeletePolicyEvent event = new APIDeletePolicyEvent(msg.getId());
        if (vo != null) {
            dbf.getEntityManager().remove(dbf.getEntityManager().merge(vo));
            UpdateQuery.New(AlarmLogVO.class).condAnd(AlarmLogVO_.policyUuid, SimpleQuery.Op.EQ, msg.getUuid()).delete();
        }
        List<RegulationVO> regulationVOS = getRegulationVOs(msg.getUuid());
        if (regulationVOS != null && regulationVOS.size() > 0) {
            for (RegulationVO regulationVO : regulationVOS) {
                dbf.getEntityManager().remove(dbf.getEntityManager().merge(regulationVO));
            }

        }
        event.setInventory(PolicyInventory.valueOf(vo));
        bus.publish(event);
    }

    @Transactional
    private List<RegulationVO> getRegulationVOs(String uuid) {
        SimpleQuery<RegulationVO> q = dbf.createQuery(RegulationVO.class);
        q.add(RegulationVO_.policyUuid, SimpleQuery.Op.EQ, uuid);
        return q.list();
    }

    private void handle(APIUpdatePolicyMsg msg) {
        PolicyVO vo = dbf.findByUuid(msg.getUuid(), PolicyVO.class);
        if (vo != null) {
            if (msg.getName() != null) {
                vo.setName(msg.getName());
            }
            if (msg.getDescription() != null) {
                vo.setDescription(msg.getDescription());
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
        FlowChain regulationDelete = FlowChainBuilder.newSimpleFlowChain();
        regulationDelete.setName(String.format("Delete-Regulation-%s", msg.getUuid()));
        regulationDelete.then(new Flow() {
            @Override
            public void run(FlowTrigger trigger, Map data) {
                RegulationVO deleteRegulation = dbf.findByUuid(msg.getUuid(), RegulationVO.class);
                if (deleteRegulation != null) {
                    dbf.remove(deleteRegulation);

                }

                trigger.next();
            }

            @Override
            public void rollback(FlowRollback trigger, Map data) {
                trigger.rollback();
            }
        }).then(new Flow() {
            @Override
            public void run(FlowTrigger trigger, Map data) {
                List<ResourcePolicyRefVO> refVOList = Q.New(ResourcePolicyRefVO.class)
                        .eq(ResourcePolicyRefVO_.policyUuid, regulationVO.getPolicyUuid()).list();
                if (!refVOList.isEmpty())
                    updateFalconByPolicy(regulationVO.getPolicyUuid());

                trigger.next();
            }

            @Override
            public void rollback(FlowRollback trigger, Map data) {
                dbf.updateAndRefresh(regulationVO);
                trigger.rollback();
            }
        }).done(new FlowDoneHandler(null) {
            @Override
            public void handle(Map data) {
                UpdateQuery.New(AlarmLogVO.class).condAnd(AlarmLogVO_.regulationUuid, SimpleQuery.Op.EQ, msg.getUuid()).delete();
                event.setInventory(RegulationInventory.valueOf(regulationVO));
                bus.publish(event);
            }
        }).error(new FlowErrorHandler(null) {
            @Override
            public void handle(ErrorCode errCode, Map data) {
                event.setError(Platform.operr(errCode.getDetails()));
                bus.publish(event);
            }
        }).start();
    }

    @Transactional
    private void handle(APIUpdateRegulationMsg msg) {
        RegulationVO regulationVO = dbf.findByUuid(msg.getUuid(), RegulationVO.class);

        APIUpdateRegulationEvent event = new APIUpdateRegulationEvent(msg.getId());

        FlowChain regulationUpdate = FlowChainBuilder.newSimpleFlowChain();
        regulationUpdate.setName(String.format("Update-Regulation-%s", msg.getUuid()));
        regulationUpdate.then(new Flow() {
            @Override
            public void run(FlowTrigger trigger, Map data) {
                RegulationVO updateRegulation = dbf.findByUuid(msg.getUuid(), RegulationVO.class);

                if (msg.getAlarmThreshold() != regulationVO.getAlarmThreshold()) {
                    updateRegulation.setAlarmThreshold(msg.getAlarmThreshold());
                }
                if (msg.getComparisonRuleUuid() != null) {
                    updateRegulation.setComparisonRuleVO(dbf.findByUuid(msg.getComparisonRuleUuid(), ComparisonRuleVO.class));
                }
                if (msg.getDetectPeriod() != regulationVO.getDetectPeriod()) {
                    updateRegulation.setDetectPeriod(msg.getDetectPeriod());
                }
                if (msg.getTriggerPeriod() != regulationVO.getTriggerPeriod()) {
                    updateRegulation.setTriggerPeriod(msg.getTriggerPeriod());
                }
                dbf.updateAndRefresh(updateRegulation);

                trigger.next();
            }

            @Override
            public void rollback(FlowRollback trigger, Map data) {
                trigger.rollback();
            }
        }).then(new Flow() {
            @Override
            public void run(FlowTrigger trigger, Map data) {
                List<ResourcePolicyRefVO> refVOList = Q.New(ResourcePolicyRefVO.class)
                        .eq(ResourcePolicyRefVO_.policyUuid, regulationVO.getPolicyUuid()).list();
                if (!refVOList.isEmpty())
                    updateFalconByPolicy(regulationVO.getPolicyUuid());

                trigger.next();
            }

            @Override
            public void rollback(FlowRollback trigger, Map data) {
                dbf.updateAndRefresh(regulationVO);
                trigger.rollback();
            }
        }).done(new FlowDoneHandler(null) {
            @Override
            public void handle(Map data) {
                event.setInventory(RegulationInventory.valueOf(dbf.reload(regulationVO)));
                bus.publish(event);
            }
        }).error(new FlowErrorHandler(null) {
            @Override
            public void handle(ErrorCode errCode, Map data) {
                event.setError(Platform.operr(errCode.getDetails()));
                bus.publish(event);
            }
        }).start();
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
        APICreateRegulationEvent event = new APICreateRegulationEvent(msg.getId());
        RegulationVO regulationVO = new RegulationVO();

        FlowChain regulationCreate = FlowChainBuilder.newSimpleFlowChain();
        regulationCreate.setName(String.format("Create-Regulation"));
        regulationCreate.then(new Flow() {
            @Override
            public void run(FlowTrigger trigger, Map data) {
                SimpleQuery<RegulationVO> query = dbf.createQuery(RegulationVO.class);
                query.add(RegulationVO_.policyUuid, SimpleQuery.Op.EQ, msg.getPolicyUuid());
                long count = query.count();
                if (count > 5) {
                    throw new IllegalArgumentException("every policy can build 5 regulations");
                }
                regulationVO.setUuid(Platform.getUuid());
                regulationVO.setAlarmThreshold(msg.getAlarmThreshold());
                regulationVO.setComparisonRuleVO(dbf.findByUuid(msg.getComparisonRuleUuid(), ComparisonRuleVO.class));
                regulationVO.setMonitorTargetVO(dbf.findByUuid(msg.getMonitorTargetUuid(), MonitorTargetVO.class));
                regulationVO.setDetectPeriod(msg.getDetectPeriod());
                regulationVO.setPolicyUuid(msg.getPolicyUuid());
                regulationVO.setTriggerPeriod(msg.getTriggerPeriod());
                dbf.persistAndRefresh(regulationVO);

                trigger.next();
            }

            @Override
            public void rollback(FlowRollback trigger, Map data) {
                dbf.remove(regulationVO);
                trigger.rollback();
            }
        }).then(new Flow() {
            @Override
            public void run(FlowTrigger trigger, Map data) {
                List<ResourcePolicyRefVO> refVOList = Q.New(ResourcePolicyRefVO.class)
                        .eq(ResourcePolicyRefVO_.policyUuid, regulationVO.getPolicyUuid()).list();
                if (!refVOList.isEmpty())
                    updateFalconByPolicy(regulationVO.getPolicyUuid());

                trigger.next();
            }

            @Override
            public void rollback(FlowRollback trigger, Map data) {
                dbf.updateAndRefresh(regulationVO);
                trigger.rollback();
            }
        }).done(new FlowDoneHandler(null) {
            @Override
            public void handle(Map data) {
                event.setInventory(RegulationInventory.valueOf(dbf.reload(regulationVO)));
                bus.publish(event);
            }
        }).error(new FlowErrorHandler(null) {
            @Override
            public void handle(ErrorCode errCode, Map data) {
                event.setError(Platform.operr(errCode.getDetails()));
                bus.publish(event);
            }
        }).start();
    }

    private void handle(APICreatePolicyMsg msg) {
        SimpleQuery<PolicyVO> query = dbf.createQuery(PolicyVO.class);
        query.add(PolicyVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        query.add(PolicyVO_.productType, SimpleQuery.Op.EQ, msg.getProductType());
        long count = query.count();
        if (count > 10) {
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

    private void updateFalconByPolicy(String policyUuid) {
        String falconCommand = getFalconCommandByPolicy(policyUuid);
        String url = getFalconUrl(FalconApiRestConstant.STRATEGY_SYNC);

        sendFalconCommand(url, falconCommand);
    }

    private String getFalconCommandByPolicy(String policyUuid) {
        SimpleQuery<ResourcePolicyRefVO> resourcequery = dbf.createQuery(ResourcePolicyRefVO.class);
        resourcequery.add(ResourcePolicyRefVO_.policyUuid, SimpleQuery.Op.EQ, policyUuid);

        List<ResourcePolicyRefVO> refList = resourcequery.list();
        List<String> prameterlist = new ArrayList<>();
        for (ResourcePolicyRefVO rvo : refList) {
            prameterlist.add(rvo.getResourceUuid());
        }
        Map<String, Object> map = getTunnelInfo(prameterlist);

        if (map.isEmpty())
            throw new RuntimeException(String.format("failed to get resource info ! TunnelUuid: %s",refList.get(0).getResourceUuid())); // IllegalArgumentException("failed to get resource info !");

        List<FalconApiCommands.Tunnel> tunnels = new ArrayList<>();
        for (ResourcePolicyRefVO resource : refList) {
            if (!map.containsKey(resource.getResourceUuid()))
                continue;

            FalconApiCommands.Tunnel tunnel = JSONObjectUtil.toObject(map.get(resource.getResourceUuid()).toString(), FalconApiCommands.Tunnel.class);
            //tunnel.setUser_id(session.getAccountUuid());

            List<FalconApiCommands.Rule> rulelist = new ArrayList<>();
            SimpleQuery<RegulationVO> regulationvoquery = dbf.createQuery(RegulationVO.class);
            regulationvoquery.add(RegulationVO_.policyUuid, SimpleQuery.Op.EQ, resource.getPolicyUuid());
            List<RegulationVO> regulationvolist = regulationvoquery.list();
            for (RegulationVO regulationvo : regulationvolist) {
                FalconApiCommands.Rule rule = new FalconApiCommands.Rule();
                rule.setRegulation_id(regulationvo.getUuid());
                rule.setRight_value(String.valueOf(regulationvo.getAlarmThreshold()));
                rule.setOp(regulationvo.getComparisonRuleVO().getComparisonValue());
                rule.setStay_time(regulationvo.getTriggerPeriod());
                rule.setStrategy_type(regulationvo.getMonitorTargetVO().getTargetValue());
                rulelist.add(rule);
            }

            tunnel.setRules(rulelist);
            tunnels.add(tunnel);
        }

        return JSONObjectUtil.toJsonString(tunnels);
    }


    private void updateFalconByResource(String resourceUuid) {
        String falconCommand = getFalconCommandByResource(resourceUuid);
        String url = getFalconUrl(FalconApiRestConstant.STRATEGY_SYNC);

        sendFalconCommand(url, falconCommand);
    }

    private String getFalconCommandByResource(String resourceUuid) {
        List<String> list = new ArrayList<>();
        list.add(resourceUuid);
        Map<String, Object> map = getTunnelInfo(list);
        List<ResourcePolicyRefVO> refVOList = Q.New(ResourcePolicyRefVO.class)
                .eq(ResourcePolicyRefVO_.resourceUuid, resourceUuid).list();

        List<FalconApiCommands.Tunnel> tunnels = new ArrayList<>();
        if (refVOList.isEmpty()) {
            if (map.containsKey(resourceUuid)) {
                FalconApiCommands.Tunnel tunnel = JSONObjectUtil.toObject(map.get(resourceUuid).toString(), FalconApiCommands.Tunnel.class);

                List<FalconApiCommands.Rule> rulelist = new ArrayList<>();
                tunnel.setRules(rulelist);
                tunnels.add(tunnel);
            }
        } else {
            for (ResourcePolicyRefVO refVO : refVOList) {
                if (!map.containsKey(refVO.getResourceUuid()))
                    continue;

                FalconApiCommands.Tunnel tunnel = JSONObjectUtil.toObject(map.get(refVO.getResourceUuid()).toString(), FalconApiCommands.Tunnel.class);

                SimpleQuery<RegulationVO> regulationvoquery = dbf.createQuery(RegulationVO.class);
                regulationvoquery.add(RegulationVO_.policyUuid, SimpleQuery.Op.EQ, refVO.getPolicyUuid());
                List<RegulationVO> regulationvolist = regulationvoquery.list();
                List<FalconApiCommands.Rule> rulelist = new ArrayList<>();
                for (RegulationVO regulationvo : regulationvolist) {
                    FalconApiCommands.Rule rule = new FalconApiCommands.Rule();
                    rule.setRight_value(String.valueOf(regulationvo.getAlarmThreshold()));
                    rule.setRegulation_id(regulationvo.getUuid());
                    rule.setStay_time(regulationvo.getTriggerPeriod());
                    rule.setOp(regulationvo.getComparisonRuleVO().getComparisonValue());
                    rule.setStrategy_type(regulationvo.getMonitorTargetVO().getTargetValue());
                    rulelist.add(rule);
                }
                tunnel.setRules(rulelist);
                tunnels.add(tunnel);
            }
        }

        return JSONObjectUtil.toJsonString(tunnels);
    }


    private String getFalconUrl(String method) {
        return AlarmGlobalProperty.FALCON_API_URL + method;
    }

    private void sendFalconCommand(String url, String command) {
        FalconApiCommands.RestResponse response = new FalconApiCommands.RestResponse();
        try {
            response = restf.syncJsonPost(url, command, FalconApiCommands.RestResponse.class);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMsg(String.format("unable to post %s. %s", url, e.getMessage()));
        }

        if (!response.isSuccess())
            throw new IllegalArgumentException(String.format("strategy sync fail! Error: %s , command: %s", response.getMsg(),command));
    }

    private Map getTunnelInfo(List<String> Resources) {

        Map result = new HashMap();
        APIQueryTunnelDetailForAlarmMsg tunnelMsg = new APIQueryTunnelDetailForAlarmMsg();
        tunnelMsg.setTunnelUuidList(Resources);
        InnerMessageHelper.setMD5(tunnelMsg);

        APIQueryTunnelDetailForAlarmReply reply = new APIQueryTunnelDetailForAlarmReply();
        RestAPIResponse raps = new RestAPIResponse();
        try {
            raps = restf.syncJsonPost(AlarmUtil.getProductApiUrl(ProductType.TUNNEL),
                    RESTApiDecoder.dump(tunnelMsg), RestAPIResponse.class);

            reply = JSON.parseObject(raps.getResult(), APIQueryTunnelDetailForAlarmReply.class);

            if (!reply.isSuccess())
                reply.setError(Platform.operr("failed to get tunnelInfo! Error: ", reply.getError()));
            else {
                result = (Map) ((Map) JSONObject.parseObject(raps.getResult()).get("com.syscxp.header.tunnel.tunnel.APIQueryTunnelDetailForAlarmReply")).get("map");
            }
        } catch (Exception e) {
            reply.setError(Platform.operr("failed to get tunnelInfo! Error: ", e.getMessage()));
        }

        return result;
    }

    @Override
    public List<Quota> reportQuota() {
        AlarmQuotaOperator quotaOperator = new AlarmQuotaOperator();
        // interface quota
        Quota quota = new Quota();
        quota.setOperator(quotaOperator);
        quota.addMessageNeedValidation(APICreatePolicyMsg.class);
        quota.addMessageNeedValidation(APICreateRegulationMsg.class);

        Quota.QuotaPair p = new Quota.QuotaPair();
        p.setName(AlarmConstant.QUOTA_ALARM_POLICY_NUM);
        p.setValue(QuotaConstant.QUOTA_ALARM_POLICY_NUM);
        quota.addPair(p);

        p = new Quota.QuotaPair();
        p.setName(AlarmConstant.QUOTA_POLICY_RULE_NUM);
        p.setValue(QuotaConstant.QUOTA_POLICY_RULE_NUM);
        quota.addPair(p);


        return list(quota);
    }
}
