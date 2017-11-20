package com.syscxp.tunnel.tunnel;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.CloudBusCallBack;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.*;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.core.thread.PeriodicTask;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.core.workflow.FlowChainBuilder;
import com.syscxp.header.AbstractService;
import com.syscxp.header.agent.OrderCallbackCmd;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.billing.*;
import com.syscxp.header.quota.Quota;
import com.syscxp.header.quota.QuotaConstant;
import com.syscxp.header.quota.ReportQuotaExtensionPoint;
import com.syscxp.header.message.*;
import com.syscxp.header.tunnel.billingCallBack.*;
import com.syscxp.header.core.workflow.*;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.falconapi.FalconApiCommands;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.tunnel.*;
import com.syscxp.header.tunnel.endpoint.EndpointVO;
import com.syscxp.header.tunnel.endpoint.InnerConnectedEndpointInventory;
import com.syscxp.header.tunnel.endpoint.InnerConnectedEndpointVO;
import com.syscxp.header.tunnel.endpoint.InnerConnectedEndpointVO_;
import com.syscxp.header.tunnel.node.NodeVO;
import com.syscxp.header.tunnel.node.ZoneNodeRefVO;
import com.syscxp.header.tunnel.node.ZoneNodeRefVO_;
import com.syscxp.header.tunnel.switchs.*;
import com.syscxp.header.tunnel.tunnel.*;
import com.syscxp.tunnel.quota.InterfaceQuotaOperator;
import com.syscxp.tunnel.quota.TunnelQuotaOperator;
import com.syscxp.utils.CollectionDSL;
import com.syscxp.utils.CollectionUtils;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.TypedQuery;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.syscxp.core.Platform.argerr;
import static com.syscxp.utils.CollectionDSL.list;

/**
 * Create by DCY on 2017/10/26
 */
public class TunnelManagerImpl extends AbstractService implements TunnelManager, ApiMessageInterceptor, ReportQuotaExtensionPoint {
    private static final CLogger logger = Utils.getLogger(TunnelManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private RESTFacade restf;
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private ThreadFacade thdf;

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
        TunnelBase base = new TunnelBase();
        base.handleMessage(msg);
    }

    private void handleApiMessage(APIMessage msg) {
        if (msg instanceof APICreateInterfaceMsg) {
            handle((APICreateInterfaceMsg) msg);
        } else if (msg instanceof APIGetVlanAutoMsg) {
            handle((APIGetVlanAutoMsg) msg);
        } else if (msg instanceof APIGetInterfacePriceMsg) {
            handle((APIGetInterfacePriceMsg) msg);
        } else if (msg instanceof APIGetUnscribeInterfacePriceDiffMsg) {
            handle((APIGetUnscribeInterfacePriceDiffMsg) msg);
        } else if (msg instanceof APIGetTunnelPriceMsg) {
            handle((APIGetTunnelPriceMsg) msg);
        } else if (msg instanceof APIGetModifyTunnelPriceDiffMsg) {
            handle((APIGetModifyTunnelPriceDiffMsg) msg);
        } else if (msg instanceof APIGetUnscribeTunnelPriceDiffMsg) {
            handle((APIGetUnscribeTunnelPriceDiffMsg) msg);
        } else if (msg instanceof APIUpdateInterfacePortMsg) {
            handle((APIUpdateInterfacePortMsg) msg);
        } else if (msg instanceof APIGetInterfaceTypeMsg) {
            handle((APIGetInterfaceTypeMsg) msg);
        } else if (msg instanceof APICreateInterfaceManualMsg) {
            handle((APICreateInterfaceManualMsg) msg);
        } else if (msg instanceof APIUpdateInterfaceMsg) {
            handle((APIUpdateInterfaceMsg) msg);
        } else if (msg instanceof APISLAInterfaceMsg) {
            handle((APISLAInterfaceMsg) msg);
        } else if (msg instanceof APIRenewAutoInterfaceMsg) {
            handle((APIRenewAutoInterfaceMsg) msg);
        } else if (msg instanceof APIRenewInterfaceMsg) {
            handle((APIRenewInterfaceMsg) msg);
        } else if (msg instanceof APIDeleteInterfaceMsg) {
            handle((APIDeleteInterfaceMsg) msg);
        } else if (msg instanceof APICreateTunnelMsg) {
            handle((APICreateTunnelMsg) msg);
        } else if (msg instanceof APICreateTunnelManualMsg) {
            handle((APICreateTunnelManualMsg) msg);
        } else if (msg instanceof APIUpdateTunnelMsg) {
            handle((APIUpdateTunnelMsg) msg);
        } else if (msg instanceof APIUpdateTunnelBandwidthMsg) {
            handle((APIUpdateTunnelBandwidthMsg) msg);
        } else if (msg instanceof APIRenewTunnelMsg) {
            handle((APIRenewTunnelMsg) msg);
        } else if (msg instanceof APIRenewAutoTunnelMsg) {
            handle((APIRenewAutoTunnelMsg) msg);
        } else if (msg instanceof APISalTunnelMsg) {
            handle((APISalTunnelMsg) msg);
        } else if (msg instanceof APIDeleteTunnelMsg) {
            handle((APIDeleteTunnelMsg) msg);
        } else if (msg instanceof APIDeleteForciblyTunnelMsg) {
            handle((APIDeleteForciblyTunnelMsg) msg);
        } else if (msg instanceof APIUpdateTunnelStateMsg) {
            handle((APIUpdateTunnelStateMsg) msg);
        } else if (msg instanceof APICreateQinqMsg) {
            handle((APICreateQinqMsg) msg);
        } else if (msg instanceof APIDeleteQinqMsg) {
            handle((APIDeleteQinqMsg) msg);
        } else if (msg instanceof APIQueryTunnelDetailForAlarmMsg) {
            handle((APIQueryTunnelDetailForAlarmMsg) msg);
        } else if (msg instanceof APIListSwitchPortByTypeMsg) {
            handle((APIListSwitchPortByTypeMsg) msg);
        } else if (msg instanceof APIUpdateForciblyTunnelVlanMsg) {
            handle((APIUpdateForciblyTunnelVlanMsg) msg);
        } else if (msg instanceof APIUpdateTunnelVlanMsg) {
            handle((APIUpdateTunnelVlanMsg) msg);
        } else if (msg instanceof APIListCrossTunnelMsg) {
            handle((APIListCrossTunnelMsg) msg);
        } else if (msg instanceof APIListInnerEndpointMsg) {
            handle((APIListInnerEndpointMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }


    private void handle(APIListSwitchPortByTypeMsg msg) {
        List<SwitchPortVO> ports = getSwitchPortByType(msg.getUuid(), msg.getType());
        APIListSwitchPortByTypeReply reply = new APIListSwitchPortByTypeReply();
        reply.setInventories(SwitchPortInventory.valueOf(ports));
        bus.reply(msg, reply);
    }

    private void handle(APIListCrossTunnelMsg msg) {
        APIListCrossTunnelReply reply = new APIListCrossTunnelReply();
        List<String> tunnelUuids = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.interfaceUuid,msg.getUuid())
                .select(TunnelSwitchPortVO_.tunnelUuid)
                .listValues();
        List<TunnelVO> tunnelVOS = new ArrayList<>();
        if(!tunnelUuids.isEmpty()){
            tunnelVOS = Q.New(TunnelVO.class).
                    in(TunnelVO_.uuid,tunnelUuids).
                    eq(TunnelVO_.accountUuid,msg.getAccountUuid()).
                    eq(TunnelVO_.state,TunnelState.Enabled)
                    .list();
        }
        reply.setInventories(TunnelInventory.valueOf(tunnelVOS));
        bus.reply(msg, reply);
    }

    private void handle(APIListInnerEndpointMsg msg) {
        APIListInnerEndpointReply reply = new APIListInnerEndpointReply();

        //根据连接点查询节点
        EndpointVO endpointVOA = dbf.findByUuid(msg.getEndpointAUuid(),EndpointVO.class);
        EndpointVO endpointVOZ = dbf.findByUuid(msg.getEndpointZUuid(),EndpointVO.class);

        NodeVO nodeVOA = dbf.findByUuid(endpointVOA.getNodeUuid(),NodeVO.class);
        NodeVO nodeVOZ = dbf.findByUuid(endpointVOZ.getNodeUuid(),NodeVO.class);

        String conntectedEndpoint = null;
        List<InnerConnectedEndpointVO> innerEndpoints = new ArrayList<>();
        if(nodeVOA.getCountry().equals("CHINA") && !nodeVOZ.getCountry().equals("CHINA")){
            conntectedEndpoint = msg.getEndpointZUuid();
        }
        if(!nodeVOA.getCountry().equals("CHINA") && nodeVOZ.getCountry().equals("CHINA")){
            conntectedEndpoint = msg.getEndpointAUuid();
        }
        if(conntectedEndpoint != null){
            innerEndpoints = Q.New(InnerConnectedEndpointVO.class)
                    .eq(InnerConnectedEndpointVO_.connectedEndpointUuid,conntectedEndpoint)
                    .list();
        }

        reply.setInventories(InnerConnectedEndpointInventory.valueOf(innerEndpoints));
        bus.reply(msg, reply);
    }

    private void updateNetworkType(InterfaceVO iface, String tunnelUuid, NetworkType newType, List<InnerVlanSegment> segments) {
        UpdateQuery.New(InterfaceVO.class)
                .set(InterfaceVO_.type, newType)
                .eq(InterfaceVO_.uuid, iface.getUuid())
                .update();

        if (tunnelUuid == null)
            return;

        if (iface.getType() == NetworkType.QINQ)
            UpdateQuery.New(QinqVO.class).eq(QinqVO_.tunnelUuid, tunnelUuid).delete();

        if (newType == NetworkType.QINQ) {
            List<QinqVO> vos = new ArrayList<>();
            for (InnerVlanSegment segment : segments) {
                QinqVO qinq = new QinqVO();
                qinq.setUuid(Platform.getUuid());
                qinq.setTunnelUuid(tunnelUuid);
                qinq.setStartVlan(segment.getStartVlan());
                qinq.setEndVlan(segment.getEndVlan());
                vos.add(qinq);
            }
            dbf.persistCollection(vos);
        }
    }

    private void handle(APIUpdateInterfacePortMsg msg) {
        InterfaceVO iface = dbf.findByUuid(msg.getUuid(), InterfaceVO.class);
        logger.info(String.format("before update InterfaceVO: [%s]", iface));

        TunnelSwitchPortVO tsPort = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.interfaceUuid, msg.getUuid())
                .find();
        boolean isUsed = tsPort != null;
        Map<String, Object> rollback = new HashMap<>();
        if (tsPort != null && iface.getType() == NetworkType.QINQ) {
            List<QinqVO> qinqs = Q.New(QinqVO.class)
                    .eq(QinqVO_.tunnelUuid, tsPort.getTunnelUuid()).list();
            rollback.put("qinqs", qinqs);
        }


        APIUpdateInterfacePortEvent evt = new APIUpdateInterfacePortEvent(msg.getId());
        FlowChain updateInterface = FlowChainBuilder.newSimpleFlowChain();
        updateInterface.setName(String.format("update-interfacePort-%s", msg.getUuid()));
        updateInterface.setData(rollback);
        updateInterface.then(new Flow() {
            String __name__ = "update-interface-for-DB";

            @Override
            public void run(FlowTrigger trigger, Map data) {
                if (!Objects.equals(msg.getSwitchPortUuid(), iface.getSwitchPortUuid())) {
                    UpdateQuery.New(InterfaceVO.class)
                            .set(InterfaceVO_.switchPortUuid, msg.getSwitchPortUuid())
                            .eq(InterfaceVO_.uuid, msg.getUuid())
                            .update();
                } else {
                    String tunnelUuid = isUsed ? tsPort.getTunnelUuid() : null;
                    updateNetworkType(iface, tunnelUuid, msg.getNetworkType(), msg.getSegments());
                }
                logger.info(String.format("after update InterfaceVO[uuid: %s]", iface.getUuid()));
                //throw new CloudRuntimeException("update interface port ...............");
                trigger.next();
            }

            @Override
            public void rollback(FlowRollback trigger, Map data) {
                dbf.updateAndRefresh(iface);
                if (isUsed) {
                    List<QinqVO> qinqs = (List<QinqVO>) data.getOrDefault("qinqs", new ArrayList());
                    dbf.updateCollection(qinqs);
                }
                logger.info(String.format("rollback to update InterfaceVO[uuid: %s]", iface.getUuid()));

                trigger.rollback();
            }
        }).then(new Flow() {
            String __name__ = "update-tunnelSwitchPort-for-DB";

            @Override
            public void run(FlowTrigger trigger, Map data) {
                if (isUsed) {
                    UpdateQuery.New(TunnelSwitchPortVO.class)
                            .set(TunnelSwitchPortVO_.switchPortUuid, msg.getSwitchPortUuid())
                            .set(TunnelSwitchPortVO_.type, msg.getNetworkType())
                            .eq(TunnelSwitchPortVO_.interfaceUuid, msg.getUuid())
                            .update();
                    logger.info(String.format("after update TunnelSwitchPortVO[uuid: %s]", tsPort.getUuid()));
                }
                trigger.next();
            }

            @Override
            public void rollback(FlowRollback trigger, Map data) {
                if (isUsed) {
                    dbf.updateAndRefresh(tsPort);
                    logger.info(String.format("rollback to update TunnelSwitchPortVO[uuid: %s]", tsPort.getUuid()));
                }
                trigger.rollback();
            }
        }).then(new Flow() {
            String __name__ = "send-tunnelMsg";

            @Override
            public void run(FlowTrigger trigger, Map data) {
                if (msg.isIssue() && isUsed) {
                    TunnelVO tunnel = Q.New(TunnelVO.class)
                            .eq(TunnelVO_.uuid, tsPort.getTunnelUuid())
                            .find();
                    TaskResourceVO taskResource = newTaskResourceVO(tunnel, TaskType.ModifyPorts);
                    ModifyTunnelPortsMsg modifyMsg = new ModifyTunnelPortsMsg();
                    modifyMsg.setTunnelUuid(tunnel.getUuid());
                    modifyMsg.setTaskUuid(taskResource.getUuid());
                    bus.makeLocalServiceId(modifyMsg, TunnelConstant.SERVICE_ID);
                    bus.send(modifyMsg, new CloudBusCallBack(null) {
                        @Override
                        public void run(MessageReply reply) {
                            if (reply.isSuccess()) {
                                logger.info(String.format("Successfully restart tunnel[uuid:%s].", tunnel.getUuid()));
                            } else {
                                logger.info(String.format("Failed to restart tunnel[uuid:%s].", tunnel.getUuid()));
                                trigger.fail(reply.getError());
                            }
                        }
                    });
                }
                trigger.next();
            }

            @Override
            public void rollback(FlowRollback trigger, Map data) {
                trigger.rollback();
            }
        }).done(new FlowDoneHandler(null) {
            @Override
            public void handle(Map data) {
                evt.setInventory(InterfaceInventory.valueOf(dbf.reload(iface)));
            }
        }).error(new FlowErrorHandler(null) {
            @Override
            public void handle(ErrorCode errCode, Map data) {
                evt.setError(errf.stringToOperationError("update interfacePort failed!"));
            }
        }).start();

        bus.publish(evt);
    }

    private void handle(APIGetVlanAutoMsg msg) {
        APIGetVlanAutoReply reply = new APIGetVlanAutoReply();

        TunnelStrategy ts = new TunnelStrategy();
        Integer vlan = ts.getVlanByStrategy(msg.getInterfaceUuid());
        if (vlan == 0) {
            throw new ApiMessageInterceptionException(argerr("该端口所属虚拟交换机下已无可使用的VLAN，请联系系统管理员 "));
        }

        reply.setVlan(vlan);
        bus.reply(msg, reply);
    }

    private void handle(APIGetInterfaceTypeMsg msg) {
        APIGetInterfaceTypeReply reply = new APIGetInterfaceTypeReply();
        reply.setInventories(PortOfferingInventory.valueOf(getPortTypeByEndpoint(msg.getUuid())));
        bus.reply(msg, reply);
    }

    private void handle(APIGetInterfacePriceMsg msg) {

        APIGetProductPriceMsg pmsg = new APIGetProductPriceMsg();
        pmsg.setProductChargeModel(msg.getProductChargeModel());
        pmsg.setDuration(msg.getDuration());
        pmsg.setAccountUuid(msg.getAccountUuid());
        pmsg.setUnits(getInterfacePriceUnit(msg.getPortOfferingUuid()));
        APIGetProductPriceReply reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(pmsg);
        bus.reply(msg, new APIGetInterfacePriceReply(reply));
    }

    private void handle(APIGetUnscribeInterfacePriceDiffMsg msg) {

        InterfaceVO vo = dbf.findByUuid(msg.getUuid(), InterfaceVO.class);

        APIGetUnscribeProductPriceDiffMsg upmsg = new APIGetUnscribeProductPriceDiffMsg();

        upmsg.setAccountUuid(vo.getAccountUuid());
        upmsg.setProductUuid(msg.getUuid());
        upmsg.setExpiredTime(vo.getExpireDate());

        APIGetUnscribeProductPriceDiffReply reply = new TunnelRESTCaller().syncJsonPost(upmsg);

        bus.reply(msg, new APIGetUnscribeInterfacePriceDiffReply(reply));
    }

    private void handle(APIGetTunnelPriceMsg msg) {

        APIGetProductPriceMsg pmsg = new APIGetProductPriceMsg();
        pmsg.setProductChargeModel(msg.getProductChargeModel());
        pmsg.setDuration(msg.getDuration());
        pmsg.setAccountUuid(msg.getAccountUuid());
        pmsg.setUnits(getTunnelPriceUnit(msg.getBandwidthOfferingUuid(), msg.getNodeAUuid(),
                msg.getNodeZUuid(), msg.getInnerEndpointUuid()));
        APIGetProductPriceReply reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(pmsg);
        bus.reply(msg, new APIGetTunnelPriceReply(reply));
    }

    private void handle(APIGetModifyTunnelPriceDiffMsg msg) {
        TunnelSwitchPortVO tunnelSwitchPortVOA = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "A")
                .find();
        TunnelSwitchPortVO tunnelSwitchPortVOZ = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "Z")
                .find();
        String innerEndpointUuid = null;
        TunnelSwitchPortVO tunnelSwitchPortVOB = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "B")
                .find();
        if (tunnelSwitchPortVOB != null) {
            innerEndpointUuid = tunnelSwitchPortVOB.getEndpointUuid();
        }
        EndpointVO endpointVOA = dbf.findByUuid(tunnelSwitchPortVOA.getEndpointUuid(), EndpointVO.class);
        EndpointVO endpointVOZ = dbf.findByUuid(tunnelSwitchPortVOZ.getEndpointUuid(), EndpointVO.class);

        APIGetModifyProductPriceDiffMsg pmsg = new APIGetModifyProductPriceDiffMsg();
        pmsg.setUnits(getTunnelPriceUnit(msg.getBandwidthOfferingUuid(), endpointVOA.getNodeUuid(),
                endpointVOZ.getNodeUuid(), innerEndpointUuid));
        pmsg.setProductUuid(msg.getUuid());
        pmsg.setAccountUuid(msg.getAccountUuid());
        pmsg.setExpiredTime(dbf.findByUuid(msg.getUuid(), TunnelVO.class).getExpireDate());

        APIGetModifyProductPriceDiffReply reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(pmsg);
        bus.reply(msg, new APIGetModifyTunnelPriceDiffReply(reply));
    }

    private void handle(APIGetUnscribeTunnelPriceDiffMsg msg) {
        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);

        APIGetUnscribeProductPriceDiffMsg upmsg = new APIGetUnscribeProductPriceDiffMsg();
        upmsg.setAccountUuid(msg.getAccountUuid());
        upmsg.setProductUuid(msg.getUuid());
        if (vo.getExpireDate() == null) {
            upmsg.setExpiredTime(dbf.getCurrentSqlTime());
            upmsg.setCreateFailure(true);
        } else {
            upmsg.setExpiredTime(vo.getExpireDate());
        }

        APIGetUnscribeProductPriceDiffReply reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(upmsg);
        bus.reply(msg, new APIGetUnscribeTunnelPriceDiffReply(reply));

    }

    private void handle(APICreateInterfaceMsg msg) {

        //分配资源:策略分配端口
        TunnelStrategy ts = new TunnelStrategy();
        String switchPortUuid = ts.getSwitchPortByStrategy(msg.getAccountUuid(), msg.getEndpointUuid(), msg.getPortOfferingUuid());
        if (switchPortUuid == null) {
            throw new ApiMessageInterceptionException(argerr("该连接点下无可用的端口"));
        }

        //保存数据，分配资源
        InterfaceVO vo = new InterfaceVO();
        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(null);
        vo.setOwnerAccountUuid(msg.getAccountUuid());
        vo.setName(msg.getName());
        vo.setEndpointUuid(msg.getEndpointUuid());
        vo.setSwitchPortUuid(switchPortUuid);
        vo.setType(NetworkType.TRUNK);
        vo.setDuration(msg.getDuration());
        vo.setProductChargeModel(msg.getProductChargeModel());
        vo.setDescription(msg.getDescription());
        vo.setState(InterfaceState.Unpaid);
        vo.setMaxModifies(CoreGlobalProperty.INTERFACE_MAX_MOTIFIES);
        vo.setExpireDate(dbf.getCurrentSqlTime());
        vo = dbf.persistAndRefresh(vo);

        //调用支付
        APICreateBuyOrderMsg orderMsg = new APICreateBuyOrderMsg();
        ProductInfoForOrder productInfoForOrder = createBuyOrderForInterface(vo, msg.getPortOfferingUuid(), new CreateInterfaceCallBack());
        productInfoForOrder.setOpAccountUuid(msg.getSession().getAccountUuid());
        productInfoForOrder.setNotifyUrl(restf.getSendCommandUrl());
        orderMsg.setProducts(CollectionDSL.list(productInfoForOrder));

        List<OrderInventory> inventories = createBuyOrder(orderMsg);
        afterCreateInterface(inventories, vo, msg);
    }

    private ProductInfoForOrder createBuyOrderForInterface(InterfaceVO vo, String portOfferingUuid, NotifyCallBackData callBack) {
        ProductInfoForOrder order = new ProductInfoForOrder();
        order.setProductChargeModel(vo.getProductChargeModel());
        order.setDuration(vo.getDuration());
        order.setProductName(vo.getName());
        order.setProductUuid(vo.getUuid());
        order.setProductType(ProductType.PORT);
        order.setDescriptionData(getDescriptionForInterface(vo));
        order.setCallBackData(RESTApiDecoder.dump(callBack));
        order.setUnits(getInterfacePriceUnit(portOfferingUuid));
        order.setAccountUuid(vo.getOwnerAccountUuid());

        return order;
    }

    private void afterCreateInterface(List<OrderInventory> inventories, InterfaceVO vo, APIMessage msg) {
        APICreateInterfaceEvent evt = new APICreateInterfaceEvent(msg.getId());

        if (!inventories.isEmpty()) {

            //付款成功,记录生效订单
            saveResourceOrderEffective(inventories.get(0).getUuid(), vo.getUuid(), vo.getClass().getSimpleName());
            //状态修改已支付，生成到期时间
            vo.setAccountUuid(vo.getOwnerAccountUuid());
            vo.setState(InterfaceState.Paid);
            vo.setExpireDate(getExpireDate(dbf.getCurrentSqlTime(), vo.getProductChargeModel(), vo.getDuration()));

            vo = dbf.updateAndRefresh(vo);

            evt.setInventory(InterfaceInventory.valueOf(vo));
        } else {
            //付款失败
            evt.setError(errf.stringToOperationError("付款失败"));
            evt.setInventory(InterfaceInventory.valueOf(vo));
        }
        bus.publish(evt);
    }

    private void handle(APICreateInterfaceManualMsg msg) {
        //保存数据
        InterfaceVO vo = new InterfaceVO();
        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(null);
        vo.setOwnerAccountUuid(msg.getAccountUuid());
        vo.setName(msg.getName());
        vo.setEndpointUuid(msg.getEndpointUuid());
        vo.setSwitchPortUuid(msg.getSwitchPortUuid());
        vo.setType(msg.getNetworkType() != null ? msg.getNetworkType() : NetworkType.TRUNK);
        vo.setDuration(msg.getDuration());
        vo.setProductChargeModel(msg.getProductChargeModel());
        vo.setDescription(msg.getDescription());
        vo.setExpireDate(dbf.getCurrentSqlTime());
        vo.setState(InterfaceState.Unpaid);
        vo.setMaxModifies(CoreGlobalProperty.INTERFACE_MAX_MOTIFIES);

        vo = dbf.persistAndRefresh(vo);

        //调用支付
        APICreateBuyOrderMsg orderMsg = new APICreateBuyOrderMsg();
        ProductInfoForOrder productInfoForOrder = createBuyOrderForInterface(vo, msg.getPortOfferingUuid(), new CreateInterfaceCallBack());
        productInfoForOrder.setOpAccountUuid(msg.getAccountUuid());
        orderMsg.setProducts(CollectionDSL.list(productInfoForOrder));

        List<OrderInventory> inventories = createBuyOrder(orderMsg);
        afterCreateInterface(inventories, vo, msg);
    }

    private void handle(APIUpdateInterfaceMsg msg) {
        InterfaceVO vo = dbf.findByUuid(msg.getUuid(), InterfaceVO.class);
        boolean update = false;
        if (msg.getName() != null) {
            vo.setName(msg.getName());
            update = true;
        }
        if (msg.getDescription() != null) {
            vo.setDescription(msg.getDescription());
            update = true;
        }

        if (update)
            vo = dbf.updateAndRefresh(vo);

        APIUpdateInterfaceEvent evt = new APIUpdateInterfaceEvent(msg.getId());
        evt.setInventory(InterfaceInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIRenewInterfaceMsg msg) {

        InterfaceVO vo = dbf.findByUuid(msg.getUuid(), InterfaceVO.class);

        APICreateRenewOrderMsg orderMsg = new APICreateRenewOrderMsg(getOrderMsgForInterface(vo, new RenewInterfaceCallBack()));
        orderMsg.setDuration(msg.getDuration());
        orderMsg.setProductChargeModel(msg.getProductChargeModel());
        orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
        orderMsg.setStartTime(dbf.getCurrentSqlTime());
        orderMsg.setExpiredTime(vo.getExpireDate());

        OrderInventory inventory = createOrder(orderMsg);

        afterRenewInterface(inventory, vo, msg);
    }

    private void handle(APIRenewAutoInterfaceMsg msg) {

        InterfaceVO vo = dbf.findByUuid(msg.getUuid(), InterfaceVO.class);

        APICreateRenewOrderMsg orderMsg = new APICreateRenewOrderMsg(getOrderMsgForInterface(vo, new RenewAutoInterfaceCallBack()));
        orderMsg.setDuration(msg.getDuration());
        orderMsg.setProductChargeModel(msg.getProductChargeModel());
        orderMsg.setOpAccountUuid("system");
        orderMsg.setStartTime(dbf.getCurrentSqlTime());
        orderMsg.setExpiredTime(vo.getExpireDate());

        OrderInventory inventory = createOrder(orderMsg);

        afterRenewInterface(inventory, vo, msg);

    }

    private void afterRenewInterface(OrderInventory orderInventory, InterfaceVO vo, APIMessage msg) {
        APIRenewAutoInterfaceReply reply = new APIRenewAutoInterfaceReply();

        if (orderInventory != null) {
            //续费或者赔偿成功,记录生效订单
            saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());
            //更新到期时间
            vo.setDuration(orderInventory.getDuration());
            vo.setProductChargeModel(orderInventory.getProductChargeModel());
            vo.setExpireDate(getExpireDate(vo.getExpireDate(), orderInventory.getProductChargeModel(), orderInventory.getDuration()));

            vo = dbf.updateAndRefresh(vo);
            reply.setInventory(InterfaceInventory.valueOf(vo));
        } else {
            reply.setError(errf.stringToOperationError("订单操作失败"));
        }

        bus.reply(msg, reply);
    }

    private void handle(APISLAInterfaceMsg msg) {
        APISLAInterfaceReply reply = new APISLAInterfaceReply();

        InterfaceVO vo = dbf.findByUuid(msg.getUuid(), InterfaceVO.class);
        Timestamp newTime = vo.getExpireDate();
        APICreateSLACompensationOrderMsg orderMsg = new APICreateSLACompensationOrderMsg(getOrderMsgForInterface(vo, new SlaInterfaceCallBack()));
        orderMsg.setDuration(msg.getDuration());
        orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
        orderMsg.setStartTime(dbf.getCurrentSqlTime());
        orderMsg.setExpiredTime(vo.getExpireDate());

        OrderInventory orderInventory = createOrder(orderMsg);

        if (orderInventory != null) {
            //续费或者赔偿成功,记录生效订单
            saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());
            //更新到期时间
            vo.setDuration(msg.getDuration());
            vo.setExpireDate(getExpireDate(newTime, ProductChargeModel.BY_DAY, msg.getDuration()));

            vo = dbf.updateAndRefresh(vo);
            reply.setInventory(InterfaceInventory.valueOf(vo));
        } else {
            reply.setError(errf.stringToOperationError("订单操作失败"));
        }

        bus.reply(msg, reply);
    }

    private void handle(APIDeleteInterfaceMsg msg) {
        APIDeleteInterfaceEvent evt = new APIDeleteInterfaceEvent(msg.getId());

        InterfaceVO vo = dbf.findByUuid(msg.getUuid(), InterfaceVO.class);

        //调用退订
        APICreateUnsubcribeOrderMsg orderMsg = new APICreateUnsubcribeOrderMsg(getOrderMsgForInterface(vo, new UnsubcribeInterfaceCallBack()));
        orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
        orderMsg.setStartTime(dbf.getCurrentSqlTime());
        orderMsg.setExpiredTime(vo.getExpireDate());

        OrderInventory orderInventory = createOrder(orderMsg);
        if (orderInventory != null) {
            //退订成功,记录生效订单
            saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());
            //删除产品
            dbf.remove(vo);
            evt.setInventory(InterfaceInventory.valueOf(vo));
        } else {
            //退订失败
            evt.setError(errf.stringToOperationError("退订失败"));
        }

        bus.publish(evt);
    }

    @Transactional
    private void handle(APICreateTunnelMsg msg) {

        //保存数据，分配资源
        TunnelStrategy ts = new TunnelStrategy();

        TunnelVO vo = new TunnelVO();
        BandwidthOfferingVO bandwidthOfferingVO = dbf.findByUuid(msg.getBandwidthOfferingUuid(), BandwidthOfferingVO.class);

        EndpointVO evoA = dbf.findByUuid(msg.getEndpointAUuid(), EndpointVO.class);
        EndpointVO evoZ = dbf.findByUuid(msg.getEndpointZUuid(), EndpointVO.class);

        NodeVO nvoA = dbf.findByUuid(evoA.getNodeUuid(),NodeVO.class);
        NodeVO nvoZ = dbf.findByUuid(evoZ.getNodeUuid(),NodeVO.class);

        InterfaceVO interfaceVOA = new InterfaceVO();
        InterfaceVO interfaceVOZ = new InterfaceVO();
        boolean newBuyInterfaceA = false;
        boolean newBuyInterfaceZ = false;
        boolean crossTunnel = false;
        Integer crossVlan = null;
        Integer vlanA;
        Integer vlanZ;
        Integer crossVsi;

        if (msg.getCrossTunnelUuid() != null) {   //存在关联云专线
            crossTunnel = true;
            crossVsi = Q.New(TunnelVO.class)
                    .eq(TunnelVO_.uuid, msg.getCrossTunnelUuid())
                    .select(TunnelVO_.vsi)
                    .findValue();
            crossVlan = Q.New(TunnelSwitchPortVO.class)
                    .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getCrossTunnelUuid())
                    .eq(TunnelSwitchPortVO_.interfaceUuid, msg.getCrossInterfaceUuid())
                    .select(TunnelSwitchPortVO_.vlan)
                    .findValue();
            vo.setVsi(crossVsi);
        } else {
            vo.setVsi(getVsiAuto());
        }

        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(null);
        vo.setOwnerAccountUuid(msg.getAccountUuid());
        vo.setMonitorCidr(null);
        vo.setName(msg.getName());
        vo.setBandwidth(bandwidthOfferingVO.getBandwidth());
        vo.setDuration(msg.getDuration());
        vo.setState(TunnelState.Unpaid);
        vo.setStatus(TunnelStatus.Disconnected);
        vo.setExpireDate(null);
        vo.setDescription(msg.getDescription());
        vo.setProductChargeModel(msg.getProductChargeModel());
        vo.setMonitorState(TunnelMonitorState.Disabled);
        vo.setMaxModifies(CoreGlobalProperty.TUNNEL_MAX_MOTIFIES);
        vo.setDistance(Distance.getDistance(nvoA.getLongtitude(), nvoA.getLatitude(), nvoZ.getLongtitude(), nvoZ.getLatitude()));

        if (msg.getInterfaceAUuid() != null && msg.getInterfaceZUuid() != null) {         //没有新购接口
            interfaceVOA = dbf.findByUuid(msg.getInterfaceAUuid(), InterfaceVO.class);
            interfaceVOZ = dbf.findByUuid(msg.getInterfaceZUuid(), InterfaceVO.class);

            if (crossTunnel) {
                if (msg.getInterfaceAUuid().equals(msg.getCrossInterfaceUuid())) {
                    vlanA = crossVlan;
                    vlanZ = ts.getVlanByStrategy(msg.getInterfaceZUuid());
                    if (vlanZ == 0) {
                        throw new ApiMessageInterceptionException(argerr("该端口所属虚拟交换机下已无可使用的VLAN，请联系系统管理员 "));
                    }
                } else {
                    vlanA = ts.getVlanByStrategy(msg.getInterfaceAUuid());
                    if (vlanA == 0) {
                        throw new ApiMessageInterceptionException(argerr("该端口所属虚拟交换机下已无可使用的VLAN，请联系系统管理员 "));
                    }
                    vlanZ = crossVlan;
                }
            } else {
                vlanA = ts.getVlanByStrategy(msg.getInterfaceAUuid());
                if (vlanA == 0) {
                    throw new ApiMessageInterceptionException(argerr("该端口所属虚拟交换机下已无可使用的VLAN，请联系系统管理员 "));
                }
                vlanZ = ts.getVlanByStrategy(msg.getInterfaceZUuid());
                if (vlanZ == 0) {
                    throw new ApiMessageInterceptionException(argerr("该端口所属虚拟交换机下已无可使用的VLAN，请联系系统管理员 "));
                }
            }

        } else if (msg.getInterfaceAUuid() == null && msg.getInterfaceZUuid() == null) {   //都是新购接口
            //分配A端口
            newBuyInterfaceA = true;
            String switchPortUuidA = ts.getSwitchPortByStrategy(msg.getAccountUuid(), msg.getEndpointAUuid(), msg.getPortOfferingUuidA());
            if (switchPortUuidA == null) {
                throw new ApiMessageInterceptionException(argerr("该连接点A下无可用的端口"));
            }
            interfaceVOA.setUuid(Platform.getUuid());
            interfaceVOA.setAccountUuid(null);
            interfaceVOA.setOwnerAccountUuid(msg.getAccountUuid());
            interfaceVOA.setName(evoA.getName() + Platform.getUuid().substring(0, 6));
            interfaceVOA.setEndpointUuid(msg.getEndpointAUuid());
            interfaceVOA.setSwitchPortUuid(switchPortUuidA);
            interfaceVOA.setType(NetworkType.TRUNK);
            interfaceVOA.setDuration(msg.getDuration());
            interfaceVOA.setProductChargeModel(msg.getProductChargeModel());
            interfaceVOA.setDescription(null);
            interfaceVOA.setState(InterfaceState.Unpaid);
            interfaceVOA.setMaxModifies(CoreGlobalProperty.INTERFACE_MAX_MOTIFIES);
            interfaceVOA.setExpireDate(dbf.getCurrentSqlTime());
            //分配vlanA
            vlanA = ts.getVlanBySwitch(dbf.findByUuid(switchPortUuidA, SwitchPortVO.class).getSwitchUuid());
            if (vlanA == 0) {
                throw new ApiMessageInterceptionException(argerr("该端口所属虚拟交换机下已无可使用的VLAN，请联系系统管理员 "));
            }

            //分配Z端口
            newBuyInterfaceZ = true;
            String switchPortUuidZ = ts.getSwitchPortByStrategy(msg.getAccountUuid(),msg.getEndpointZUuid(), msg.getPortOfferingUuidZ());
            if (switchPortUuidZ == null) {
                throw new ApiMessageInterceptionException(argerr("该连接点Z下无可用的端口"));
            }
            interfaceVOZ.setUuid(Platform.getUuid());
            interfaceVOZ.setAccountUuid(null);
            interfaceVOZ.setOwnerAccountUuid(msg.getAccountUuid());
            interfaceVOZ.setName(evoZ.getName() + Platform.getUuid().substring(0, 6));
            interfaceVOZ.setEndpointUuid(msg.getEndpointZUuid());
            interfaceVOZ.setSwitchPortUuid(switchPortUuidZ);
            interfaceVOZ.setType(NetworkType.TRUNK);
            interfaceVOZ.setDuration(msg.getDuration());
            interfaceVOZ.setProductChargeModel(msg.getProductChargeModel());
            interfaceVOZ.setDescription(null);
            interfaceVOZ.setState(InterfaceState.Unpaid);
            interfaceVOZ.setMaxModifies(CoreGlobalProperty.INTERFACE_MAX_MOTIFIES);
            interfaceVOZ.setExpireDate(dbf.getCurrentSqlTime());
            //分配vlanZ
            vlanZ = ts.getVlanBySwitch(dbf.findByUuid(switchPortUuidZ, SwitchPortVO.class).getSwitchUuid());
            if (vlanZ == 0) {
                throw new ApiMessageInterceptionException(argerr("该端口所属虚拟交换机下已无可使用的VLAN，请联系系统管理员 "));
            }

            interfaceVOA = dbf.persistAndRefresh(interfaceVOA);
            interfaceVOZ = dbf.persistAndRefresh(interfaceVOZ);

        } else {                                              //有一端为新购
            if (msg.getInterfaceAUuid() == null) {            //A新购，Z已有
                newBuyInterfaceA = true;
                String switchPortUuidA = ts.getSwitchPortByStrategy(msg.getAccountUuid(), msg.getEndpointAUuid(), msg.getPortOfferingUuidA());
                if (switchPortUuidA == null) {
                    throw new ApiMessageInterceptionException(argerr("该连接点A下无可用的端口"));
                }
                //保存数据
                interfaceVOA.setUuid(Platform.getUuid());
                interfaceVOA.setAccountUuid(null);
                interfaceVOA.setOwnerAccountUuid(msg.getAccountUuid());
                interfaceVOA.setName(evoA.getName() + Platform.getUuid().substring(0, 6));
                interfaceVOA.setEndpointUuid(msg.getEndpointAUuid());
                interfaceVOA.setSwitchPortUuid(switchPortUuidA);
                interfaceVOA.setType(NetworkType.TRUNK);
                interfaceVOA.setDuration(msg.getDuration());
                interfaceVOA.setProductChargeModel(msg.getProductChargeModel());
                interfaceVOA.setDescription(null);
                interfaceVOA.setState(InterfaceState.Unpaid);
                interfaceVOA.setMaxModifies(CoreGlobalProperty.INTERFACE_MAX_MOTIFIES);
                interfaceVOA.setExpireDate(dbf.getCurrentSqlTime());
                //分配vlanA
                vlanA = ts.getVlanBySwitch(dbf.findByUuid(switchPortUuidA, SwitchPortVO.class).getSwitchUuid());
                if (vlanA == 0) {
                    throw new ApiMessageInterceptionException(argerr("该端口所属虚拟交换机下已无可使用的VLAN，请联系系统管理员 "));
                }

                if (crossTunnel) {
                    vlanZ = crossVlan;
                } else {
                    vlanZ = ts.getVlanByStrategy(msg.getInterfaceZUuid());
                    if (vlanZ == 0) {
                        throw new ApiMessageInterceptionException(argerr("该端口所属虚拟交换机下已无可使用的VLAN，请联系系统管理员 "));
                    }
                }

                interfaceVOA = dbf.persistAndRefresh(interfaceVOA);
                interfaceVOZ = dbf.findByUuid(msg.getInterfaceZUuid(), InterfaceVO.class);

            } else {                                          //Z新购，A已有
                newBuyInterfaceZ = true;
                String switchPortUuidZ = ts.getSwitchPortByStrategy(msg.getAccountUuid(), msg.getEndpointZUuid(), msg.getPortOfferingUuidZ());
                if (switchPortUuidZ == null) {
                    throw new ApiMessageInterceptionException(argerr("该连接点Z下无可用的端口"));
                }
                //保存数据
                interfaceVOZ.setUuid(Platform.getUuid());
                interfaceVOZ.setAccountUuid(null);
                interfaceVOZ.setOwnerAccountUuid(msg.getAccountUuid());
                interfaceVOZ.setName(evoZ.getName() + Platform.getUuid().substring(0, 6));
                interfaceVOZ.setEndpointUuid(msg.getEndpointZUuid());
                interfaceVOZ.setSwitchPortUuid(switchPortUuidZ);
                interfaceVOZ.setType(NetworkType.TRUNK);
                interfaceVOZ.setDuration(msg.getDuration());
                interfaceVOZ.setProductChargeModel(msg.getProductChargeModel());
                interfaceVOZ.setDescription(null);
                interfaceVOZ.setState(InterfaceState.Unpaid);
                interfaceVOZ.setMaxModifies(CoreGlobalProperty.INTERFACE_MAX_MOTIFIES);
                interfaceVOZ.setExpireDate(dbf.getCurrentSqlTime());
                //分配vlanZ
                vlanZ = ts.getVlanBySwitch(dbf.findByUuid(switchPortUuidZ, SwitchPortVO.class).getSwitchUuid());
                if (vlanZ == 0) {
                    throw new ApiMessageInterceptionException(argerr("该端口所属虚拟交换机下已无可使用的VLAN，请联系系统管理员 "));
                }

                if (crossTunnel) {
                    vlanA = crossVlan;
                } else {
                    vlanA = ts.getVlanByStrategy(msg.getInterfaceAUuid());
                    if (vlanA == 0) {
                        throw new ApiMessageInterceptionException(argerr("该端口所属虚拟交换机下已无可使用的VLAN，请联系系统管理员 "));
                    }
                }

                interfaceVOA = dbf.findByUuid(msg.getInterfaceAUuid(), InterfaceVO.class);
                interfaceVOZ = dbf.persistAndRefresh(interfaceVOZ);

            }

        }

        //创建TunnelSwitchPort
        TunnelSwitchPortVO tsvoA = new TunnelSwitchPortVO();
        tsvoA.setUuid(Platform.getUuid());
        tsvoA.setTunnelUuid(vo.getUuid());
        tsvoA.setInterfaceUuid(interfaceVOA.getUuid());
        tsvoA.setEndpointUuid(msg.getEndpointAUuid());
        tsvoA.setSwitchPortUuid(interfaceVOA.getSwitchPortUuid());
        tsvoA.setType(interfaceVOA.getType());
        tsvoA.setVlan(vlanA);
        tsvoA.setSortTag("A");

        TunnelSwitchPortVO tsvoZ = new TunnelSwitchPortVO();
        tsvoZ.setUuid(Platform.getUuid());
        tsvoZ.setTunnelUuid(vo.getUuid());
        tsvoZ.setInterfaceUuid(interfaceVOZ.getUuid());
        tsvoZ.setEndpointUuid(msg.getEndpointZUuid());
        tsvoZ.setSwitchPortUuid(interfaceVOZ.getSwitchPortUuid());
        tsvoZ.setType(interfaceVOZ.getType());
        tsvoZ.setVlan(vlanZ);
        tsvoZ.setSortTag("Z");

        //如果跨国,将出海口设备添加至TunnelSwitchPort
        if (msg.getInnerConnectedEndpointUuid() != null) {
            if(nvoA.getCountry().equals("CHINA")){
                createTunnelSwitchPortForAbroad(msg.getInnerConnectedEndpointUuid(), vo, true);
            }else{
                createTunnelSwitchPortForAbroad(msg.getInnerConnectedEndpointUuid(), vo, false);
            }

        }

        dbf.persistAndRefresh(tsvoA);
        dbf.persistAndRefresh(tsvoZ);
        vo = dbf.persistAndRefresh(vo);

        afterCreateTunnel(vo, msg, newBuyInterfaceA, newBuyInterfaceZ, interfaceVOA, interfaceVOZ);
    }

    /**
     * 创建云专线的支付和下发
     */
    private void afterCreateTunnel(TunnelVO vo,
                                   APICreateTunnelMsg msg,
                                   boolean newBuyInterfaceA,
                                   boolean newBuyInterfaceZ,
                                   InterfaceVO interfaceVOA,
                                   InterfaceVO interfaceVOZ) {
        APICreateTunnelEvent evt = new APICreateTunnelEvent(msg.getId());

        //支付接口订单
        APICreateBuyOrderMsg orderMsg = new APICreateBuyOrderMsg();
        List<ProductInfoForOrder> products = new ArrayList<>();
        if (newBuyInterfaceA) {
            ProductInfoForOrder productInfoForOrderA = createBuyOrderForInterface(interfaceVOA, msg.getPortOfferingUuidA(), new CreateInterfaceCallBack());
            productInfoForOrderA.setOpAccountUuid(msg.getSession().getAccountUuid());
            //productInfoForOrderA.setNotifyUrl(restf.getSendCommandUrl());
            products.add(productInfoForOrderA);
        }
        if (newBuyInterfaceZ) {
            ProductInfoForOrder productInfoForOrderZ = createBuyOrderForInterface(interfaceVOZ, msg.getPortOfferingUuidZ(), new CreateInterfaceCallBack());
            productInfoForOrderZ.setOpAccountUuid(msg.getSession().getAccountUuid());
            //productInfoForOrderZ.setNotifyUrl(restf.getSendCommandUrl());
            products.add(productInfoForOrderZ);
        }
        //支付专线订单
        ProductInfoForOrder productInfoForOrderTunnel = createBuyOrderForTunnel(vo, msg);

        CreateTunnelCallBack createTunnelCallBack = new CreateTunnelCallBack();
        createTunnelCallBack.setInterfaceAUuid(interfaceVOA.getUuid());
        createTunnelCallBack.setInterfaceZUuid(interfaceVOZ.getUuid());
        createTunnelCallBack.setNewBuyInterfaceA(newBuyInterfaceA);
        createTunnelCallBack.setNewBuyInterfaceZ(newBuyInterfaceZ);

        productInfoForOrderTunnel.setCallBackData(RESTApiDecoder.dump(createTunnelCallBack));

        products.add(productInfoForOrderTunnel);
        orderMsg.setProducts(products);

        //调用支付
        List<OrderInventory> inventories = createBuyOrder(orderMsg);

        if (!inventories.isEmpty()) {     //付款成功
            //支付成功修改状态,记录生效订单
            String orderUuid = null;
            for (OrderInventory orderInventory : inventories) {
                if (orderInventory.getProductType() == ProductType.TUNNEL) {
                    orderUuid = orderInventory.getUuid();
                    break;
                }
            }
            saveResourceOrderEffective(orderUuid, vo.getUuid(), vo.getClass().getSimpleName());
            //修改tunnel状态
            vo.setAccountUuid(vo.getOwnerAccountUuid());
            vo.setState(TunnelState.Deploying);
            vo.setStatus(TunnelStatus.Connecting);
            vo = dbf.updateAndRefresh(vo);
            //修改interface状态,生成到期时间
            if (newBuyInterfaceA) {
                interfaceVOA.setAccountUuid(interfaceVOA.getOwnerAccountUuid());
                interfaceVOA.setState(InterfaceState.Paid);
                interfaceVOA.setExpireDate(getExpireDate(dbf.getCurrentSqlTime(), interfaceVOA.getProductChargeModel(), interfaceVOA.getDuration()));
                dbf.updateAndRefresh(interfaceVOA);
            }
            if (newBuyInterfaceZ) {
                interfaceVOZ.setState(InterfaceState.Paid);
                interfaceVOZ.setAccountUuid(interfaceVOZ.getOwnerAccountUuid());
                interfaceVOZ.setExpireDate(getExpireDate(dbf.getCurrentSqlTime(), interfaceVOZ.getProductChargeModel(), interfaceVOZ.getDuration()));
                dbf.updateAndRefresh(interfaceVOZ);
            }

            //创建任务
            TaskResourceVO taskResourceVO = newTaskResourceVO(vo, TaskType.Create);

            CreateTunnelMsg createTunnelMsg = new CreateTunnelMsg();
            createTunnelMsg.setTunnelUuid(vo.getUuid());
            createTunnelMsg.setTaskUuid(taskResourceVO.getUuid());
            bus.makeLocalServiceId(createTunnelMsg, TunnelConstant.SERVICE_ID);
            bus.send(createTunnelMsg);

        } else {                          //付款失败
            //付款失败
            vo.setExpireDate(dbf.getCurrentSqlTime());
            vo = dbf.updateAndRefresh(vo);
            evt.setError(errf.stringToOperationError("付款失败"));
        }

        evt.setInventory(TunnelInventory.valueOf(vo));
        bus.publish(evt);
    }

    private ProductInfoForOrder createBuyOrderForTunnel(TunnelVO vo, APICreateTunnelMsg msg) {
        EndpointVO evoA = dbf.findByUuid(msg.getEndpointAUuid(), EndpointVO.class);
        EndpointVO evoZ = dbf.findByUuid(msg.getEndpointZUuid(), EndpointVO.class);

        ProductInfoForOrder order = new ProductInfoForOrder();
        order.setProductUuid(vo.getUuid());
        order.setProductType(ProductType.TUNNEL);
        order.setProductChargeModel(vo.getProductChargeModel());
        order.setDuration(vo.getDuration());
        order.setProductName(vo.getName());
        order.setAccountUuid(vo.getOwnerAccountUuid());
        order.setOpAccountUuid(msg.getSession().getAccountUuid());
        order.setDescriptionData(getDescriptionForTunnel(vo));
        order.setUnits(getTunnelPriceUnit(msg.getBandwidthOfferingUuid(), evoA.getNodeUuid(), evoZ.getNodeUuid(), msg.getInnerConnectedEndpointUuid()));
        order.setNotifyUrl(restf.getSendCommandUrl());
        return order;
    }

    @Transactional
    private void handle(APICreateTunnelManualMsg msg) {

        //保存数据
        TunnelVO vo = new TunnelVO();
        BandwidthOfferingVO bandwidthOfferingVO = dbf.findByUuid(msg.getBandwidthOfferingUuid(), BandwidthOfferingVO.class);
        InterfaceVO interfaceVOA = dbf.findByUuid(msg.getInterfaceAUuid(), InterfaceVO.class);
        InterfaceVO interfaceVOZ = dbf.findByUuid(msg.getInterfaceZUuid(), InterfaceVO.class);
        EndpointVO evoA = dbf.findByUuid(interfaceVOA.getEndpointUuid(), EndpointVO.class);
        EndpointVO evoZ = dbf.findByUuid(interfaceVOZ.getEndpointUuid(), EndpointVO.class);
        NodeVO nvoA = dbf.findByUuid(evoA.getNodeUuid(),NodeVO.class);
        NodeVO nvoZ = dbf.findByUuid(evoZ.getNodeUuid(),NodeVO.class);

        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(null);
        vo.setOwnerAccountUuid(msg.getAccountUuid());
        vo.setVsi(getVsiAuto());
        vo.setMonitorCidr(null);
        vo.setName(msg.getName());
        vo.setDuration(msg.getDuration());
        vo.setBandwidth(bandwidthOfferingVO.getBandwidth());
        vo.setState(TunnelState.Unpaid);
        vo.setStatus(TunnelStatus.Disconnected);
        vo.setExpireDate(null);
        vo.setDescription(msg.getDescription());
        vo.setProductChargeModel(msg.getProductChargeModel());
        vo.setMonitorState(TunnelMonitorState.Disabled);
        vo.setMaxModifies(CoreGlobalProperty.TUNNEL_MAX_MOTIFIES);
        vo.setDistance(Distance.getDistance(nvoA.getLongtitude(), nvoA.getLatitude(), nvoZ.getLongtitude(), nvoZ.getLatitude()));

        TunnelSwitchPortVO tsvoA = new TunnelSwitchPortVO();
        tsvoA.setUuid(Platform.getUuid());
        tsvoA.setTunnelUuid(vo.getUuid());
        tsvoA.setInterfaceUuid(msg.getInterfaceAUuid());
        tsvoA.setEndpointUuid(interfaceVOA.getEndpointUuid());
        tsvoA.setSwitchPortUuid(interfaceVOA.getSwitchPortUuid());
        tsvoA.setType(interfaceVOA.getType());
        tsvoA.setVlan(msg.getaVlan());
        tsvoA.setSortTag("A");

        TunnelSwitchPortVO tsvoZ = new TunnelSwitchPortVO();
        tsvoZ.setUuid(Platform.getUuid());
        tsvoZ.setTunnelUuid(vo.getUuid());
        tsvoZ.setInterfaceUuid(msg.getInterfaceZUuid());
        tsvoZ.setEndpointUuid(interfaceVOZ.getEndpointUuid());
        tsvoZ.setSwitchPortUuid(interfaceVOZ.getSwitchPortUuid());
        tsvoZ.setType(interfaceVOZ.getType());
        tsvoZ.setVlan(msg.getzVlan());
        tsvoZ.setSortTag("Z");

        //如果跨国,将出海口设备添加至TunnelSwitchPort
        if (msg.getInnerConnectedEndpointUuid() != null) {
            if(nvoA.getCountry().equals("CHINA")){
                createTunnelSwitchPortForAbroad(msg.getInnerConnectedEndpointUuid(), vo, true);
            }else{
                createTunnelSwitchPortForAbroad(msg.getInnerConnectedEndpointUuid(), vo, false);
            }
        }

        //如果开启Qinq,需要指定内部vlan段
        if (interfaceVOA.getType() == NetworkType.QINQ || interfaceVOZ.getType() == NetworkType.QINQ) {
            List<InnerVlanSegment> vlanSegments = msg.getVlanSegment();
            for (InnerVlanSegment vlanSegment : vlanSegments) {
                QinqVO qvo = new QinqVO();
                qvo.setUuid(Platform.getUuid());
                qvo.setTunnelUuid(vo.getUuid());
                qvo.setStartVlan(vlanSegment.getStartVlan());
                qvo.setEndVlan(vlanSegment.getEndVlan());
                dbf.getEntityManager().persist(qvo);
            }
        }

        dbf.persistAndRefresh(tsvoA);
        dbf.persistAndRefresh(tsvoZ);
        vo = dbf.persistAndRefresh(vo);

        afterCreateTunnelManual(vo, msg);
    }

    /**
     * 手动创建云专线的支付和下发
     */
    private void afterCreateTunnelManual(TunnelVO vo, APICreateTunnelManualMsg msg) {
        APICreateTunnelManualEvent evt = new APICreateTunnelManualEvent(msg.getId());

        //调用支付
        APICreateBuyOrderMsg orderMsg = new APICreateBuyOrderMsg();
        List<ProductInfoForOrder> products = new ArrayList<>();
        ProductInfoForOrder productInfoForOrderTunnel = createBuyOrderForTunnelManual(vo, msg);

        CreateTunnelCallBack createTunnelCallBack = new CreateTunnelCallBack();
        createTunnelCallBack.setInterfaceAUuid(null);
        createTunnelCallBack.setInterfaceZUuid(null);
        createTunnelCallBack.setNewBuyInterfaceA(false);
        createTunnelCallBack.setNewBuyInterfaceZ(false);

        productInfoForOrderTunnel.setCallBackData(RESTApiDecoder.dump(createTunnelCallBack));

        products.add(productInfoForOrderTunnel);
        orderMsg.setProducts(products);
        List<OrderInventory> inventories = createBuyOrder(orderMsg);

        if (inventories.isEmpty()) {
            vo.setExpireDate(dbf.getCurrentSqlTime());
            vo = dbf.updateAndRefresh(vo);
            evt.setError(errf.stringToOperationError("付款失败"));
            evt.setInventory(TunnelInventory.valueOf(vo));
            bus.publish(evt);
            return;
        }

        //支付成功修改状态,记录生效订单
        saveResourceOrderEffective(inventories.get(0).getUuid(), vo.getUuid(), vo.getClass().getSimpleName());

        vo.setAccountUuid(vo.getOwnerAccountUuid());
        vo.setState(TunnelState.Deploying);
        vo.setStatus(TunnelStatus.Connecting);
        vo = dbf.updateAndRefresh(vo);

        //创建任务
        TaskResourceVO taskResourceVO = newTaskResourceVO(vo, TaskType.Create);

        CreateTunnelMsg createTunnelMsg = new CreateTunnelMsg();
        createTunnelMsg.setTunnelUuid(vo.getUuid());
        createTunnelMsg.setTaskUuid(taskResourceVO.getUuid());
        bus.makeLocalServiceId(createTunnelMsg, TunnelConstant.SERVICE_ID);
        bus.send(createTunnelMsg);

        evt.setInventory(TunnelInventory.valueOf(vo));
        bus.publish(evt);
    }

    private ProductInfoForOrder createBuyOrderForTunnelManual(TunnelVO vo, APICreateTunnelManualMsg msg) {
        InterfaceVO interfaceVOA = dbf.findByUuid(msg.getInterfaceAUuid(), InterfaceVO.class);
        InterfaceVO interfaceVOZ = dbf.findByUuid(msg.getInterfaceZUuid(), InterfaceVO.class);
        EndpointVO evoA = dbf.findByUuid(interfaceVOA.getEndpointUuid(), EndpointVO.class);
        EndpointVO evoZ = dbf.findByUuid(interfaceVOZ.getEndpointUuid(), EndpointVO.class);

        ProductInfoForOrder order = new ProductInfoForOrder();
        order.setProductUuid(vo.getUuid());
        order.setProductType(ProductType.TUNNEL);
        order.setProductChargeModel(vo.getProductChargeModel());
        order.setProductName(vo.getName());
        order.setDuration(vo.getDuration());
        order.setAccountUuid(vo.getOwnerAccountUuid());
        order.setOpAccountUuid(msg.getSession().getAccountUuid());
        order.setDescriptionData(getDescriptionForTunnel(vo));
        order.setUnits(getTunnelPriceUnit(msg.getBandwidthOfferingUuid(), evoA.getNodeUuid(), evoZ.getNodeUuid(), msg.getInnerConnectedEndpointUuid()));
        order.setNotifyUrl(restf.getSendCommandUrl());
        return order;
    }

    private void handle(APIUpdateTunnelVlanMsg msg) {
        APIUpdateTunnelVlanEvent evt = new APIUpdateTunnelVlanEvent(msg.getId());

        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);
        TunnelSwitchPortVO tunnelSwitchPortA = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "A")
                .find();
        TunnelSwitchPortVO tunnelSwitchPortZ = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "Z")
                .find();

        FlowChain updatevlan = FlowChainBuilder.newSimpleFlowChain();
        updatevlan.setName(String.format("update-tunnelvlan-%s", msg.getUuid()));
        updatevlan.then(new Flow() {
            @Override
            public void run(FlowTrigger trigger, Map data) {
                if (!Objects.equals(msg.getInterfaceAUuid(), msg.getOldInterfaceAUuid()) || !Objects.equals(msg.getaVlan(), msg.getOldAVlan())) {
                    UpdateQuery.New(TunnelSwitchPortVO.class)
                            .set(TunnelSwitchPortVO_.switchPortUuid, dbf.findByUuid(msg.getInterfaceAUuid(), InterfaceVO.class).getSwitchPortUuid())
                            .set(TunnelSwitchPortVO_.vlan, msg.getaVlan())
                            .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                            .eq(TunnelSwitchPortVO_.sortTag, "A")
                            .update();
                    logger.info("修改A端物理接口或VLAN");
                }

                trigger.next();
            }

            @Override
            public void rollback(FlowRollback trigger, Map data) {
                if (!Objects.equals(msg.getInterfaceAUuid(), msg.getOldInterfaceAUuid()) || !Objects.equals(msg.getaVlan(), msg.getOldAVlan())) {
                    dbf.updateAndRefresh(tunnelSwitchPortA);
                    logger.info("回滚A端物理接口或VLAN");
                }

                trigger.rollback();
            }
        }).then(new Flow() {
            @Override
            public void run(FlowTrigger trigger, Map data) {
                if (!Objects.equals(msg.getInterfaceZUuid(), msg.getOldInterfaceZUuid()) || !Objects.equals(msg.getzVlan(), msg.getOldZVlan())) {
                    UpdateQuery.New(TunnelSwitchPortVO.class)
                            .set(TunnelSwitchPortVO_.switchPortUuid, dbf.findByUuid(msg.getInterfaceZUuid(), InterfaceVO.class).getSwitchPortUuid())
                            .set(TunnelSwitchPortVO_.vlan, msg.getzVlan())
                            .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                            .eq(TunnelSwitchPortVO_.sortTag, "Z")
                            .update();
                    logger.info("修改Z端物理接口或VLAN");
                }

                trigger.next();
            }

            @Override
            public void rollback(FlowRollback trigger, Map data) {
                if (!Objects.equals(msg.getInterfaceZUuid(), msg.getOldInterfaceZUuid()) || !Objects.equals(msg.getzVlan(), msg.getOldZVlan())) {
                    dbf.updateAndRefresh(tunnelSwitchPortZ);
                    logger.info("回滚Z端物理接口或VLAN");
                }
                trigger.rollback();
            }
        }).then(new Flow() {
            @Override
            public void run(FlowTrigger trigger, Map data) {
                if (!Objects.equals(msg.getInterfaceAUuid(), msg.getOldInterfaceAUuid()) || !Objects.equals(msg.getaVlan(), msg.getOldAVlan()) || !Objects.equals(msg.getInterfaceZUuid(), msg.getOldInterfaceZUuid()) || !Objects.equals(msg.getzVlan(), msg.getOldZVlan())) {
                    //创建任务
                    TaskResourceVO taskResourceVO = newTaskResourceVO(vo, TaskType.ModifyPorts);

                    ModifyTunnelPortsMsg modifyTunnelPortsMsg = new ModifyTunnelPortsMsg();
                    modifyTunnelPortsMsg.setTunnelUuid(vo.getUuid());
                    modifyTunnelPortsMsg.setTaskUuid(taskResourceVO.getUuid());
                    bus.makeLocalServiceId(modifyTunnelPortsMsg, TunnelConstant.SERVICE_ID);
                    bus.send(modifyTunnelPortsMsg, new CloudBusCallBack(null) {
                        @Override
                        public void run(MessageReply reply) {
                            if (reply.isSuccess()) {
                                logger.info(String.format("Successfully update vlan of tunnel[uuid:%s].", vo.getUuid()));
                            } else {
                                logger.info(String.format("Failed update vlan of tunnel[uuid:%s].", vo.getUuid()));
                                trigger.fail(reply.getError());
                            }
                        }
                    });
                }

                trigger.next();
            }

            @Override
            public void rollback(FlowRollback trigger, Map data) {
                trigger.rollback();
            }
        }).done(new FlowDoneHandler(null) {
            @Override
            public void handle(Map data) {
                evt.setInventory(TunnelInventory.valueOf(vo));
            }
        }).error(new FlowErrorHandler(null) {
            @Override
            public void handle(ErrorCode errCode, Map data) {
                evt.setError(errf.stringToOperationError("update vlan failed!"));
            }
        }).start();

        bus.publish(evt);

    }

    private void handle(APIUpdateForciblyTunnelVlanMsg msg) {
        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);
        boolean updateA = false;
        boolean updateZ = false;
        TunnelSwitchPortVO tunnelSwitchPortA = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "A")
                .find();
        TunnelSwitchPortVO tunnelSwitchPortZ = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "Z")
                .find();
        if (!msg.getInterfaceAUuid().equals(msg.getOldInterfaceAUuid())) {
            tunnelSwitchPortA.setSwitchPortUuid(dbf.findByUuid(msg.getInterfaceAUuid(), InterfaceVO.class).getSwitchPortUuid());
            tunnelSwitchPortA.setVlan(msg.getaVlan());
            updateA = true;
        } else {
            if (!Objects.equals(msg.getaVlan(), msg.getOldAVlan())) {
                tunnelSwitchPortA.setVlan(msg.getaVlan());
                updateA = true;
            }
        }
        if (!msg.getInterfaceZUuid().equals(msg.getOldInterfaceZUuid())) {
            tunnelSwitchPortZ.setSwitchPortUuid(dbf.findByUuid(msg.getInterfaceZUuid(), InterfaceVO.class).getSwitchPortUuid());
            tunnelSwitchPortZ.setVlan(msg.getzVlan());
            updateZ = true;
        } else {
            if (!Objects.equals(msg.getzVlan(), msg.getOldZVlan())) {
                tunnelSwitchPortZ.setVlan(msg.getzVlan());
                updateZ = true;
            }
        }
        if (updateA)
            dbf.updateAndRefresh(tunnelSwitchPortA);
        if (updateZ)
            dbf.updateAndRefresh(tunnelSwitchPortZ);

        APIUpdateForciblyTunnelVlanEvent evt = new APIUpdateForciblyTunnelVlanEvent(msg.getId());
        evt.setInventory(TunnelInventory.valueOf(vo));
        bus.publish(evt);

    }

    private void handle(APIUpdateTunnelMsg msg) {
        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);
        boolean update = false;


        if (msg.getName() != null) {
            vo.setName(msg.getName());
            update = true;
        }
        if (msg.getDistance() != null) {
            vo.setDistance(msg.getDistance());
            update = true;
        }
        if (msg.getState() != null) {
            vo.setState(msg.getState());
            update = true;
        }
        if (msg.getStatus() != null) {
            vo.setStatus(msg.getStatus());
            update = true;
        }
        if (msg.getMonitorState() != null) {
            vo.setMonitorState(msg.getMonitorState());
            update = true;
        }
        if (msg.getDescription() != null) {
            vo.setDescription(msg.getDescription());
            update = true;
        }

        if (update)
            vo = dbf.updateAndRefresh(vo);

        APIUpdateTunnelEvent evt = new APIUpdateTunnelEvent(msg.getId());
        evt.setInventory(TunnelInventory.valueOf(vo));
        bus.publish(evt);
    }

    @Transactional
    private void handle(APIUpdateTunnelBandwidthMsg msg) {
        APIUpdateTunnelBandwidthEvent evt = new APIUpdateTunnelBandwidthEvent(msg.getId());

        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);

        String nodeAUuid = getNodeUuid(vo, "A");
        String nodeZUuid = getNodeUuid(vo, "Z");

        String innerEndpointUuid = null;
        TunnelSwitchPortVO tunnelSwitchPort = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, vo.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "B")
                .find();
        if (tunnelSwitchPort != null) {
            innerEndpointUuid = tunnelSwitchPort.getEndpointUuid();
        }

        BandwidthOfferingVO bandwidthOfferingVO = dbf.findByUuid(msg.getBandwidthOfferingUuid(), BandwidthOfferingVO.class);

        //调整次数记录表
        TunnelMotifyRecordVO record = new TunnelMotifyRecordVO();
        record.setUuid(Platform.getUuid());
        record.setTunnelUuid(vo.getUuid());
        record.setOpAccountUuid(msg.getSession().getAccountUuid());
        record.setMotifyType(bandwidthOfferingVO.getBandwidth() > vo.getBandwidth() ? MotifyType.UPGRADE : MotifyType.DEMOTION);
        dbf.persistAndRefresh(record);

        //调用支付-调整带宽
        UpdateTunnelBandwidthCallBack uc = new UpdateTunnelBandwidthCallBack();
        uc.setBandwidth(bandwidthOfferingVO.getBandwidth());

        APICreateModifyOrderMsg orderMsg = new APICreateModifyOrderMsg();
        orderMsg.setProductUuid(vo.getUuid());
        orderMsg.setProductName(vo.getName());
        orderMsg.setDescriptionData(getDescriptionForTunnel(vo));
        orderMsg.setCallBackData(RESTApiDecoder.dump(uc));
        orderMsg.setUnits(getTunnelPriceUnit(msg.getBandwidthOfferingUuid(), nodeAUuid,
                nodeZUuid, innerEndpointUuid));
        orderMsg.setProductType(ProductType.TUNNEL);
        orderMsg.setAccountUuid(msg.getAccountUuid());
        orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
        orderMsg.setStartTime(dbf.getCurrentSqlTime());
        orderMsg.setExpiredTime(vo.getExpireDate());
        orderMsg.setNotifyUrl(restf.getSendCommandUrl());

        OrderInventory orderInventory = createOrder(orderMsg);
        if (orderInventory != null) {
            //付款成功,记录生效订单
            saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());

            vo.setBandwidth(bandwidthOfferingVO.getBandwidth());
            vo = dbf.updateAndRefresh(vo);

            //创建任务
            TaskResourceVO taskResourceVO = newTaskResourceVO(vo, TaskType.ModifyBandwidth);

            ModifyTunnelBandwidthMsg modifyTunnelBandwidthMsg = new ModifyTunnelBandwidthMsg();
            modifyTunnelBandwidthMsg.setTunnelUuid(vo.getUuid());
            modifyTunnelBandwidthMsg.setTaskUuid(taskResourceVO.getUuid());
            bus.makeLocalServiceId(modifyTunnelBandwidthMsg, TunnelConstant.SERVICE_ID);
            bus.send(modifyTunnelBandwidthMsg);
            evt.setInventory(TunnelInventory.valueOf(vo));
        } else {
            evt.setError(errf.stringToOperationError("订单操作失败"));
        }

        bus.publish(evt);
    }

    private void handle(APIRenewTunnelMsg msg) {
        APIRenewTunnelReply reply
                = renewTunnel(msg.getUuid(),
                msg.getDuration(),
                msg.getProductChargeModel(),
                msg.getAccountUuid(),
                msg.getSession().getAccountUuid());

        bus.reply(msg, reply);
    }

    private void handle(APIRenewAutoTunnelMsg msg) {

        APIRenewTunnelReply reply
                = renewTunnel(msg.getUuid(),
                msg.getDuration(),
                msg.getProductChargeModel(),
                msg.getAccountUuid(),
                msg.getSession().getAccountUuid());

        bus.reply(msg, reply);
    }

    private APIRenewTunnelReply renewTunnel(String uuid,
                                            Integer duration,
                                            ProductChargeModel productChargeModel,
                                            String accountUuid,
                                            String opAccountUuid) {

        APIRenewTunnelReply reply = new APIRenewTunnelReply();

        TunnelVO vo = dbf.findByUuid(uuid, TunnelVO.class);
        Timestamp newTime = vo.getExpireDate();

        //续费
        RenewTunnelCallBack rc = new RenewTunnelCallBack();
        APICreateRenewOrderMsg renewOrderMsg = new APICreateRenewOrderMsg();
        renewOrderMsg.setProductUuid(vo.getUuid());
        renewOrderMsg.setProductName(vo.getName());
        renewOrderMsg.setProductType(ProductType.TUNNEL);
        renewOrderMsg.setDuration(duration);
        renewOrderMsg.setDescriptionData(getDescriptionForTunnel(vo));
        renewOrderMsg.setProductChargeModel(productChargeModel);
        renewOrderMsg.setAccountUuid(accountUuid);
        renewOrderMsg.setOpAccountUuid(opAccountUuid);
        renewOrderMsg.setStartTime(dbf.getCurrentSqlTime());
        renewOrderMsg.setExpiredTime(vo.getExpireDate());
        renewOrderMsg.setNotifyUrl(restf.getSendCommandUrl());
        renewOrderMsg.setCallBackData(RESTApiDecoder.dump(rc));

        OrderInventory orderInventory = createOrder(renewOrderMsg);

        if (orderInventory != null) {
            //续费或者自动续费成功,记录生效订单
            saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());
            //更新到期时间
            vo.setDuration(duration);
            vo.setProductChargeModel(productChargeModel);
            vo.setExpireDate(getExpireDate(newTime, productChargeModel, duration));

            vo = dbf.updateAndRefresh(vo);
            reply.setInventory(TunnelInventory.valueOf(vo));
        } else {
            reply.setError(errf.stringToOperationError("订单操作失败"));
        }

        return reply;
    }

    private void handle(APISalTunnelMsg msg) {
        APISalTunnelReply reply = new APISalTunnelReply();

        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);
        Timestamp newTime = vo.getExpireDate();

        //赔偿
        SalTunnelCallBack sc = new SalTunnelCallBack();
        APICreateSLACompensationOrderMsg slaCompensationOrderMsg =
                new APICreateSLACompensationOrderMsg();
        slaCompensationOrderMsg.setProductUuid(vo.getUuid());
        slaCompensationOrderMsg.setProductName(vo.getName());
        slaCompensationOrderMsg.setDescriptionData(getDescriptionForTunnel(vo));
        slaCompensationOrderMsg.setProductType(ProductType.TUNNEL);
        slaCompensationOrderMsg.setDuration(msg.getDuration());
        slaCompensationOrderMsg.setAccountUuid(msg.getAccountUuid());
        slaCompensationOrderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
        slaCompensationOrderMsg.setStartTime(dbf.getCurrentSqlTime());
        slaCompensationOrderMsg.setExpiredTime(vo.getExpireDate());
        slaCompensationOrderMsg.setCallBackData(RESTApiDecoder.dump(sc));
        slaCompensationOrderMsg.setNotifyUrl(restf.getSendCommandUrl());

        OrderInventory orderInventory = createOrder(slaCompensationOrderMsg);

        if (orderInventory != null) {
            //续费或者赔偿成功,记录生效订单
            saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());
            //更新到期时间
            vo.setDuration(msg.getDuration());
            vo.setProductChargeModel(ProductChargeModel.BY_DAY);
            vo.setExpireDate(getExpireDate(newTime, ProductChargeModel.BY_DAY, msg.getDuration()));

            vo = dbf.updateAndRefresh(vo);
            reply.setInventory(TunnelInventory.valueOf(vo));
        } else {
            reply.setError(errf.stringToOperationError("订单操作失败"));
        }

        bus.reply(msg, reply);
    }

    @Transactional
    private void handle(APIDeleteTunnelMsg msg) {
        APIDeleteTunnelEvent evt = new APIDeleteTunnelEvent(msg.getId());

        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);

        if (vo.getState() == TunnelState.Unsupport) {         //仅删除：无法开通
            deleteTunnel(vo);
            evt.setInventory(TunnelInventory.valueOf(vo));
        } else {
            if (vo.getAccountUuid() == null) {                  //仅下发删除：退订成功但是下发失败了的再次下发，不需要再退订
                //创建任务
                TaskResourceVO taskResourceVO = newTaskResourceVO(vo, TaskType.Delete);

                DeleteTunnelMsg deleteTunnelMsg = new DeleteTunnelMsg();
                deleteTunnelMsg.setTunnelUuid(vo.getUuid());
                deleteTunnelMsg.setTaskUuid(taskResourceVO.getUuid());
                bus.makeLocalServiceId(deleteTunnelMsg, TunnelConstant.SERVICE_ID);
                bus.send(deleteTunnelMsg);

                evt.setInventory(TunnelInventory.valueOf(vo));
            } else {
                //调用退订
                DeleteTunnelCallBack dc = new DeleteTunnelCallBack();
                dc.setDescription("delete");
                APICreateUnsubcribeOrderMsg orderMsg = new APICreateUnsubcribeOrderMsg();
                orderMsg.setProductUuid(vo.getUuid());
                orderMsg.setProductType(ProductType.TUNNEL);
                orderMsg.setProductName(vo.getName());
                orderMsg.setAccountUuid(msg.getAccountUuid());
                orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
                orderMsg.setStartTime(dbf.getCurrentSqlTime());
                orderMsg.setExpiredTime(vo.getExpireDate());
                orderMsg.setDescriptionData(getDescriptionForTunnel(vo));
                orderMsg.setCallBackData(RESTApiDecoder.dump(dc));
                orderMsg.setNotifyUrl(restf.getSendCommandUrl());

                OrderInventory orderInventory = createOrder(orderMsg);
                if (orderInventory == null) {
                    evt.setError(errf.stringToOperationError("退订失败"));
                } else {
                    if (vo.getState() == TunnelState.Enabled) {          //退订下发删除：对于已开通的产品
                        //退订成功,记录生效订单
                        saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());

                        vo.setAccountUuid(null);
                        dbf.updateAndRefresh(vo);

                        //创建任务
                        TaskResourceVO taskResourceVO = newTaskResourceVO(vo, TaskType.Delete);

                        DeleteTunnelMsg deleteTunnelMsg = new DeleteTunnelMsg();
                        deleteTunnelMsg.setTunnelUuid(vo.getUuid());
                        deleteTunnelMsg.setTaskUuid(taskResourceVO.getUuid());
                        bus.makeLocalServiceId(deleteTunnelMsg, TunnelConstant.SERVICE_ID);
                        bus.send(deleteTunnelMsg);

                        evt.setInventory(TunnelInventory.valueOf(vo));
                    } else {                                            //退订删除：对于已关闭的产品
                        //退订成功,记录生效订单
                        saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());
                        deleteTunnel(vo);
                        evt.setInventory(TunnelInventory.valueOf(vo));
                    }
                }

            }
        }

        bus.publish(evt);
    }

    private void handle(APIDeleteForciblyTunnelMsg msg) {
        APIDeleteForciblyTunnelEvent evt = new APIDeleteForciblyTunnelEvent(msg.getId());

        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);

        //调用退订
        DeleteTunnelCallBack dc = new DeleteTunnelCallBack();
        dc.setDescription("forciblydelete");
        APICreateUnsubcribeOrderMsg orderMsg = new APICreateUnsubcribeOrderMsg();
        orderMsg.setProductUuid(vo.getUuid());
        orderMsg.setProductType(ProductType.TUNNEL);
        orderMsg.setProductName(vo.getName());
        orderMsg.setAccountUuid(msg.getAccountUuid());
        orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
        orderMsg.setStartTime(dbf.getCurrentSqlTime());
        if (vo.getExpireDate() == null) {
            orderMsg.setExpiredTime(dbf.getCurrentSqlTime());
            orderMsg.setCreateFailure(true);
        } else {
            orderMsg.setExpiredTime(vo.getExpireDate());
        }
        orderMsg.setDescriptionData(getDescriptionForTunnel(vo));
        orderMsg.setCallBackData(RESTApiDecoder.dump(dc));
        orderMsg.setNotifyUrl(restf.getSendCommandUrl());

        OrderInventory orderInventory = createOrder(orderMsg);
        if (orderInventory != null) {
            //退订成功,记录生效订单
            saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());

            deleteTunnel(vo);

            evt.setInventory(TunnelInventory.valueOf(vo));
        } else {
            evt.setError(errf.stringToOperationError("退订失败"));
        }
        bus.publish(evt);
    }

    private void handle(APIUpdateTunnelStateMsg msg) {
        APIUpdateTunnelStateEvent evt = new APIUpdateTunnelStateEvent(msg.getId());

        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);

        if (vo.getState() == TunnelState.Deployfailure) {  //开通和无法开通
            if (msg.isUnsupport()) {
                //调用退订
                DeleteTunnelCallBack dc = new DeleteTunnelCallBack();
                dc.setDescription("unsupport");
                APICreateUnsubcribeOrderMsg orderMsg = new APICreateUnsubcribeOrderMsg();
                orderMsg.setProductUuid(vo.getUuid());
                orderMsg.setProductType(ProductType.TUNNEL);
                orderMsg.setProductName(vo.getName());
                orderMsg.setAccountUuid(vo.getAccountUuid());
                orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
                orderMsg.setStartTime(dbf.getCurrentSqlTime());
                orderMsg.setExpiredTime(dbf.getCurrentSqlTime());
                orderMsg.setCreateFailure(true);
                orderMsg.setDescriptionData(getDescriptionForTunnel(vo));
                orderMsg.setCallBackData(RESTApiDecoder.dump(dc));
                orderMsg.setNotifyUrl(restf.getSendCommandUrl());

                OrderInventory orderInventory = createOrder(orderMsg);
                if (orderInventory == null) {
                    evt.setError(errf.stringToOperationError("退订失败"));
                } else {
                    //退订成功,记录生效订单
                    saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());
                    vo.setState(TunnelState.Unsupport);
                    vo.setExpireDate(dbf.getCurrentSqlTime());
                    dbf.updateAndRefresh(vo);
                }
            } else {
                if (msg.isSaveOnly()) {

                    vo.setState(TunnelState.Enabled);
                    vo.setStatus(TunnelStatus.Connected);
                    if (vo.getProductChargeModel() == ProductChargeModel.BY_MONTH) {
                        vo.setExpireDate(Timestamp.valueOf(LocalDateTime.now().plusMonths(vo.getDuration())));
                    } else if (vo.getProductChargeModel() == ProductChargeModel.BY_YEAR) {
                        vo.setExpireDate(Timestamp.valueOf(LocalDateTime.now().plusYears(vo.getDuration())));
                    }

                    dbf.updateAndRefresh(vo);
                } else {
                    //创建任务
                    TaskResourceVO taskResourceVO = newTaskResourceVO(vo, TaskType.Enabled);

                    EnabledTunnelMsg enabledTunnelMsg = new EnabledTunnelMsg();
                    enabledTunnelMsg.setTunnelUuid(vo.getUuid());
                    enabledTunnelMsg.setTaskUuid(taskResourceVO.getUuid());
                    bus.makeLocalServiceId(enabledTunnelMsg, TunnelConstant.SERVICE_ID);
                    bus.send(enabledTunnelMsg);
                }
            }

        } else {                                           //恢复连接和关闭连接
            //创建任务
            TaskResourceVO taskResourceVO = newTaskResourceVO(vo, TaskType.valueOf(msg.getState().toString()));

            if (msg.getState() == TunnelState.Enabled) {
                EnabledTunnelMsg enabledTunnelMsg = new EnabledTunnelMsg();
                enabledTunnelMsg.setTunnelUuid(vo.getUuid());
                enabledTunnelMsg.setTaskUuid(taskResourceVO.getUuid());
                bus.makeLocalServiceId(enabledTunnelMsg, TunnelConstant.SERVICE_ID);
                bus.send(enabledTunnelMsg);
            } else {
                DisabledTunnelMsg disabledTunnelMsg = new DisabledTunnelMsg();
                disabledTunnelMsg.setTunnelUuid(vo.getUuid());
                disabledTunnelMsg.setTaskUuid(taskResourceVO.getUuid());
                bus.makeLocalServiceId(disabledTunnelMsg, TunnelConstant.SERVICE_ID);
                bus.send(disabledTunnelMsg);
            }
        }

        evt.setInventory(TunnelInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APICreateQinqMsg msg) {
        APICreateQinqEvent evt = new APICreateQinqEvent(msg.getId());
        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);

        String uuid = Platform.getUuid();
        QinqVO qinqVO = new QinqVO();
        qinqVO.setUuid(uuid);
        qinqVO.setTunnelUuid(msg.getUuid());
        qinqVO.setStartVlan(msg.getStartVlan());
        qinqVO.setEndVlan(msg.getEndVlan());

        FlowChain createQinq = FlowChainBuilder.newSimpleFlowChain();
        createQinq.setName(String.format("create-tunnel-%s-innervlan", msg.getUuid()));
        createQinq.then(new Flow() {
            @Override
            public void run(FlowTrigger trigger, Map data) {

                dbf.persistAndRefresh(qinqVO);

                logger.info("新增一段QINQ");
                trigger.next();
            }

            @Override
            public void rollback(FlowRollback trigger, Map data) {
                UpdateQuery.New(QinqVO.class)
                        .eq(QinqVO_.uuid, uuid)
                        .hardDelete();

                logger.info("回滚QINQ");
                trigger.rollback();
            }
        }).then(new Flow() {
            @Override
            public void run(FlowTrigger trigger, Map data) {
                //创建任务
                TaskResourceVO taskResourceVO = newTaskResourceVO(vo, TaskType.ModifyPorts);

                ModifyTunnelPortsMsg modifyTunnelPortsMsg = new ModifyTunnelPortsMsg();
                modifyTunnelPortsMsg.setTunnelUuid(vo.getUuid());
                modifyTunnelPortsMsg.setTaskUuid(taskResourceVO.getUuid());
                bus.makeLocalServiceId(modifyTunnelPortsMsg, TunnelConstant.SERVICE_ID);
                bus.send(modifyTunnelPortsMsg, new CloudBusCallBack(null) {
                    @Override
                    public void run(MessageReply reply) {
                        if (reply.isSuccess()) {
                            logger.info(String.format("新增 tunnel[uuid:%s] 内部VLAN 成功.", vo.getUuid()));
                        } else {
                            logger.info(String.format("新增 tunnel[uuid:%s] 内部VLAN 失败.", vo.getUuid()));
                            trigger.fail(reply.getError());
                        }
                    }
                });

                trigger.next();
            }

            @Override
            public void rollback(FlowRollback trigger, Map data) {
                trigger.rollback();
            }
        }).done(new FlowDoneHandler(null) {
            @Override
            public void handle(Map data) {
                evt.setInventory(QinqInventory.valueOf(qinqVO));
            }
        }).error(new FlowErrorHandler(null) {
            @Override
            public void handle(ErrorCode errCode, Map data) {
                evt.setError(errf.stringToOperationError("create innervlan failed!"));
            }
        }).start();

        bus.publish(evt);
    }

    private void handle(APIDeleteQinqMsg msg) {
        APIDeleteQinqEvent evt = new APIDeleteQinqEvent(msg.getId());
        QinqVO qinqVO = dbf.findByUuid(msg.getUuid(), QinqVO.class);
        TunnelVO vo = dbf.findByUuid(qinqVO.getTunnelUuid(), TunnelVO.class);

        FlowChain deleteQinq = FlowChainBuilder.newSimpleFlowChain();
        deleteQinq.setName(String.format("delete-tunnel-%s-innervlan", vo.getUuid()));
        deleteQinq.then(new Flow() {
            @Override
            public void run(FlowTrigger trigger, Map data) {
                dbf.remove(qinqVO);

                logger.info("删除一段QINQ");
                trigger.next();
            }

            @Override
            public void rollback(FlowRollback trigger, Map data) {
                dbf.persistAndRefresh(qinqVO);

                logger.info("回滚QINQ");
                trigger.rollback();
            }
        }).then(new Flow() {
            @Override
            public void run(FlowTrigger trigger, Map data) {
                //创建任务
                TaskResourceVO taskResourceVO = newTaskResourceVO(vo, TaskType.ModifyPorts);

                ModifyTunnelPortsMsg modifyTunnelPortsMsg = new ModifyTunnelPortsMsg();
                modifyTunnelPortsMsg.setTunnelUuid(vo.getUuid());
                modifyTunnelPortsMsg.setTaskUuid(taskResourceVO.getUuid());
                bus.makeLocalServiceId(modifyTunnelPortsMsg, TunnelConstant.SERVICE_ID);
                bus.send(modifyTunnelPortsMsg, new CloudBusCallBack(null) {
                    @Override
                    public void run(MessageReply reply) {
                        if (reply.isSuccess()) {
                            logger.info(String.format("删除 tunnel[uuid:%s] 内部VLAN 成功.", vo.getUuid()));
                        } else {
                            logger.info(String.format("删除 tunnel[uuid:%s] 内部VLAN 失败.", vo.getUuid()));
                            trigger.fail(reply.getError());
                        }
                    }
                });

                trigger.next();
            }

            @Override
            public void rollback(FlowRollback trigger, Map data) {
                trigger.rollback();
            }
        }).done(new FlowDoneHandler(null) {
            @Override
            public void handle(Map data) {
                evt.setInventory(QinqInventory.valueOf(qinqVO));
            }
        }).error(new FlowErrorHandler(null) {
            @Override
            public void handle(ErrorCode errCode, Map data) {
                evt.setError(errf.stringToOperationError("delete innervlan failed!"));
            }
        }).start();

        bus.publish(evt);
    }

    private void handle(APIQueryTunnelDetailForAlarmMsg msg) {
        Map<String, Object> detailMap = new HashMap<>();

        FalconApiCommands.Tunnel tunnelCmd = new FalconApiCommands.Tunnel();
        for (String tunnelUuid : msg.getTunnelUuidList()) {
            TunnelVO tunnel = Q.New(TunnelVO.class).eq(TunnelVO_.uuid, tunnelUuid).findValue();
            tunnelCmd.setTunnel_id(tunnel.getUuid());
            tunnelCmd.setBandwidth(tunnel.getBandwidth());

            List<TunnelSwitchPortVO> tunnelSwitchPortVOS = Q.New(TunnelSwitchPortVO.class).eq(TunnelSwitchPortVO_.tunnelUuid, tunnelUuid).list();
            for (TunnelSwitchPortVO vo : tunnelSwitchPortVOS) {
                if ("A".equals(vo.getSortTag())) {
                    tunnelCmd.setEndpointA_ip(getPhysicalSwitch(vo.getSwitchPortUuid()));
                    tunnelCmd.setEndpointA_vid(vo.getVlan());
                } else if ("Z".equals(vo.getSortTag())) {
                    tunnelCmd.setEndpointB_ip(getPhysicalSwitch(vo.getSwitchPortUuid()));
                    tunnelCmd.setEndpointB_vid(vo.getVlan());
                }
            }

            detailMap.put(tunnelUuid, tunnelCmd);
        }

        APIQueryTunnelDetailForAlarmReply reply = new APIQueryTunnelDetailForAlarmReply();
        reply.setMap(detailMap);
        bus.reply(msg, reply);
    }

    //////////////////////////////////////////                 billing call back                  /////////////////////////////////////////

    private boolean orderIsExist(String orderUuid) {
        return Q.New(ResourceOrderEffectiveVO.class)
                .eq(ResourceOrderEffectiveVO_.orderUuid, orderUuid)
                .isExists();
    }

    private void renewOrSla(OrderCallbackCmd cmd) {
        if (cmd.getProductType() == ProductType.PORT) {
            InterfaceVO vo = dbf.findByUuid(cmd.getPorductUuid(), InterfaceVO.class);
            vo.setExpireDate(getExpireDate(vo.getExpireDate(), cmd.getProductChargeModel(), cmd.getDuration()));
            vo.setDuration(cmd.getDuration());
            vo.setProductChargeModel(cmd.getProductChargeModel());
            dbf.updateAndRefresh(vo);
            //付款成功,记录生效订单
            saveResourceOrderEffective(cmd.getOrderUuid(), vo.getUuid(), vo.getClass().getSimpleName());

        } else if (cmd.getProductType() == ProductType.TUNNEL) {
            TunnelVO vo = dbf.findByUuid(cmd.getPorductUuid(), TunnelVO.class);
            vo.setExpireDate(getExpireDate(vo.getExpireDate(), cmd.getProductChargeModel(), cmd.getDuration()));
            vo.setDuration(cmd.getDuration());
            vo.setProductChargeModel(cmd.getProductChargeModel());
            dbf.updateAndRefresh(vo);
            //付款成功,记录生效订单
            saveResourceOrderEffective(cmd.getOrderUuid(), vo.getUuid(), vo.getClass().getSimpleName());
        }
    }

    private void buyTunnel(OrderCallbackCmd cmd, CreateTunnelCallBack message) {
        if (message.isNewBuyInterfaceA()) {
            InterfaceVO vo = dbf.findByUuid(message.getInterfaceAUuid(), InterfaceVO.class);
            vo.setAccountUuid(vo.getOwnerAccountUuid());
            vo.setState(InterfaceState.Paid);
            vo.setExpireDate(getExpireDate(dbf.getCurrentSqlTime(), cmd.getProductChargeModel(), cmd.getDuration()));
            dbf.updateAndRefresh(vo);
        }
        if (message.isNewBuyInterfaceZ()) {
            InterfaceVO vo = dbf.findByUuid(message.getInterfaceZUuid(), InterfaceVO.class);
            vo.setAccountUuid(vo.getOwnerAccountUuid());
            vo.setState(InterfaceState.Paid);
            vo.setExpireDate(getExpireDate(dbf.getCurrentSqlTime(), cmd.getProductChargeModel(), cmd.getDuration()));
            dbf.updateAndRefresh(vo);
        }

        TunnelVO vo = dbf.findByUuid(cmd.getPorductUuid(), TunnelVO.class);
        vo.setAccountUuid(vo.getOwnerAccountUuid());
        vo.setState(TunnelState.Deploying);
        vo.setStatus(TunnelStatus.Connecting);
        dbf.updateAndRefresh(vo);
        //付款成功,记录生效订单
        saveResourceOrderEffective(cmd.getOrderUuid(), vo.getUuid(), vo.getClass().getSimpleName());

        //创建任务
        TaskResourceVO taskResourceVO = newTaskResourceVO(vo, TaskType.Create);

        CreateTunnelMsg createTunnelMsg = new CreateTunnelMsg();
        createTunnelMsg.setTunnelUuid(vo.getUuid());
        createTunnelMsg.setTaskUuid(taskResourceVO.getUuid());
        bus.makeLocalServiceId(createTunnelMsg, TunnelConstant.SERVICE_ID);
        bus.send(createTunnelMsg);

    }

    private void buyInterface(OrderCallbackCmd cmd) {

        InterfaceVO vo = dbf.findByUuid(cmd.getPorductUuid(), InterfaceVO.class);
        vo.setAccountUuid(vo.getOwnerAccountUuid());
        vo.setState(InterfaceState.Paid);
        vo.setExpireDate(getExpireDate(dbf.getCurrentSqlTime(), cmd.getProductChargeModel(), cmd.getDuration()));
        dbf.updateAndRefresh(vo);
        //付款成功,记录生效订单
        saveResourceOrderEffective(cmd.getOrderUuid(), vo.getUuid(), vo.getClass().getSimpleName());

    }

    private void unsubcribeInterface(OrderCallbackCmd cmd) {
        InterfaceVO vo = dbf.findByUuid(cmd.getPorductUuid(), InterfaceVO.class);
        saveResourceOrderEffective(cmd.getOrderUuid(), vo.getUuid(), vo.getClass().getSimpleName());
        dbf.remove(vo);

    }

    private void unsubcribeTunnel(OrderCallbackCmd cmd, DeleteTunnelCallBack message) {

        TunnelVO vo = dbf.findByUuid(cmd.getPorductUuid(), TunnelVO.class);

        //退订成功，记录生效订单
        saveResourceOrderEffective(cmd.getOrderUuid(), vo.getUuid(), vo.getClass().getSimpleName());
        if (message.getDescription().equals("forciblydelete")) {
            deleteTunnel(vo);
        } else if (message.getDescription().equals("delete") && vo.getState() == TunnelState.Enabled) {
            vo.setAccountUuid(null);
            dbf.updateAndRefresh(vo);

            //创建任务
            TaskResourceVO taskResourceVO = newTaskResourceVO(vo, TaskType.Delete);

            DeleteTunnelMsg deleteTunnelMsg = new DeleteTunnelMsg();
            deleteTunnelMsg.setTunnelUuid(vo.getUuid());
            deleteTunnelMsg.setTaskUuid(taskResourceVO.getUuid());
            bus.makeLocalServiceId(deleteTunnelMsg, TunnelConstant.SERVICE_ID);
            bus.send(deleteTunnelMsg);
        } else if (message.getDescription().equals("delete") && vo.getState() == TunnelState.Disabled) {
            deleteTunnel(vo);
        } else if (message.getDescription().equals("unsupport")) {
            vo.setState(TunnelState.Unsupport);
            vo.setExpireDate(dbf.getCurrentSqlTime());
            dbf.updateAndRefresh(vo);
        }

    }

    private void modifyBandwidth(OrderCallbackCmd cmd, UpdateTunnelBandwidthCallBack message) {
        TunnelVO vo = dbf.findByUuid(cmd.getPorductUuid(), TunnelVO.class);
        vo.setBandwidth(message.getBandwidth());
        dbf.updateAndRefresh(vo);
        //付款成功,记录生效订单
        saveResourceOrderEffective(cmd.getOrderUuid(), vo.getUuid(), vo.getClass().getSimpleName());

        //创建任务
        TaskResourceVO taskResourceVO = newTaskResourceVO(vo, TaskType.ModifyBandwidth);

        ModifyTunnelBandwidthMsg modifyTunnelBandwidthMsg = new ModifyTunnelBandwidthMsg();
        modifyTunnelBandwidthMsg.setTunnelUuid(vo.getUuid());
        modifyTunnelBandwidthMsg.setTaskUuid(taskResourceVO.getUuid());
        bus.makeLocalServiceId(modifyTunnelBandwidthMsg, TunnelConstant.SERVICE_ID);
        bus.send(modifyTunnelBandwidthMsg);
    }

    @Override
    public boolean start() {
        startCleanExpiredProduct();

        restf.registerSyncHttpCallHandler("callbackData", OrderCallbackCmd.class,
                cmd -> {
                    Message message = RESTApiDecoder.loads(cmd.getCallBackData());
                    if (message instanceof CreateInterfaceCallBack) {
                        logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                        if (!orderIsExist(cmd.getOrderUuid())) {
                            buyInterface(cmd);
                        }

                    } else if (message instanceof CreateTunnelCallBack) {
                        logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                        if (!orderIsExist(cmd.getOrderUuid())) {
                            buyTunnel(cmd, (CreateTunnelCallBack) message);
                        }

                    } else if (message instanceof DeleteTunnelCallBack) {
                        logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                        if (!orderIsExist(cmd.getOrderUuid())) {
                            unsubcribeTunnel(cmd, (DeleteTunnelCallBack) message);
                        }

                    } else if (message instanceof RenewAutoInterfaceCallBack) {
                        logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                        if (!orderIsExist(cmd.getOrderUuid())) {
                            renewOrSla(cmd);
                        }

                    } else if (message instanceof RenewInterfaceCallBack) {
                        logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                        if (!orderIsExist(cmd.getOrderUuid())) {
                            renewOrSla(cmd);
                        }

                    } else if (message instanceof RenewTunnelCallBack) {
                        logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                        if (!orderIsExist(cmd.getOrderUuid())) {
                            renewOrSla(cmd);
                        }

                    } else if (message instanceof SalTunnelCallBack) {
                        logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                        if (!orderIsExist(cmd.getOrderUuid())) {
                            renewOrSla(cmd);
                        }

                    } else if (message instanceof SlaInterfaceCallBack) {
                        logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                        if (!orderIsExist(cmd.getOrderUuid())) {
                            renewOrSla(cmd);
                        }

                    } else if (message instanceof UnsubcribeInterfaceCallBack) {
                        logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                        if (!orderIsExist(cmd.getOrderUuid())) {
                            unsubcribeInterface(cmd);
                        }

                    } else if (message instanceof UpdateTunnelBandwidthCallBack) {
                        logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                        if (!orderIsExist(cmd.getOrderUuid())) {
                            modifyBandwidth(cmd, (UpdateTunnelBandwidthCallBack) message);
                        }

                    } else {
                        logger.debug("未知回调！！！！！！！！");
                    }

                    return null;
                });
        return true;
    }

    private Future<Void> cleanExpiredProductThread = null;
    private int cleanExpiredProductInterval;
    private int expiredProductCloseTime;
    private int expiredProductDeleteTime;

    private void startCleanExpiredProduct() {
        cleanExpiredProductInterval = CoreGlobalProperty.CLEAN_EXPIRED_PRODUCT_INTERVAL;
        expiredProductCloseTime = CoreGlobalProperty.EXPIRED_PRODUCT_CLOSE_TIME;
        expiredProductDeleteTime = CoreGlobalProperty.EXPIRED_PRODUCT_DELETE_TIME;
        if (cleanExpiredProductThread != null) {
            cleanExpiredProductThread.cancel(true);
        }

        cleanExpiredProductThread = thdf.submitPeriodicTask(new CleanExpiredProductThread(), TimeUnit.SECONDS.toMillis(10));
        logger.debug(String
                .format("security group cleanExpiredProductThread starts[cleanExpiredProductInterval: %s day]", cleanExpiredProductInterval));
    }

    private class CleanExpiredProductThread implements PeriodicTask {

        @Override
        public TimeUnit getTimeUnit() {
            return TimeUnit.MILLISECONDS;
        }

        @Override
        public long getInterval() {
            return TimeUnit.DAYS.toMillis(cleanExpiredProductInterval);
        }

        @Override
        public String getName() {
            return "clean-expired-product-" + Platform.getManagementServerId();
        }

        private List<TunnelVO> getTunnels() {

            return Q.New(TunnelVO.class)
                    .lte(TunnelVO_.expireDate, dbf.getCurrentSqlTime())
                    .list();
        }

        private List<InterfaceVO> getInterfaces() {

            return Q.New(InterfaceVO.class)
                    .lte(InterfaceVO_.expireDate, dbf.getCurrentSqlTime())
                    .list();
        }

        private void deleteInterface(List<InterfaceVO> ifaces, Timestamp close, Timestamp delete) {

            for (InterfaceVO vo : ifaces) {
                if (vo.getExpireDate().before(delete)) {
                    if (!Q.New(TunnelSwitchPortVO.class).eq(TunnelSwitchPortVO_.interfaceUuid, vo.getUuid()).isExists())
                        dbf.remove(vo);
                }
                if (vo.getExpireDate().after(close) && vo.getState() == InterfaceState.Unpaid) {
                    dbf.remove(vo);
                }
            }
        }

        @Override
        public void run() {
            Timestamp closeTime = Timestamp.valueOf(LocalDateTime.now().plusDays(expiredProductCloseTime));
            Timestamp deleteTime = Timestamp.valueOf(LocalDateTime.now().plusDays(expiredProductDeleteTime));

            try {
                List<TunnelVO> tunnelVOs = getTunnels();
                logger.debug("delete expired tunnel.");
                if (tunnelVOs.isEmpty())
                    return;
                List<NeedReplyMessage> msgs = new ArrayList<>();
                for (TunnelVO vo : tunnelVOs) {
                    if (vo.getExpireDate().before(deleteTime)) {
                        vo.setAccountUuid(null);
                        dbf.updateAndRefresh(vo);
                        TaskResourceVO task = newTaskResourceVO(vo, TaskType.Delete);
                        DeleteTunnelMsg msg = new DeleteTunnelMsg();
                        msg.setTaskUuid(task.getUuid());
                        msg.setTunnelUuid(vo.getUuid());
                        bus.makeLocalServiceId(msg, TunnelConstant.SERVICE_ID);
                        msgs.add(msg);
                    }
                    if (vo.getExpireDate().before(closeTime)) {
                        if (vo.getState() == TunnelState.Unpaid) {
                            deleteTunnel(vo);
                        } else {
                            TaskResourceVO task = newTaskResourceVO(vo, TaskType.Delete);
                            DisabledTunnelMsg msg = new DisabledTunnelMsg();
                            msg.setTaskUuid(task.getUuid());
                            msg.setTunnelUuid(vo.getUuid());
                            bus.makeLocalServiceId(msg, TunnelConstant.SERVICE_ID);
                            msgs.add(msg);
                        }
                    }
                }
                if (msgs.isEmpty()) {
                    return;
                }
                bus.send(msgs);

                List<InterfaceVO> ifaces = getInterfaces();
                logger.debug("delete expired interface.");
                if (ifaces.isEmpty())
                    return;
                deleteInterface(ifaces, closeTime, deleteTime);
            } catch (Throwable t) {
                logger.warn("unhandled exception", t);
            }
        }

    }

    @Override
    public boolean stop() {
        return true;
    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(TunnelConstant.SERVICE_ID);
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if (msg instanceof APICreateInterfaceMsg) {
            validate((APICreateInterfaceMsg) msg);
        } else if (msg instanceof APICreateInterfaceManualMsg) {
            validate((APICreateInterfaceManualMsg) msg);
        } else if (msg instanceof APIUpdateInterfaceMsg) {
            validate((APIUpdateInterfaceMsg) msg);
        } else if (msg instanceof APIRenewInterfaceMsg) {
            validate((APIRenewInterfaceMsg) msg);
        } else if (msg instanceof APIRenewAutoInterfaceMsg) {
            validate((APIRenewAutoInterfaceMsg) msg);
        } else if (msg instanceof APISLAInterfaceMsg) {
            validate((APISLAInterfaceMsg) msg);
        } else if (msg instanceof APIDeleteInterfaceMsg) {
            validate((APIDeleteInterfaceMsg) msg);
        } else if (msg instanceof APICreateTunnelMsg) {
            validate((APICreateTunnelMsg) msg);
        } else if (msg instanceof APICreateTunnelManualMsg) {
            validate((APICreateTunnelManualMsg) msg);
        } else if (msg instanceof APIUpdateTunnelMsg) {
            validate((APIUpdateTunnelMsg) msg);
        } else if (msg instanceof APIUpdateTunnelBandwidthMsg) {
            validate((APIUpdateTunnelBandwidthMsg) msg);
        } else if (msg instanceof APIRenewTunnelMsg) {
            validate((APIRenewTunnelMsg) msg);
        } else if (msg instanceof APIRenewAutoTunnelMsg) {
            validate((APIRenewAutoTunnelMsg) msg);
        } else if (msg instanceof APISalTunnelMsg) {
            validate((APISalTunnelMsg) msg);
        } else if (msg instanceof APIDeleteTunnelMsg) {
            validate((APIDeleteTunnelMsg) msg);
        } else if (msg instanceof APIDeleteForciblyTunnelMsg) {
            validate((APIDeleteForciblyTunnelMsg) msg);
        } else if (msg instanceof APIUpdateTunnelStateMsg) {
            validate((APIUpdateTunnelStateMsg) msg);
        } else if (msg instanceof APICreateQinqMsg) {
            validate((APICreateQinqMsg) msg);
        } else if (msg instanceof APIDeleteQinqMsg) {
            validate((APIDeleteQinqMsg) msg);
        } else if (msg instanceof APIUpdateTunnelVlanMsg) {
            validate((APIUpdateTunnelVlanMsg) msg);
        } else if (msg instanceof APIUpdateForciblyTunnelVlanMsg) {
            validate((APIUpdateForciblyTunnelVlanMsg) msg);
        } else if (msg instanceof APIUpdateInterfacePortMsg) {
            validate((APIUpdateInterfacePortMsg) msg);
        }
        return msg;
    }

    private void validate(APIUpdateInterfacePortMsg msg) {

        InterfaceVO iface = Q.New(InterfaceVO.class).eq(InterfaceVO_.uuid, msg.getUuid()).find();
        if (iface.getExpireDate().before(Timestamp.valueOf(LocalDateTime.now())))
            throw new ApiMessageInterceptionException(
                    argerr("The Interface[uuid:%s] has expired！", msg.getUuid()));
        Q q = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.interfaceUuid, iface.getUuid());
        if (q.count() > 1)
            throw new ApiMessageInterceptionException(
                    argerr("The Interface[uuid:%s] has been used by two tunnel as least！", msg.getUuid()));
        SwitchPortVO switchPort = Q.New(SwitchPortVO.class).eq(SwitchPortVO_.uuid, iface.getSwitchPortUuid()).find();
        if (switchPort.getPortType().equals("SHARE"))
            throw new ApiMessageInterceptionException(
                    argerr("The type of Interface[uuid:%s] is %s, could not modify！", msg.getUuid(), switchPort.getPortType()));

        if (msg.getSwitchPortUuid().equals(iface.getSwitchPortUuid())
                && (msg.getSegments() == null || msg.getSegments().isEmpty())) {
            throw new ApiMessageInterceptionException(
                    argerr("The InnerVlans cannot be empty！"));
        }
    }

    private void validate(APICreateInterfaceMsg msg) {
        //判断同一个用户的接口名称是否已经存在
        Q q1 = Q.New(InterfaceVO.class)
                .eq(InterfaceVO_.name, msg.getName())
                .eq(InterfaceVO_.accountUuid, msg.getAccountUuid());
        if (q1.isExists()) {
            throw new ApiMessageInterceptionException(argerr("物理接口名称【%s】已经存在!", msg.getName()));
        }


        //判断账户金额是否充足
        APIGetProductPriceMsg priceMsg = new APIGetProductPriceMsg();
        priceMsg.setAccountUuid(msg.getAccountUuid());
        priceMsg.setProductChargeModel(msg.getProductChargeModel());
        priceMsg.setDuration(msg.getDuration());
        priceMsg.setUnits(getInterfacePriceUnit(msg.getPortOfferingUuid()));
        APIGetProductPriceReply reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(priceMsg);
        if (!reply.isPayable())
            throw new ApiMessageInterceptionException(
                    argerr("账户[uuid:%s]余额不足!", msg.getAccountUuid()));
    }

    private void validate(APICreateInterfaceManualMsg msg) {
        //判断同一个用户的接口名称是否已经存在
        Q q = Q.New(InterfaceVO.class)
                .eq(InterfaceVO_.name, msg.getName())
                .eq(InterfaceVO_.accountUuid, msg.getAccountUuid());

        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("物理接口名称【%s】已经存在!", msg.getName()));
        }

        //判断同一个用户在同一个连接点下是否已经购买共享端口
        if(msg.getPortOfferingUuid().equals("SHARE")){
            String sql = "select a from InterfaceVO a,SwitchPortVO b " +
                    "where a.switchPortUuid = b.uuid " +
                    "and a.accountUuid = :accountUuid " +
                    "and a.endpointUuid = :endpointUuid " +
                    "and b.portType = 'SHARE'";
            TypedQuery<InterfaceVO> itq = dbf.getEntityManager().createQuery(sql, InterfaceVO.class);
            itq.setParameter("accountUuid",msg.getAccountUuid());
            itq.setParameter("endpointUuid",msg.getEndpointUuid());
            if(!itq.getResultList().isEmpty()){
                throw new ApiMessageInterceptionException(argerr("一个用户在同一个连接点下只能购买一个共享口！ "));
            }
        }

        //判断账户金额是否充足
        APIGetProductPriceMsg priceMsg = new APIGetProductPriceMsg();
        priceMsg.setAccountUuid(msg.getAccountUuid());
        priceMsg.setProductChargeModel(msg.getProductChargeModel());
        priceMsg.setDuration(msg.getDuration());
        priceMsg.setUnits(getInterfacePriceUnit(msg.getPortOfferingUuid()));
        APIGetProductPriceReply reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(priceMsg);
        if (!reply.isPayable())
            throw new ApiMessageInterceptionException(
                    argerr("The Account[uuid:%s] has no money to pay.", msg.getAccountUuid()));

    }

    private void validate(APIUpdateInterfaceMsg msg) {

        InterfaceVO iface = Q.New(InterfaceVO.class).eq(InterfaceVO_.uuid, msg.getUuid()).find();
        if (iface.getExpireDate().before(Timestamp.valueOf(LocalDateTime.now())))
            throw new ApiMessageInterceptionException(
                    argerr("The Interface[uuid:%s] has expired！", msg.getUuid()));

        //判断同一个用户的网络名称是否已经存在
        if (!StringUtils.isEmpty(msg.getName()) && !msg.getName().equals(iface.getName())) {
            if (checkResourceName(InterfaceVO.class.getSimpleName(), msg.getName(), iface.getAccountUuid())) {
                throw new ApiMessageInterceptionException(argerr("物理接口名称【%s】已经存在!", msg.getName()));
            }
        }

    }

    private void validate(APISLAInterfaceMsg msg) {
        checkOrderNoPayForInterface(msg.getUuid());
    }

    private void validate(APIRenewInterfaceMsg msg) {
        checkOrderNoPayForInterface(msg.getUuid());
    }

    private void validate(APIRenewAutoInterfaceMsg msg) {
        checkOrderNoPayForInterface(msg.getUuid());
    }

    private void checkOrderNoPayForInterface(String productUuid) {
        String accountUuid = Q.New(InterfaceVO.class)
                .eq(InterfaceVO_.uuid, productUuid)
                .select(InterfaceVO_.accountUuid).findValue();
        checkOrderNoPay(accountUuid, productUuid);
    }

    private void validate(APIDeleteInterfaceMsg msg) {
        //判断云专线下是否有该物理接口
        Q q = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.interfaceUuid, msg.getUuid());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(
                    argerr("The interface[uuid:%s] is being used, cannot delete!", msg.getUuid()));
        }

        //判断该产品是否有未完成订单
        String accountUuid = Q.New(InterfaceVO.class)
                .eq(InterfaceVO_.uuid, msg.getUuid())
                .select(InterfaceVO_.accountUuid).findValue();
        checkOrderNoPay(accountUuid, msg.getUuid());
    }

    private void validate(APICreateTunnelMsg msg) {
        //判断同一个用户的名称是否已经存在
        SimpleQuery<TunnelVO> q = dbf.createQuery(TunnelVO.class);
        q.add(TunnelVO_.name, SimpleQuery.Op.EQ, msg.getName());
        q.add(TunnelVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("Tunnel's name %s is already exist ", msg.getName()));
        }
        //判断通道两端的连接点是否相同，不允许相同
        if (Objects.equals(msg.getEndpointAUuid(), msg.getEndpointZUuid())) {
            throw new ApiMessageInterceptionException(argerr("通道两端不允许在同一个连接点 "));
        }
        //如果跨国,验证互联连接点和内外联交换机配置
        if (msg.getInnerConnectedEndpointUuid() != null) {
            validateInnerConnectEndpoint(msg.getInnerConnectedEndpointUuid());
        }
        //判断账户金额是否充足
        EndpointVO evoA = dbf.findByUuid(msg.getEndpointAUuid(), EndpointVO.class);
        EndpointVO evoZ = dbf.findByUuid(msg.getEndpointZUuid(), EndpointVO.class);

        APIGetProductPriceMsg priceMsg = new APIGetProductPriceMsg();
        priceMsg.setAccountUuid(msg.getAccountUuid());
        priceMsg.setProductChargeModel(msg.getProductChargeModel());
        priceMsg.setDuration(msg.getDuration());
        priceMsg.setUnits(getTunnelPriceUnit(msg.getBandwidthOfferingUuid(), evoA.getNodeUuid(),
                evoZ.getNodeUuid(), msg.getInnerConnectedEndpointUuid()));
        APIGetProductPriceReply reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(priceMsg);
        if (!reply.isPayable())
            throw new ApiMessageInterceptionException(
                    argerr("The Account[uuid:%s] has no money to pay.", msg.getAccountUuid()));

    }

    private void validateInnerConnectEndpoint(String innerConnectedEndpointUuid) {
        TunnelStrategy ts = new TunnelStrategy();
        //通过互联连接点找到内联交换机和内联端口
        SwitchVO innerSwitch = Q.New(SwitchVO.class)
                .eq(SwitchVO_.endpointUuid, innerConnectedEndpointUuid)
                .eq(SwitchVO_.type, SwitchType.INNER)
                .find();
        if (innerSwitch == null) {
            throw new ApiMessageInterceptionException(argerr("该互联连接点下未添加内联逻辑交换机 "));
        }
        SwitchPortVO innerSwitchPort = Q.New(SwitchPortVO.class)
                .eq(SwitchPortVO_.switchUuid, innerSwitch.getUuid())
                .find();
        if (innerSwitchPort == null) {
            throw new ApiMessageInterceptionException(argerr("该内联逻辑交换机下未添加端口 "));
        }
        //通过互联连接点找到外联交换机和外联端口
        SwitchVO outerSwitch = Q.New(SwitchVO.class)
                .eq(SwitchVO_.endpointUuid, innerConnectedEndpointUuid)
                .eq(SwitchVO_.type, SwitchType.OUTER)
                .find();
        if (outerSwitch == null) {
            throw new ApiMessageInterceptionException(argerr("该互联连接点下未添加外联逻辑交换机 "));
        }
        SwitchPortVO outerSwitchPort = Q.New(SwitchPortVO.class)
                .eq(SwitchPortVO_.switchUuid, outerSwitch.getUuid())
                .find();
        if (outerSwitchPort == null) {
            throw new ApiMessageInterceptionException(argerr("该外联逻辑交换机下未添加端口 "));
        }
        //获取互联设备的VLAN
        Integer innerVlan = ts.getVlanBySwitch(innerSwitch.getUuid());
        if (innerVlan == 0) {
            throw new ApiMessageInterceptionException(argerr("该端口所属内联虚拟交换机下已无可使用的VLAN，请联系系统管理员 "));
        }
    }

    private void validate(APICreateTunnelManualMsg msg) {
        InterfaceVO interfaceVOA = dbf.findByUuid(msg.getInterfaceAUuid(), InterfaceVO.class);
        InterfaceVO interfaceVOZ = dbf.findByUuid(msg.getInterfaceZUuid(), InterfaceVO.class);
        EndpointVO evoA = dbf.findByUuid(interfaceVOA.getEndpointUuid(), EndpointVO.class);
        EndpointVO evoZ = dbf.findByUuid(interfaceVOZ.getEndpointUuid(), EndpointVO.class);

        //判断同一个用户的名称是否已经存在
        SimpleQuery<TunnelVO> q = dbf.createQuery(TunnelVO.class);
        q.add(TunnelVO_.name, SimpleQuery.Op.EQ, msg.getName());
        q.add(TunnelVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("Tunnel's name %s is already exist ", msg.getName()));
        }

        //判断通道两端的连接点是否相同，不允许相同
        if (Objects.equals(evoA.getUuid(), evoZ.getUuid())) {
            throw new ApiMessageInterceptionException(argerr("通道两端不允许在同一个连接点 "));
        }

        //如果跨国,验证互联连接点和内外联交换机配置
        if (msg.getInnerConnectedEndpointUuid() != null) {
            validateInnerConnectEndpoint(msg.getInnerConnectedEndpointUuid());
        }

        //判断外部VLAN是否可用
        validateVlan(msg.getInterfaceAUuid(), msg.getaVlan());
        validateVlan(msg.getInterfaceZUuid(), msg.getzVlan());

        //如果是ACCESS或是QINQ的物理接口，判断该物理接口是否已经开通通道
        if (interfaceVOA.getType() == NetworkType.ACCESS || interfaceVOA.getType() == NetworkType.QINQ) {
            boolean exists = Q.New(TunnelSwitchPortVO.class)
                    .eq(TunnelSwitchPortVO_.interfaceUuid, msg.getInterfaceAUuid())
                    .isExists();
            if (exists) {
                throw new ApiMessageInterceptionException(argerr("该物理接口不可复用"));
            }
        }
        if (interfaceVOZ.getType() == NetworkType.ACCESS || interfaceVOZ.getType() == NetworkType.QINQ) {
            boolean exists = Q.New(TunnelSwitchPortVO.class)
                    .eq(TunnelSwitchPortVO_.interfaceUuid, msg.getInterfaceZUuid())
                    .isExists();
            if (exists) {
                throw new ApiMessageInterceptionException(argerr("该物理接口不可复用"));
            }
        }

        //判断同一个switchPort下内部VLAN段是否有重叠
        String sql = "select count(a.uuid) from QinqVO a " +
                "where a.tunnelUuid in (select b.tunnelUuid from TunnelSwitchPortVO b where b.switchPortUuid = :switchPortUuid and b.type = 'QINQ') " +
                "and ((a.startVlan between :startVlan and :endVlan) " +
                "or (a.endVlan between :startVlan and :endVlan) " +
                "or (:startVlan between a.startVlan and a.endVlan) " +
                "or (:endVlan between a.startVlan and a.endVlan))";
        if (msg.getVlanSegment() != null) {
            if (interfaceVOA.getType() == NetworkType.QINQ) {
                List<InnerVlanSegment> vlanSegments = msg.getVlanSegment();
                for (InnerVlanSegment vlanSegment : vlanSegments) {
                    TypedQuery<Long> vq = dbf.getEntityManager().createQuery(sql, Long.class);
                    vq.setParameter("switchPortUuid", interfaceVOA.getSwitchPortUuid());
                    vq.setParameter("startVlan", vlanSegment.getStartVlan());
                    vq.setParameter("endVlan", vlanSegment.getEndVlan());
                    Long count = vq.getSingleResult();
                    if (count > 0) {
                        throw new ApiMessageInterceptionException(argerr("vlan has overlapping"));
                    }
                }
            }
            if (interfaceVOZ.getType() == NetworkType.QINQ) {
                List<InnerVlanSegment> vlanSegments = msg.getVlanSegment();
                for (InnerVlanSegment vlanSegment : vlanSegments) {
                    TypedQuery<Long> vq = dbf.getEntityManager().createQuery(sql, Long.class);
                    vq.setParameter("switchPortUuid", interfaceVOZ.getSwitchPortUuid());
                    vq.setParameter("startVlan", vlanSegment.getStartVlan());
                    vq.setParameter("endVlan", vlanSegment.getEndVlan());
                    Long count = vq.getSingleResult();
                    if (count > 0) {
                        throw new ApiMessageInterceptionException(argerr("vlan has overlapping"));
                    }
                }
            }
        }

        //判断账户金额是否充足
        APIGetProductPriceMsg priceMsg = new APIGetProductPriceMsg();
        priceMsg.setAccountUuid(msg.getAccountUuid());
        priceMsg.setProductChargeModel(msg.getProductChargeModel());
        priceMsg.setDuration(msg.getDuration());
        priceMsg.setUnits(getTunnelPriceUnit(msg.getBandwidthOfferingUuid(), evoA.getNodeUuid(),
                evoZ.getNodeUuid(), msg.getInnerConnectedEndpointUuid()));
        APIGetProductPriceReply reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(priceMsg);
        if (!reply.isPayable())
            throw new ApiMessageInterceptionException(
                    argerr("The Account[uuid:%s] has no money to pay.", msg.getAccountUuid()));
    }

    private void validate(APIUpdateTunnelMsg msg) {
        //判断同一个用户tunnel名称是否已经存在
        if (msg.getName() != null) {
            SimpleQuery<TunnelVO> q = dbf.createQuery(TunnelVO.class);
            q.add(TunnelVO_.name, SimpleQuery.Op.EQ, msg.getName());
            q.add(TunnelVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
            q.add(TunnelVO_.uuid, SimpleQuery.Op.NOT_EQ, msg.getUuid());
            if (q.isExists()) {
                throw new ApiMessageInterceptionException(argerr("Tunnel's name %s is already exist ", msg.getName()));
            }
        }

    }

    private void validate(APIUpdateTunnelBandwidthMsg msg) {
        //判断该产品是否有未完成订单
        checkOrderNoPay(msg.getAccountUuid(), msg.getUuid());

        //调整次数当月是否达到上限
        LocalDateTime dateTime =
                LocalDate.now().withDayOfMonth(LocalDate.MIN.getDayOfMonth()).atTime(LocalTime.MIN);
        Long times = Q.New(TunnelMotifyRecordVO.class).eq(TunnelMotifyRecordVO_.tunnelUuid, msg.getUuid())
                .gte(TunnelMotifyRecordVO_.createDate, Timestamp.valueOf(dateTime))
                .lt(TunnelMotifyRecordVO_.createDate, Timestamp.valueOf(dateTime.plusMonths(1))).count();
        Integer maxModifies =
                Q.New(TunnelVO.class).eq(TunnelVO_.uuid, msg.getUuid()).select(TunnelVO_.maxModifies)
                        .findValue();

        if (times >= maxModifies) {
            throw new ApiMessageInterceptionException(
                    argerr("The Tunnel[uuid:%s] has motified %s times.", msg.getUuid(), times));
        }
    }

    private void validate(APIRenewTunnelMsg msg) {
        String accountUuid = Q.New(TunnelVO.class)
                .eq(TunnelVO_.uuid, msg.getUuid())
                .select(TunnelVO_.accountUuid)
                .findValue();
        checkOrderNoPay(accountUuid, msg.getUuid());
    }

    private void validate(APIRenewAutoTunnelMsg msg) {
        String accountUuid = Q.New(TunnelVO.class)
                .eq(TunnelVO_.uuid, msg.getUuid())
                .select(TunnelVO_.accountUuid)
                .findValue();
        checkOrderNoPay(accountUuid, msg.getUuid());
    }

    private void validate(APISalTunnelMsg msg) {
        String accountUuid = Q.New(TunnelVO.class)
                .eq(TunnelVO_.uuid, msg.getUuid())
                .select(TunnelVO_.accountUuid)
                .findValue();
        checkOrderNoPay(accountUuid, msg.getUuid());
    }

    private void validate(APIDeleteTunnelMsg msg) {
        checkOrderNoPay(msg.getAccountUuid(), msg.getUuid());
    }

    private void validate(APIDeleteForciblyTunnelMsg msg) {
        checkOrderNoPay(msg.getAccountUuid(), msg.getUuid());
    }

    private void validate(APIUpdateTunnelStateMsg msg) {
        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);
        if (vo.getState() == msg.getState()) {
            throw new ApiMessageInterceptionException(argerr("该云专线[uuid:%s] 已是该状况，不可重复操作 ", msg.getUuid()));
        }
    }

    private void validate(APICreateQinqMsg msg) {

        //判断同一个switchPort下内部VLAN段是否有重叠
        String sql = "select count(a.uuid) from QinqVO a " +
                "where a.tunnelUuid in (select b.tunnelUuid from TunnelSwitchPortVO b where b.switchPortUuid = :switchPortUuid and b.type = 'QINQ') " +
                "and ((a.startVlan between :startVlan and :endVlan) " +
                "or (a.endVlan between :startVlan and :endVlan) " +
                "or (:startVlan between a.startVlan and a.endVlan) " +
                "or (:endVlan between a.startVlan and a.endVlan))";
        TunnelSwitchPortVO tunnelSwitchPortVOA = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "A")
                .find();
        TunnelSwitchPortVO tunnelSwitchPortVOZ = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "Z")
                .find();
        if (tunnelSwitchPortVOA.getType() == NetworkType.QINQ) {
            TypedQuery<Long> vq = dbf.getEntityManager().createQuery(sql, Long.class);
            vq.setParameter("switchPortUuid", tunnelSwitchPortVOA.getSwitchPortUuid());
            vq.setParameter("startVlan", msg.getStartVlan());
            vq.setParameter("endVlan", msg.getEndVlan());
            Long count = vq.getSingleResult();
            if (count > 0) {
                throw new ApiMessageInterceptionException(argerr("vlan has overlapping"));
            }
        }
        if (tunnelSwitchPortVOZ.getType() == NetworkType.QINQ) {
            TypedQuery<Long> vq = dbf.getEntityManager().createQuery(sql, Long.class);
            vq.setParameter("switchPortUuid", tunnelSwitchPortVOZ.getSwitchPortUuid());
            vq.setParameter("startVlan", msg.getStartVlan());
            vq.setParameter("endVlan", msg.getEndVlan());
            Long count = vq.getSingleResult();
            if (count > 0) {
                throw new ApiMessageInterceptionException(argerr("vlan has overlapping"));
            }
        }

    }

    private void validate(APIDeleteQinqMsg msg) {
        QinqVO qinqVO = dbf.findByUuid(msg.getUuid(), QinqVO.class);
        Long count = Q.New(QinqVO.class)
                .eq(QinqVO_.tunnelUuid, qinqVO.getTunnelUuid())
                .count();
        if (count == 1) {
            throw new ApiMessageInterceptionException(argerr("该云专线[uuid:%s] 至少要有一个内部VLAN段，不能删！ ", qinqVO.getTunnelUuid()));
        }
    }

    private void validate(APIUpdateTunnelVlanMsg msg) {


        if (!msg.getInterfaceAUuid().equals(msg.getOldInterfaceAUuid()) || !Objects.equals(msg.getaVlan(), msg.getOldAVlan())) {
            if(isCross(msg.getUuid(),msg.getOldInterfaceAUuid())){
                throw new ApiMessageInterceptionException(argerr("该接口A为共点，不能修改配置！！"));
            }
            validateVlan(msg.getInterfaceAUuid(), msg.getaVlan());
        }

        if (!msg.getInterfaceZUuid().equals(msg.getOldInterfaceZUuid()) || !Objects.equals(msg.getzVlan(), msg.getOldZVlan())) {
            if(isCross(msg.getUuid(),msg.getOldInterfaceZUuid())){
                throw new ApiMessageInterceptionException(argerr("该接口Z为共点，不能修改配置！！"));
            }
            validateVlan(msg.getInterfaceZUuid(), msg.getzVlan());
        }

    }

    private void validate(APIUpdateForciblyTunnelVlanMsg msg) {

        if (!msg.getInterfaceAUuid().equals(msg.getOldInterfaceAUuid()) || !Objects.equals(msg.getaVlan(), msg.getOldAVlan())) {
            if(isCross(msg.getUuid(),msg.getOldInterfaceAUuid())){
                throw new ApiMessageInterceptionException(argerr("该接口A为共点，不能修改配置！！"));
            }
            validateVlan(msg.getInterfaceAUuid(), msg.getaVlan());
        }

        if (!msg.getInterfaceZUuid().equals(msg.getOldInterfaceZUuid()) || !Objects.equals(msg.getzVlan(), msg.getOldZVlan())) {
            if(isCross(msg.getUuid(),msg.getOldInterfaceZUuid())){
                throw new ApiMessageInterceptionException(argerr("该接口Z为共点，不能修改配置！！"));
            }
            validateVlan(msg.getInterfaceZUuid(), msg.getzVlan());
        }
    }

    /**
     * 判断物理接口是否是共点
     */

    private boolean isCross(String tunnelUuid,String interfaceUuid){
        TunnelVO vo = dbf.findByUuid(tunnelUuid,TunnelVO.class);
        Integer vsi = vo.getVsi();

        String sql = "select b from TunnelVO a, TunnelSwitchPortVO b " +
                "where a.uuid = b.tunnelUuid " +
                "and a.vsi = :vsi " +
                "and b.interfaceUuid = :interfaceUuid";
        TypedQuery<TunnelSwitchPortVO> vq = dbf.getEntityManager().createQuery(sql, TunnelSwitchPortVO.class);
        vq.setParameter("vsi", vsi);
        vq.setParameter("interfaceUuid", interfaceUuid);
        if(vq.getResultList().size()==1){
            return false;
        }else{
            return true;
        }

    }

    /**
     * 判断外部VLAN是否可用
     */
    private void validateVlan(String interfaceUuid, Integer vlan) {
        TunnelStrategy ts = new TunnelStrategy();
        //查询该TUNNEL的物理接口所属的虚拟交换机
        String switchUuid = ts.findSwitchByInterface(interfaceUuid);

        //查询该虚拟交换机下所有的Vlan段
        List<SwitchVlanVO> vlanList = ts.findSwitchVlanBySwitch(switchUuid);

        if (vlanList.isEmpty()) {
            throw new ApiMessageInterceptionException(argerr("该端口所属虚拟交换机下未配置VLAN，请联系系统管理员 "));
        }

        //查询该虚拟交换机下已经分配的Vlan
        List<Integer> allocatedVlans = ts.fingAllocateVlanBySwitch(switchUuid);

        //判断外部VLAN是否在该虚拟交换机的VLAN段中
        Boolean inner = false;
        for (SwitchVlanVO switchVlanVO : vlanList) {
            if (vlan >= switchVlanVO.getStartVlan() && vlan <= switchVlanVO.getEndVlan()) {
                inner = true;
                break;
            }
        }
        if (!inner) {
            throw new ApiMessageInterceptionException(argerr("avlan not in switchVlan"));
        }
        //判断外部vlan是否可用
        if (!allocatedVlans.isEmpty() && allocatedVlans.contains(vlan)) {
            throw new ApiMessageInterceptionException(argerr("该vlan %s 已经被占用", vlan));
        }
    }

    /**
     * 判断同一用户下产品名称是否重复
     */
    private boolean checkResourceName(String resourceType, String name, String accountUuid) {
        String uuid = SQL.New(String.format("SELECT r.uuid FROM %s r WHERE r.name = :name AND r.accountUuid = :accountUuid", resourceType), String.class)
                .param("accountUuid", accountUuid)
                .param("name", name).find();
        return uuid != null;
    }

    /**
     * 判断该产品是否有未完成订单
     */
    private void checkOrderNoPay(String accountUuid, String productUuid) {
        //判断该产品是否有未完成订单
        APIGetHasNotifyMsg apiGetHasNotifyMsg = new APIGetHasNotifyMsg();
        apiGetHasNotifyMsg.setAccountUuid(accountUuid);
        apiGetHasNotifyMsg.setProductUuid(productUuid);

        APIGetHasNotifyReply reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(apiGetHasNotifyMsg);
        if (reply.isInventory())
            throw new ApiMessageInterceptionException(
                    argerr("该订单[uuid:%s] 有未完成操作，请稍等！", productUuid));
    }

    /**
     * 自动获取 VSI
     */
    private Integer getVsiAuto() {

        GLock glock = new GLock("maxvsi", 120);
        glock.lock();

        Integer vsi;
        String sql = "select max(vo.vsi) from TunnelVO vo";
        try {
            TypedQuery<Integer> vq = dbf.getEntityManager().createQuery(sql, Integer.class);
            vsi = vq.getSingleResult();
            if (vsi == null) {
                vsi = CoreGlobalProperty.START_VSI;
            } else {
                vsi = vsi + 1;
            }

        } finally {
            glock.unlock();
        }
        return vsi;
    }

    /**
     * 调用支付
     */
    private OrderInventory createOrder(APICreateOrderMsg orderMsg) {
        //orderMsg.setNotifyUrl(TunnelConstant.NOTIFYURL);
        try {
            APICreateOrderReply reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(orderMsg);

            if (reply.isOrderSuccess()) {
                return reply.getInventory();
            }
        } catch (Exception e) {
            logger.error(String.format("无法创建订单, %s", e.getMessage()), e);
        }
        return null;
    }

    private List<OrderInventory> createBuyOrder(APICreateBuyOrderMsg orderMsg) {
        try {
            APICreateBuyOrderReply reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(orderMsg);

            if (reply.isSuccess()) {
                return reply.getInventories();
            }
        } catch (Exception e) {
            logger.error(String.format("无法创建订单, %s", e.getMessage()), e);
        }
        return Collections.emptyList();
    }

    /**
     * 获取到期时间
     */
    private Timestamp getExpireDate(Timestamp oldTime, ProductChargeModel chargeModel, int duration) {
        Timestamp newTime = oldTime;
        if (chargeModel == ProductChargeModel.BY_MONTH) {
            newTime = Timestamp.valueOf(oldTime.toLocalDateTime().plusMonths(duration));
        } else if (chargeModel == ProductChargeModel.BY_YEAR) {
            newTime = Timestamp.valueOf(oldTime.toLocalDateTime().plusYears(duration));
        } else if (chargeModel == ProductChargeModel.BY_DAY) {
            newTime = Timestamp.valueOf(oldTime.toLocalDateTime().plusDays(duration));
        }
        return newTime;
    }

    /**
     * 获取物理接口订单信息
     */
    private APICreateOrderMsg getOrderMsgForInterface(InterfaceVO vo, NotifyCallBackData callBack) {
        APICreateOrderMsg orderMsg = new APICreateOrderMsg();
        orderMsg.setProductName(vo.getName());
        orderMsg.setProductUuid(vo.getUuid());
        orderMsg.setProductType(ProductType.PORT);
        orderMsg.setDescriptionData(getDescriptionForInterface(vo));
        orderMsg.setAccountUuid(vo.getOwnerAccountUuid());
        orderMsg.setCallBackData(RESTApiDecoder.dump(callBack));
        orderMsg.setNotifyUrl(restf.getSendCommandUrl());
        return orderMsg;
    }

    /**
     * 获取物理接口单价
     */
    private List<ProductPriceUnit> getInterfacePriceUnit(String portOfferingUuid) {
        List<ProductPriceUnit> units = new ArrayList<>();
        ProductPriceUnit unit = new ProductPriceUnit();
        unit.setProductTypeCode(ProductType.PORT);
        unit.setCategoryCode(Category.PORT);
        unit.setAreaCode("DEFAULT");
        unit.setLineCode("DEFAULT");
        unit.setConfigCode(portOfferingUuid);
        units.add(unit);
        return units;
    }

    /**
     * 获取云专线单价
     */
    private List<ProductPriceUnit> getTunnelPriceUnit(String bandwidthOfferingUuid, String nodeAUuid, String nodeZUuid, String innerEndpointUuid) {
        List<ProductPriceUnit> units = new ArrayList<>();
        NodeVO nodeA = dbf.findByUuid(nodeAUuid, NodeVO.class);
        NodeVO nodeZ = dbf.findByUuid(nodeZUuid, NodeVO.class);
        String zoneUuidA = getZoneUuid(nodeA.getUuid());
        String zoneUuidZ = getZoneUuid(nodeZ.getUuid());
        if (innerEndpointUuid == null) {  //国内互传  或者 国外到国外
            if (nodeA.getCountry().equals("CHINA") && nodeZ.getCountry().equals("CHINA")) {  //国内互传
                ProductPriceUnit unit = getTunnelPriceUnitCN(bandwidthOfferingUuid, nodeA, nodeZ, zoneUuidA, zoneUuidZ);
                units.add(unit);
            } else {                          //国外到国外
                ProductPriceUnit unit = getTunnelPriceUnitAb(bandwidthOfferingUuid, nodeA, nodeZ);
                units.add(unit);
            }
        } else {                          //跨国
            EndpointVO endpointVO = dbf.findByUuid(innerEndpointUuid, EndpointVO.class);
            NodeVO nodeB = dbf.findByUuid(endpointVO.getNodeUuid(), NodeVO.class);
            String zoneUuidB = getZoneUuid(nodeB.getUuid());

            if(nodeA.getCountry().equals("CHINA")){
                ProductPriceUnit unitInner = getTunnelPriceUnitCN(bandwidthOfferingUuid, nodeA, nodeB, zoneUuidA, zoneUuidB);
                ProductPriceUnit unitOuter = getTunnelPriceUnitCNToAb(bandwidthOfferingUuid, nodeB, nodeZ);

                units.add(unitInner);
                units.add(unitOuter);
            }else{
                ProductPriceUnit unitInner = getTunnelPriceUnitCN(bandwidthOfferingUuid, nodeZ, nodeB, zoneUuidZ, zoneUuidB);
                ProductPriceUnit unitOuter = getTunnelPriceUnitCNToAb(bandwidthOfferingUuid, nodeB, nodeA);

                units.add(unitInner);
                units.add(unitOuter);
            }

        }
        return units;
    }

    /**
     * 获取云专线单价--国内互传单价
     */
    private ProductPriceUnit getTunnelPriceUnitCN(String bandwidthOfferingUuid, NodeVO nodeA, NodeVO nodeZ, String zoneUuidA, String zoneUuidZ) {
        ProductPriceUnit unit = new ProductPriceUnit();

        Category category;
        String areaCode;
        String lineCode;

        if (nodeA.getCity().equals(nodeZ.getCity())) {  //同城
            category = Category.CITY;
            areaCode = "DEFAULT";
            lineCode = "DEFAULT";
        } else if (zoneUuidA != null && zoneUuidZ != null && zoneUuidA.equals(zoneUuidZ)) { //同区域
            category = Category.REGION;
            areaCode = zoneUuidA;
            lineCode = "DEFAULT";
        } else {                      //长传
            category = Category.LONG;
            areaCode = "DEFAULT";
            lineCode = "DEFAULT";
        }
        unit.setProductTypeCode(ProductType.TUNNEL);
        unit.setCategoryCode(category);
        unit.setAreaCode(areaCode);
        unit.setLineCode(lineCode);
        unit.setConfigCode(bandwidthOfferingUuid);

        return unit;
    }

    /**
     * 获取云专线单价--国内到国外单价
     */
    private ProductPriceUnit getTunnelPriceUnitCNToAb(String bandwidthOfferingUuid, NodeVO nodeB, NodeVO nodeZ) {
        ProductPriceUnit unit = new ProductPriceUnit();

        unit.setProductTypeCode(ProductType.TUNNEL);
        unit.setCategoryCode(Category.ABROAD);
        unit.setAreaCode("CHINA2ABROAD");
        unit.setLineCode(nodeB.getCity() + "/" + nodeZ.getCountry());
        unit.setConfigCode(bandwidthOfferingUuid);

        return unit;
    }

    /**
     * 获取云专线单价--国外到国外单价
     */
    private ProductPriceUnit getTunnelPriceUnitAb(String bandwidthOfferingUuid, NodeVO nodeA, NodeVO nodeZ) {
        ProductPriceUnit unit = new ProductPriceUnit();

        unit.setProductTypeCode(ProductType.TUNNEL);
        unit.setCategoryCode(Category.ABROAD);
        unit.setAreaCode("ABROAD");
        unit.setLineCode(nodeA.getCountry() + "/" + nodeZ.getCountry());
        unit.setConfigCode(bandwidthOfferingUuid);

        return unit;
    }

    /**
     * 根据节点找到所属区域
     */
    private String getZoneUuid(String nodeUuid) {
        String zoneUuid = null;
        ZoneNodeRefVO zoneNodeRefVO = Q.New(ZoneNodeRefVO.class)
                .eq(ZoneNodeRefVO_.nodeUuid, nodeUuid)
                .find();
        if (zoneNodeRefVO != null) {
            zoneUuid = zoneNodeRefVO.getZoneUuid();
        }
        return zoneUuid;
    }

    /**
     * 付款成功,记录生效订单
     */
    private void saveResourceOrderEffective(String orderUuid, String resourceUuid, String resourceType) {
        ResourceOrderEffectiveVO resourceOrderEffectiveVO = new ResourceOrderEffectiveVO();
        resourceOrderEffectiveVO.setUuid(Platform.getUuid());
        resourceOrderEffectiveVO.setResourceUuid(resourceUuid);
        resourceOrderEffectiveVO.setResourceType(resourceType);
        resourceOrderEffectiveVO.setOrderUuid(orderUuid);
        dbf.persistAndRefresh(resourceOrderEffectiveVO);
    }

    /**
     * 创建云专线 支付成功创建下发任务
     */
    private TaskResourceVO newTaskResourceVO(TunnelVO vo, TaskType taskType) {
        TaskResourceVO taskResourceVO = new TaskResourceVO();
        taskResourceVO.setUuid(Platform.getUuid());
        taskResourceVO.setAccountUuid(vo.getAccountUuid());
        taskResourceVO.setResourceUuid(vo.getUuid());
        taskResourceVO.setResourceType(vo.getClass().getSimpleName());
        taskResourceVO.setTaskType(taskType);
        taskResourceVO.setBody(null);
        taskResourceVO.setResult(null);
        taskResourceVO.setStatus(TaskStatus.Preexecute);
        taskResourceVO = dbf.persistAndRefresh(taskResourceVO);
        return taskResourceVO;
    }

    /**
     * 创建云专线 如果跨国,将出海口设备添加至TunnelSwitchPort
     */
    private void createTunnelSwitchPortForAbroad(String innerConnectedEndpointUuid, TunnelVO vo, boolean isBInner) {
        TunnelStrategy ts = new TunnelStrategy();

        //通过互联连接点找到内联交换机和内联端口
        SwitchVO innerSwitch = Q.New(SwitchVO.class)
                .eq(SwitchVO_.endpointUuid, innerConnectedEndpointUuid)
                .eq(SwitchVO_.type, SwitchType.INNER)
                .find();
        SwitchPortVO innerSwitchPort = Q.New(SwitchPortVO.class)
                .eq(SwitchPortVO_.switchUuid, innerSwitch.getUuid())
                .find();
        //通过互联连接点找到外联交换机和外联端口
        SwitchVO outerSwitch = Q.New(SwitchVO.class)
                .eq(SwitchVO_.endpointUuid, innerConnectedEndpointUuid)
                .eq(SwitchVO_.type, SwitchType.OUTER)
                .find();
        SwitchPortVO outerSwitchPort = Q.New(SwitchPortVO.class)
                .eq(SwitchPortVO_.switchUuid, outerSwitch.getUuid())
                .find();
        //获取互联设备的VLAN
        Integer innerVlan = ts.getVlanBySwitch(innerSwitch.getUuid());

        TunnelSwitchPortVO tsvoB = new TunnelSwitchPortVO();
        TunnelSwitchPortVO tsvoC = new TunnelSwitchPortVO();

        if(isBInner){
            tsvoB.setUuid(Platform.getUuid());
            tsvoB.setTunnelUuid(vo.getUuid());
            tsvoB.setEndpointUuid(innerConnectedEndpointUuid);
            tsvoB.setInterfaceUuid(null);
            tsvoB.setSwitchPortUuid(innerSwitchPort.getUuid());
            tsvoB.setType(NetworkType.TRUNK);
            tsvoB.setVlan(innerVlan);
            tsvoB.setSortTag("B");


            tsvoC.setUuid(Platform.getUuid());
            tsvoC.setTunnelUuid(vo.getUuid());
            tsvoC.setInterfaceUuid(null);
            tsvoC.setEndpointUuid(innerConnectedEndpointUuid);
            tsvoC.setSwitchPortUuid(outerSwitchPort.getUuid());
            tsvoC.setType(NetworkType.TRUNK);
            tsvoC.setVlan(innerVlan);
            tsvoC.setSortTag("C");

        }else{
            tsvoB.setUuid(Platform.getUuid());
            tsvoB.setTunnelUuid(vo.getUuid());
            tsvoB.setInterfaceUuid(null);
            tsvoB.setEndpointUuid(innerConnectedEndpointUuid);
            tsvoB.setSwitchPortUuid(outerSwitchPort.getUuid());
            tsvoB.setType(NetworkType.TRUNK);
            tsvoB.setVlan(innerVlan);
            tsvoB.setSortTag("B");

            tsvoC.setUuid(Platform.getUuid());
            tsvoC.setTunnelUuid(vo.getUuid());
            tsvoC.setInterfaceUuid(null);
            tsvoC.setEndpointUuid(innerConnectedEndpointUuid);
            tsvoC.setSwitchPortUuid(innerSwitchPort.getUuid());
            tsvoC.setType(NetworkType.TRUNK);
            tsvoC.setVlan(innerVlan);
            tsvoC.setSortTag("C");

        }
        dbf.persistAndRefresh(tsvoB);
        dbf.persistAndRefresh(tsvoC);

    }

    /**
     * 根据TunnelSwicth获取两端节点
     */
    private String getNodeUuid(TunnelVO vo, String sortTag) {
        TunnelSwitchPortVO tunnelSwitch = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, vo.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, sortTag)
                .find();
        return dbf.findByUuid(tunnelSwitch.getEndpointUuid(), EndpointVO.class).getNodeUuid();
    }

    /**
     * 修改interface 的 switchPort
     */
    private void updateInterfacePort(APIUpdateInterfacePortMsg msg) {

        UpdateQuery.New(InterfaceVO.class)
                .set(InterfaceVO_.switchPortUuid, msg.getSwitchPortUuid())
                .set(InterfaceVO_.type, msg.getNetworkType())
                .eq(InterfaceVO_.uuid, msg.getUuid())
                .update();

        UpdateQuery.New(TunnelSwitchPortVO.class)
                .set(TunnelSwitchPortVO_.switchPortUuid, msg.getSwitchPortUuid())
                .set(TunnelSwitchPortVO_.type, msg.getNetworkType())
                .eq(TunnelSwitchPortVO_.interfaceUuid, msg.getUuid())
                .update();
    }

    /**
     * 通过连接点获取可用的端口规格
     */
    private List<PortOfferingVO> getPortTypeByEndpoint(String endpointUuid) {
        List<String> switchs = CollectionUtils.transformToList(getSwitchByEndpoint(endpointUuid), SwitchAO::getUuid);
        if (switchs.isEmpty())
            return Collections.emptyList();
        List<String> switchPorts = Q.New(SwitchPortVO.class)
                .in(SwitchPortVO_.switchUuid, switchs)
                .eq(SwitchPortVO_.state, SwitchPortState.Enabled)
                .select(SwitchPortVO_.portType)
                .groupBy(SwitchPortVO_.portType)
                .listValues();
        if (switchPorts.isEmpty())
            return Collections.emptyList();
        return Q.New(PortOfferingVO.class)
                .in(PortOfferingVO_.uuid, switchPorts)
                .list();
    }

    /**
     * 通过连接点获取可用的逻辑交换机
     */
    private List<SwitchVO> getSwitchByEndpoint(String endpointUuid) {
        return Q.New(SwitchVO.class)
                .eq(SwitchVO_.state, SwitchState.Enabled)
                .eq(SwitchVO_.status, SwitchStatus.Connected)
                .eq(SwitchVO_.endpointUuid, endpointUuid)
                .list();
    }

    /**
     * 通过连接点和端口规格获取可用的端口
     */
    private List<SwitchPortVO> getSwitchPortByType(String endpointUuid, String type) {
        List<String> switchs = CollectionUtils.transformToList(getSwitchByEndpoint(endpointUuid), SwitchAO::getUuid);
        if (switchs.isEmpty())
            return Collections.emptyList();
        return Q.New(SwitchPortVO.class)
                .in(SwitchPortVO_.switchUuid, switchs)
                .eq(SwitchPortVO_.state, SwitchPortState.Enabled)
                .eq(SwitchPortVO_.portType, type)
                .list();
    }

    private String getDescriptionForInterface(InterfaceVO vo) {
        DescriptionData data = new DescriptionData();
        data.add(new DescriptionItem("name", vo.getName()));
        data.add(new DescriptionItem("NetworkType", vo.getType().toString()));
        String portType = Q.New(SwitchPortVO.class)
                .eq(SwitchPortVO_.uuid, vo.getSwitchPortUuid())
                .select(SwitchPortVO_.portType).findValue();
        data.add(new DescriptionItem("PortType", portType));

        return JSONObjectUtil.toJsonString(data);
    }

    private String getDescriptionForTunnel(TunnelVO vo) {
        DescriptionData data = new DescriptionData();

        TunnelSwitchPortVO tunnelSwitchPortVOA = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, vo.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "A")
                .find();
        TunnelSwitchPortVO tunnelSwitchPortVOZ = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, vo.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "Z")
                .find();
        String endpointNameA = dbf.findByUuid(tunnelSwitchPortVOA.getEndpointUuid(), EndpointVO.class).getName();
        String endpointNameZ = dbf.findByUuid(tunnelSwitchPortVOZ.getEndpointUuid(), EndpointVO.class).getName();

        data.add(new DescriptionItem("endpointNameA", endpointNameA));
        data.add(new DescriptionItem("endpointNameZ", endpointNameZ));
        data.add(new DescriptionItem("bandwidth", vo.getBandwidth().toString()));

        return JSONObjectUtil.toJsonString(data);
    }

    /**
     * 通过端口获取物理交换机的管理IP
     */
    private String getPhysicalSwitch(String switchPortUuid) {
        String switcUuid = Q.New(SwitchPortVO.class).
                eq(SwitchPortVO_.uuid, switchPortUuid)
                .select(SwitchPortVO_.switchUuid)
                .findValue();

        String physicalSwitchUuid = Q.New(SwitchVO.class).
                eq(SwitchVO_.uuid, switcUuid)
                .select(SwitchVO_.physicalSwitchUuid).findValue();

        String switchIp = Q.New(PhysicalSwitchVO.class).
                eq(PhysicalSwitchVO_.uuid, physicalSwitchUuid).
                select(PhysicalSwitchVO_.mIP).findValue();

        if (switchIp != null)
            throw new IllegalArgumentException("获取物理交换机IP失败");

        return switchIp;
    }

    /**
     * 删除TUNNEL及其关联表
     */
    private void deleteTunnel(TunnelVO vo) {
        dbf.remove(vo);
        //删除对应的  TunnelSwitchPortVO 和 QingqVO
        SimpleQuery<TunnelSwitchPortVO> q = dbf.createQuery(TunnelSwitchPortVO.class);
        q.add(TunnelSwitchPortVO_.tunnelUuid, SimpleQuery.Op.EQ, vo.getUuid());
        List<TunnelSwitchPortVO> tivList = q.list();
        if (tivList.size() > 0) {
            for (TunnelSwitchPortVO tiv : tivList) {
                dbf.remove(tiv);
            }
        }

        SimpleQuery<QinqVO> q2 = dbf.createQuery(QinqVO.class);
        q2.add(QinqVO_.tunnelUuid, SimpleQuery.Op.EQ, vo.getUuid());
        List<QinqVO> qinqList = q2.list();
        if (qinqList.size() > 0) {
            for (QinqVO qv : qinqList) {
                dbf.remove(qv);
            }
        }
    }

    @Override
    public List<Quota> reportQuota() {

        InterfaceQuotaOperator interfaceQuotaOperator = new InterfaceQuotaOperator();
        // interface quota
        Quota iQuota = new Quota();
        iQuota.setOperator(interfaceQuotaOperator);
        iQuota.addMessageNeedValidation(APICreateInterfaceMsg.class);
        iQuota.addMessageNeedValidation(APICreateInterfaceManualMsg.class);

        Quota.QuotaPair p = new Quota.QuotaPair();
        p.setName(TunnelConstant.QUOTA_INTERFACE_NUM);
        p.setValue(QuotaConstant.QUOTA_INTERFACE_NUM);
        iQuota.addPair(p);

        // tunnel quota
        TunnelQuotaOperator tunnelQuotaOperator = new TunnelQuotaOperator();
        Quota tQuota = new Quota();
        tQuota.setOperator(tunnelQuotaOperator);
        tQuota.addMessageNeedValidation(APICreateTunnelMsg.class);
        tQuota.addMessageNeedValidation(APICreateTunnelManualMsg.class);

        p = new Quota.QuotaPair();
        p.setName(TunnelConstant.QUOTA_TUNNEL_NUM);
        p.setValue(QuotaConstant.QUOTA_TUNNEL_NUM);
        tQuota.addPair(p);

        return list(iQuota, tQuota);
    }
}
