package com.syscxp.tunnel.tunnel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.CloudBusCallBack;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.componentloader.PluginRegistry;
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
import com.syscxp.header.configuration.BandwidthOfferingVO;
import com.syscxp.header.core.ReturnValueCompletion;
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
import com.syscxp.header.tunnel.switchs.*;
import com.syscxp.header.tunnel.tunnel.*;
import com.syscxp.tunnel.quota.InterfaceQuotaOperator;
import com.syscxp.tunnel.quota.TunnelQuotaOperator;
import com.syscxp.utils.CollectionDSL;
import com.syscxp.utils.CollectionUtils;
import com.syscxp.utils.Utils;
import com.syscxp.utils.function.ForEachFunction;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.syscxp.core.Platform.argerr;
import static com.syscxp.utils.CollectionDSL.list;

/**
 * Create by DCY on 2017/10/26
 */
public class TunnelManagerImpl extends AbstractService implements TunnelManager, ApiMessageInterceptor, ReportQuotaExtensionPoint, TunnelDeletionExtensionPoint {
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
    @Autowired
    private PluginRegistry pluginRgty;

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
        TunnelControllerBase base = new TunnelControllerBase();
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
        } else if (msg instanceof APISLATunnelMsg) {
            handle((APISLATunnelMsg) msg);
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
        } else if (msg instanceof APIListTraceRouteMsg) {
            handle((APIListTraceRouteMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }


    /*****************************  Interface The following processing   *************************************/

    /**
     * 自动创建物理接口
     *
     **/
    private void handle(APICreateInterfaceMsg msg) {
        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();
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
        ProductInfoForOrder productInfoForOrder = tunnelBillingBase.createBuyOrderForInterface(vo, msg.getPortOfferingUuid(), new CreateInterfaceCallBack());
        productInfoForOrder.setOpAccountUuid(msg.getSession().getAccountUuid());
        productInfoForOrder.setNotifyUrl(restf.getSendCommandUrl());
        orderMsg.setProducts(CollectionDSL.list(productInfoForOrder));

        List<OrderInventory> inventories = tunnelBillingBase.createBuyOrder(orderMsg);
        afterCreateInterface(inventories, vo, msg);
    }

    /**
     * 手动创建物理接口
     *
     **/
    private void handle(APICreateInterfaceManualMsg msg) {
        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();
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

        String portType = Q.New(SwitchPortVO.class)
                .eq(SwitchPortVO_.uuid, msg.getSwitchPortUuid())
                .select(SwitchPortVO_.portType).find();

        //调用支付
        APICreateBuyOrderMsg orderMsg = new APICreateBuyOrderMsg();
        ProductInfoForOrder productInfoForOrder = tunnelBillingBase.createBuyOrderForInterface(vo, portType, new CreateInterfaceCallBack());
        productInfoForOrder.setOpAccountUuid(msg.getAccountUuid());
        orderMsg.setProducts(CollectionDSL.list(productInfoForOrder));

        List<OrderInventory> inventories = tunnelBillingBase.createBuyOrder(orderMsg);
        afterCreateInterface(inventories, vo, msg);
    }

    private void afterCreateInterface(List<OrderInventory> inventories, InterfaceVO vo, APIMessage msg) {
        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();
        APICreateInterfaceEvent evt = new APICreateInterfaceEvent(msg.getId());

        if (!inventories.isEmpty()) {

            //付款成功,记录生效订单
            tunnelBillingBase.saveResourceOrderEffective(inventories.get(0).getUuid(), vo.getUuid(), vo.getClass().getSimpleName());
            //状态修改已支付，生成到期时间
            vo.setAccountUuid(vo.getOwnerAccountUuid());
            vo.setState(InterfaceState.Paid);
            vo.setExpireDate(tunnelBillingBase.getExpireDate(dbf.getCurrentSqlTime(), vo.getProductChargeModel(), vo.getDuration()));

            vo = dbf.updateAndRefresh(vo);

            evt.setInventory(InterfaceInventory.valueOf(vo));
        } else {
            //付款失败
            evt.setError(errf.stringToOperationError("付款失败"));
            evt.setInventory(InterfaceInventory.valueOf(vo));
        }
        bus.publish(evt);
    }

    /**
     * 修改物理接口名称和描述
     *
     **/
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

    /**
     * 物理接口续费
     *
     **/
    private void handle(APIRenewInterfaceMsg msg) {
        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();
        InterfaceVO vo = dbf.findByUuid(msg.getUuid(), InterfaceVO.class);

        APICreateRenewOrderMsg orderMsg = new APICreateRenewOrderMsg(tunnelBillingBase.getOrderMsgForInterface(vo, new RenewInterfaceCallBack()));
        orderMsg.setDuration(msg.getDuration());
        orderMsg.setProductChargeModel(msg.getProductChargeModel());
        orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
        orderMsg.setStartTime(dbf.getCurrentSqlTime());
        orderMsg.setExpiredTime(vo.getExpireDate());

        OrderInventory inventory = tunnelBillingBase.createOrder(orderMsg);

        afterRenewInterface(inventory, vo, msg);
    }

    /**
     * 物理接口自动续费
     *
     **/
    private void handle(APIRenewAutoInterfaceMsg msg) {
        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();
        InterfaceVO vo = dbf.findByUuid(msg.getUuid(), InterfaceVO.class);

        APICreateRenewOrderMsg orderMsg = new APICreateRenewOrderMsg(tunnelBillingBase.getOrderMsgForInterface(vo, new RenewAutoInterfaceCallBack()));
        orderMsg.setDuration(msg.getDuration());
        orderMsg.setProductChargeModel(msg.getProductChargeModel());
        orderMsg.setOpAccountUuid("system");
        orderMsg.setStartTime(dbf.getCurrentSqlTime());
        orderMsg.setExpiredTime(vo.getExpireDate());

        OrderInventory inventory = tunnelBillingBase.createOrder(orderMsg);

        afterRenewInterface(inventory, vo, msg);

    }

    private void afterRenewInterface(OrderInventory orderInventory, InterfaceVO vo, APIMessage msg) {
        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();
        APIRenewAutoInterfaceReply reply = new APIRenewAutoInterfaceReply();

        if (orderInventory != null) {
            //续费或者赔偿成功,记录生效订单
            tunnelBillingBase.saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());
            //更新到期时间
            vo.setDuration(orderInventory.getDuration());
            vo.setProductChargeModel(orderInventory.getProductChargeModel());
            vo.setExpireDate(tunnelBillingBase.getExpireDate(vo.getExpireDate(), orderInventory.getProductChargeModel(), orderInventory.getDuration()));

            vo = dbf.updateAndRefresh(vo);
            reply.setInventory(InterfaceInventory.valueOf(vo));
        } else {
            reply.setError(errf.stringToOperationError("订单操作失败"));
        }

        bus.reply(msg, reply);
    }

    /**
     * 物理接口赔偿
     *
     **/
    private void handle(APISLAInterfaceMsg msg) {
        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();
        APISLAInterfaceReply reply = new APISLAInterfaceReply();

        InterfaceVO vo = dbf.findByUuid(msg.getUuid(), InterfaceVO.class);
        Timestamp newTime = vo.getExpireDate();
        APICreateSLACompensationOrderMsg orderMsg = new APICreateSLACompensationOrderMsg(tunnelBillingBase.getOrderMsgForInterface(vo, new SlaInterfaceCallBack()));
        orderMsg.setDuration(msg.getDuration());
        orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
        orderMsg.setStartTime(dbf.getCurrentSqlTime());
        orderMsg.setExpiredTime(vo.getExpireDate());

        OrderInventory orderInventory = tunnelBillingBase.createOrder(orderMsg);

        if (orderInventory != null) {
            //续费或者赔偿成功,记录生效订单
            tunnelBillingBase.saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());
            //更新到期时间
            vo.setDuration(msg.getDuration());
            vo.setExpireDate(tunnelBillingBase.getExpireDate(newTime, ProductChargeModel.BY_DAY, msg.getDuration()));

            vo = dbf.updateAndRefresh(vo);
            reply.setInventory(InterfaceInventory.valueOf(vo));
        } else {
            reply.setError(errf.stringToOperationError("订单操作失败"));
        }

        bus.reply(msg, reply);
    }

    /**
     * 物理接口退订和删除
     *
     **/
    private void handle(APIDeleteInterfaceMsg msg) {
        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();
        APIDeleteInterfaceEvent evt = new APIDeleteInterfaceEvent(msg.getId());

        InterfaceVO vo = dbf.findByUuid(msg.getUuid(), InterfaceVO.class);

        if (vo.getExpireDate().before(Timestamp.valueOf(LocalDateTime.now()))) {
            dbf.remove(vo);
            bus.publish(evt);
            return;
        }

        //调用退订
        APICreateUnsubcribeOrderMsg orderMsg = new APICreateUnsubcribeOrderMsg(tunnelBillingBase.getOrderMsgForInterface(vo, new UnsubcribeInterfaceCallBack()));
        orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
        orderMsg.setStartTime(dbf.getCurrentSqlTime());
        orderMsg.setExpiredTime(vo.getExpireDate());

        OrderInventory orderInventory = tunnelBillingBase.createOrder(orderMsg);
        if (orderInventory != null) {
            //退订成功,记录生效订单
            tunnelBillingBase.saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());
            //删除产品
            dbf.remove(vo);
            evt.setInventory(InterfaceInventory.valueOf(vo));
        } else {
            //退订失败
            evt.setError(errf.stringToOperationError("退订失败"));
        }

        bus.publish(evt);
    }

    /**
     * 更改物理接口的端口
     *
     **/
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
                    new TunnelBase().updateNetworkType(iface, tunnelUuid, msg.getNetworkType(), msg.getSegments());
                }
                logger.info(String.format("after update InterfaceVO[uuid: %s]", iface.getUuid()));
                //throw new CloudRuntimeException("update interface port ...............");
                trigger.next();
            }

            @Override
            public void rollback(FlowRollback trigger, Map data) {
                logger.info(String.format("rollback to update InterfaceVO[uuid: %s]", iface.getUuid()));
                dbf.updateAndRefresh(iface);
                if (isUsed) {
                    List<QinqVO> qinqs = (List<QinqVO>) data.getOrDefault("qinqs", new ArrayList());
                    dbf.updateCollection(qinqs);
                }
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
                if (isUsed && msg.isIssue()) {
                    TunnelVO tunnel = Q.New(TunnelVO.class)
                            .eq(TunnelVO_.uuid, tsPort.getTunnelUuid())
                            .find();
                    TaskResourceVO taskResource = new TunnelBase().newTaskResourceVO(tunnel, TaskType.ModifyPorts);
                    ModifyTunnelPortsMsg modifyMsg = new ModifyTunnelPortsMsg();
                    modifyMsg.setTunnelUuid(tunnel.getUuid());
                    modifyMsg.setTaskUuid(taskResource.getUuid());
                    bus.makeLocalServiceId(modifyMsg, TunnelConstant.SERVICE_ID);
                    bus.send(modifyMsg, new CloudBusCallBack(null) {
                        @Override
                        public void run(MessageReply reply) {
                            if (reply.isSuccess()) {
                                logger.info(String.format("Successfully restart tunnel[uuid:%s].", tunnel.getUuid()));
                                trigger.next();
                            } else {
                                logger.info(String.format("Failed to restart tunnel[uuid:%s].", tunnel.getUuid()));
                                trigger.fail(reply.getError());
                            }
                        }
                    });
                } else {
                    trigger.next();
                }
            }

            @Override
            public void rollback(FlowRollback trigger, Map data) {
                trigger.rollback();
            }
        }).done(new FlowDoneHandler(null) {
            @Override
            public void handle(Map data) {
                evt.setInventory(InterfaceInventory.valueOf(dbf.reload(iface)));
                bus.publish(evt);
            }
        }).error(new FlowErrorHandler(null) {
            @Override
            public void handle(ErrorCode errCode, Map data) {
                evt.setError(errf.stringToOperationError("update interfacePort failed!"));
                bus.publish(evt);
            }
        }).start();
    }

    /*****************************  Tunnel The following processing   ****************************************/

    /**
     * 自动创建云专线
     *
     **/
    private void handle(APICreateTunnelMsg msg) {

        //创建Tunnel,Interface,TunnelSwitchPort
        String tunnelUuid = doCreateTunnelVO(msg);

        TunnelVO vo = dbf.findByUuid(tunnelUuid,TunnelVO.class);

        //创建数据后的支付和下发
        afterCreateTunnel(vo, msg, new ReturnValueCompletion<TunnelInventory>(msg) {
            APICreateTunnelEvent evt = new APICreateTunnelEvent(msg.getId());

            @Override
            public void success(TunnelInventory inv) {
                evt.setInventory(inv);
                bus.publish(evt);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                evt.setError(errorCode);
                bus.publish(evt);
            }
        });

    }

    @Transactional
    private String doCreateTunnelVO(APICreateTunnelMsg msg){
        TunnelBase tunnelBase = new TunnelBase();
        TunnelStrategy ts = new TunnelStrategy();

        TunnelVO vo = new TunnelVO();
        InterfaceVO interfaceVOA = new InterfaceVO();
        InterfaceVO interfaceVOZ = new InterfaceVO();

        BandwidthOfferingVO bandwidthOfferingVO = dbf.findByUuid(msg.getBandwidthOfferingUuid(), BandwidthOfferingVO.class);

        EndpointVO evoA = dbf.findByUuid(msg.getEndpointAUuid(), EndpointVO.class);
        EndpointVO evoZ = dbf.findByUuid(msg.getEndpointZUuid(), EndpointVO.class);

        NodeVO nvoA = dbf.findByUuid(evoA.getNodeUuid(), NodeVO.class);
        NodeVO nvoZ = dbf.findByUuid(evoZ.getNodeUuid(), NodeVO.class);

        boolean crossTunnel = false;        //是否存在共点专线
        Integer crossVsi;                   //共点VSI
        Integer crossVlan = null;           //共点VLAN
        Integer vlanA;
        Integer vlanZ;


        if (msg.getCrossTunnelUuid() != null) {   //存在关联云专线，获取VSI和VLAN
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
            vo.setVsi(tunnelBase.getVsiAuto());
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
        vo.setDistance(Distance.getDistance(nvoA.getLongitude(), nvoA.getLatitude(), nvoZ.getLongitude(), nvoZ.getLatitude()));

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
            String switchPortUuidA = ts.getSwitchPortByStrategy(msg.getAccountUuid(), msg.getEndpointAUuid(), msg.getPortOfferingUuidA());
            if (switchPortUuidA == null) {
                throw new ApiMessageInterceptionException(argerr("该连接点A下无可用的端口"));
            }
            interfaceVOA.setUuid(Platform.getUuid());
            interfaceVOA.setAccountUuid(null);
            interfaceVOA.setOwnerAccountUuid(msg.getAccountUuid());
            interfaceVOA.setName(evoA.getName() + "_接口_" + Platform.getUuid().substring(0, 6));
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
            String switchPortUuidZ = ts.getSwitchPortByStrategy(msg.getAccountUuid(), msg.getEndpointZUuid(), msg.getPortOfferingUuidZ());
            if (switchPortUuidZ == null) {
                throw new ApiMessageInterceptionException(argerr("该连接点Z下无可用的端口"));
            }
            interfaceVOZ.setUuid(Platform.getUuid());
            interfaceVOZ.setAccountUuid(null);
            interfaceVOZ.setOwnerAccountUuid(msg.getAccountUuid());
            interfaceVOZ.setName(evoZ.getName() + "_接口_" + Platform.getUuid().substring(0, 6));
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

            dbf.getEntityManager().persist(interfaceVOA);
            dbf.getEntityManager().persist(interfaceVOZ);

        } else {                                              //有一端为新购
            if (msg.getInterfaceAUuid() == null) {            //A新购，Z已有
                String switchPortUuidA = ts.getSwitchPortByStrategy(msg.getAccountUuid(), msg.getEndpointAUuid(), msg.getPortOfferingUuidA());
                if (switchPortUuidA == null) {
                    throw new ApiMessageInterceptionException(argerr("该连接点A下无可用的端口"));
                }
                //保存数据
                interfaceVOA.setUuid(Platform.getUuid());
                interfaceVOA.setAccountUuid(null);
                interfaceVOA.setOwnerAccountUuid(msg.getAccountUuid());
                interfaceVOA.setName(evoA.getName() + "_接口_" + Platform.getUuid().substring(0, 6));
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

                dbf.getEntityManager().persist(interfaceVOA);
                interfaceVOZ = dbf.findByUuid(msg.getInterfaceZUuid(), InterfaceVO.class);

            } else {                                          //Z新购，A已有
                String switchPortUuidZ = ts.getSwitchPortByStrategy(msg.getAccountUuid(), msg.getEndpointZUuid(), msg.getPortOfferingUuidZ());
                if (switchPortUuidZ == null) {
                    throw new ApiMessageInterceptionException(argerr("该连接点Z下无可用的端口"));
                }
                //保存数据
                interfaceVOZ.setUuid(Platform.getUuid());
                interfaceVOZ.setAccountUuid(null);
                interfaceVOZ.setOwnerAccountUuid(msg.getAccountUuid());
                interfaceVOZ.setName(evoZ.getName() + "_接口_" + Platform.getUuid().substring(0, 6));
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
                dbf.getEntityManager().persist(interfaceVOZ);

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
            if (nvoA.getCountry().equals("CHINA")) {
                tunnelBase.createTunnelSwitchPortForAbroad(msg.getInnerConnectedEndpointUuid(), vo, true);
            } else {
                tunnelBase.createTunnelSwitchPortForAbroad(msg.getInnerConnectedEndpointUuid(), vo, false);
            }

        }

        dbf.getEntityManager().persist(tsvoA);
        dbf.getEntityManager().persist(tsvoZ);
        dbf.getEntityManager().persist(vo);

        return vo.getUuid();
    }

    private void afterCreateTunnel(TunnelVO vo, APICreateTunnelMsg msg, ReturnValueCompletion<TunnelInventory> completion) {

        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();

        boolean newBuyInterfaceA = false;   //A端是否为新购
        boolean newBuyInterfaceZ = false;   //Z端是否为新购

        if(msg.getInterfaceAUuid() == null){
            newBuyInterfaceA = true;
        }
        if(msg.getInterfaceZUuid() == null){
            newBuyInterfaceZ = true;
        }

        String interfaceUuidA = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid,vo.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag,"A")
                .select(TunnelSwitchPortVO_.interfaceUuid)
                .findValue();
        InterfaceVO interfaceVOA = dbf.findByUuid(interfaceUuidA,InterfaceVO.class);

        String interfaceUuidZ = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid,vo.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag,"Z")
                .select(TunnelSwitchPortVO_.interfaceUuid)
                .findValue();
        InterfaceVO interfaceVOZ = dbf.findByUuid(interfaceUuidZ,InterfaceVO.class);


        //支付接口订单
        APICreateBuyOrderMsg orderMsg = new APICreateBuyOrderMsg();
        List<ProductInfoForOrder> products = new ArrayList<>();
        if (newBuyInterfaceA) {
            ProductInfoForOrder productInfoForOrderA = tunnelBillingBase.createBuyOrderForInterface(interfaceVOA, msg.getPortOfferingUuidA(), new CreateInterfaceCallBack());
            productInfoForOrderA.setOpAccountUuid(msg.getSession().getAccountUuid());
            //productInfoForOrderA.setNotifyUrl(restf.getSendCommandUrl());
            products.add(productInfoForOrderA);
        }
        if (newBuyInterfaceZ) {
            ProductInfoForOrder productInfoForOrderZ = tunnelBillingBase.createBuyOrderForInterface(interfaceVOZ, msg.getPortOfferingUuidZ(), new CreateInterfaceCallBack());
            productInfoForOrderZ.setOpAccountUuid(msg.getSession().getAccountUuid());
            //productInfoForOrderZ.setNotifyUrl(restf.getSendCommandUrl());
            products.add(productInfoForOrderZ);
        }
        //支付专线订单
        ProductInfoForOrder productInfoForOrderTunnel = tunnelBillingBase.createBuyOrderForTunnel(vo, msg);

        CreateTunnelCallBack createTunnelCallBack = new CreateTunnelCallBack();
        createTunnelCallBack.setInterfaceAUuid(interfaceVOA.getUuid());
        createTunnelCallBack.setInterfaceZUuid(interfaceVOZ.getUuid());
        createTunnelCallBack.setNewBuyInterfaceA(newBuyInterfaceA);
        createTunnelCallBack.setNewBuyInterfaceZ(newBuyInterfaceZ);

        productInfoForOrderTunnel.setCallBackData(RESTApiDecoder.dump(createTunnelCallBack));

        products.add(productInfoForOrderTunnel);
        orderMsg.setProducts(products);

        //调用支付
        List<OrderInventory> inventories = tunnelBillingBase.createBuyOrder(orderMsg);

        if (!inventories.isEmpty()) {     //付款成功
            //支付成功修改状态,记录生效订单
            String orderUuid = null;
            for (OrderInventory orderInventory : inventories) {
                if (orderInventory.getProductType() == ProductType.TUNNEL) {
                    orderUuid = orderInventory.getUuid();
                    break;
                }
            }
            tunnelBillingBase.saveResourceOrderEffective(orderUuid, vo.getUuid(), vo.getClass().getSimpleName());
            //修改tunnel状态
            vo.setAccountUuid(vo.getOwnerAccountUuid());
            vo.setState(TunnelState.Deploying);
            vo.setStatus(TunnelStatus.Connecting);
            final TunnelVO vo2 = dbf.updateAndRefresh(vo);
            //修改interface状态,生成到期时间
            if (newBuyInterfaceA) {
                interfaceVOA.setAccountUuid(interfaceVOA.getOwnerAccountUuid());
                interfaceVOA.setState(InterfaceState.Paid);
                interfaceVOA.setExpireDate(tunnelBillingBase.getExpireDate(dbf.getCurrentSqlTime(), interfaceVOA.getProductChargeModel(), interfaceVOA.getDuration()));
                dbf.updateAndRefresh(interfaceVOA);
            }
            if (newBuyInterfaceZ) {
                interfaceVOZ.setState(InterfaceState.Paid);
                interfaceVOZ.setAccountUuid(interfaceVOZ.getOwnerAccountUuid());
                interfaceVOZ.setExpireDate(tunnelBillingBase.getExpireDate(dbf.getCurrentSqlTime(), interfaceVOZ.getProductChargeModel(), interfaceVOZ.getDuration()));
                dbf.updateAndRefresh(interfaceVOZ);
            }

            //创建任务
            TaskResourceVO taskResourceVO = new TunnelBase().newTaskResourceVO(vo2, TaskType.Create);

            CreateTunnelMsg createTunnelMsg = new CreateTunnelMsg();
            createTunnelMsg.setTunnelUuid(vo2.getUuid());
            createTunnelMsg.setTaskUuid(taskResourceVO.getUuid());
            bus.makeLocalServiceId(createTunnelMsg, TunnelConstant.SERVICE_ID);
            bus.send(createTunnelMsg, new CloudBusCallBack(null) {
                @Override
                public void run(MessageReply reply) {
                    if (reply.isSuccess()) {
                        completion.success(TunnelInventory.valueOf(dbf.reload(vo2)));
                    }else{
                        completion.success(TunnelInventory.valueOf(dbf.reload(vo2)));
                    }
                }
            });

        } else {
            //付款失败
            vo.setExpireDate(dbf.getCurrentSqlTime());
            dbf.updateAndRefresh(vo);

            completion.fail(errf.stringToOperationError("付款失败"));
        }


    }

    /**
     * 手动创建云专线
     *
     **/
    private void handle(APICreateTunnelManualMsg msg) {

        //创建Tunnel,TunnelSwitchPort
        String tunnelUuid = doCreateTunnelVOManual(msg);

        TunnelVO vo = dbf.findByUuid(tunnelUuid,TunnelVO.class);

        //创建数据后的支付和下发
        afterCreateTunnelManual(vo, msg, new ReturnValueCompletion<TunnelInventory>(msg) {
            APICreateTunnelEvent evt = new APICreateTunnelEvent(msg.getId());

            @Override
            public void success(TunnelInventory inv) {
                evt.setInventory(inv);
                bus.publish(evt);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                evt.setError(errorCode);
                bus.publish(evt);
            }
        });
    }

    @Transactional
    private String doCreateTunnelVOManual(APICreateTunnelManualMsg msg){
        TunnelBase tunnelBase = new TunnelBase();
        //保存数据
        TunnelVO vo = new TunnelVO();
        BandwidthOfferingVO bandwidthOfferingVO = dbf.findByUuid(msg.getBandwidthOfferingUuid(), BandwidthOfferingVO.class);
        InterfaceVO interfaceVOA = dbf.findByUuid(msg.getInterfaceAUuid(), InterfaceVO.class);
        InterfaceVO interfaceVOZ = dbf.findByUuid(msg.getInterfaceZUuid(), InterfaceVO.class);
        EndpointVO evoA = dbf.findByUuid(interfaceVOA.getEndpointUuid(), EndpointVO.class);
        EndpointVO evoZ = dbf.findByUuid(interfaceVOZ.getEndpointUuid(), EndpointVO.class);
        NodeVO nvoA = dbf.findByUuid(evoA.getNodeUuid(), NodeVO.class);
        NodeVO nvoZ = dbf.findByUuid(evoZ.getNodeUuid(), NodeVO.class);

        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(null);
        vo.setOwnerAccountUuid(msg.getAccountUuid());
        vo.setVsi(tunnelBase.getVsiAuto());
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
        vo.setDistance(Distance.getDistance(nvoA.getLongitude(), nvoA.getLatitude(), nvoZ.getLongitude(), nvoZ.getLatitude()));

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
            if (nvoA.getCountry().equals("CHINA")) {
                tunnelBase.createTunnelSwitchPortForAbroad(msg.getInnerConnectedEndpointUuid(), vo, true);
            } else {
                tunnelBase.createTunnelSwitchPortForAbroad(msg.getInnerConnectedEndpointUuid(), vo, false);
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

        dbf.getEntityManager().persist(tsvoA);
        dbf.getEntityManager().persist(tsvoZ);
        dbf.getEntityManager().persist(vo);

        return vo.getUuid();
    }

    private void afterCreateTunnelManual(TunnelVO vo, APICreateTunnelManualMsg msg, ReturnValueCompletion<TunnelInventory> completion) {
        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();

        //调用支付
        APICreateBuyOrderMsg orderMsg = new APICreateBuyOrderMsg();
        List<ProductInfoForOrder> products = new ArrayList<>();
        ProductInfoForOrder productInfoForOrderTunnel = tunnelBillingBase.createBuyOrderForTunnelManual(vo, msg);

        CreateTunnelCallBack createTunnelCallBack = new CreateTunnelCallBack();
        createTunnelCallBack.setInterfaceAUuid(null);
        createTunnelCallBack.setInterfaceZUuid(null);
        createTunnelCallBack.setNewBuyInterfaceA(false);
        createTunnelCallBack.setNewBuyInterfaceZ(false);

        productInfoForOrderTunnel.setCallBackData(RESTApiDecoder.dump(createTunnelCallBack));

        products.add(productInfoForOrderTunnel);
        orderMsg.setProducts(products);
        List<OrderInventory> inventories = tunnelBillingBase.createBuyOrder(orderMsg);

        if (inventories.isEmpty()) {
            vo.setExpireDate(dbf.getCurrentSqlTime());
            dbf.updateAndRefresh(vo);

            completion.fail(errf.stringToOperationError("付款失败"));
            return;
        }

        //支付成功修改状态,记录生效订单
        tunnelBillingBase.saveResourceOrderEffective(inventories.get(0).getUuid(), vo.getUuid(), vo.getClass().getSimpleName());

        vo.setAccountUuid(vo.getOwnerAccountUuid());
        vo.setState(TunnelState.Deploying);
        vo.setStatus(TunnelStatus.Connecting);
        final TunnelVO vo2 = dbf.updateAndRefresh(vo);

        //创建任务
        TaskResourceVO taskResourceVO = new TunnelBase().newTaskResourceVO(vo2, TaskType.Create);

        CreateTunnelMsg createTunnelMsg = new CreateTunnelMsg();
        createTunnelMsg.setTunnelUuid(vo2.getUuid());
        createTunnelMsg.setTaskUuid(taskResourceVO.getUuid());
        bus.makeLocalServiceId(createTunnelMsg, TunnelConstant.SERVICE_ID);
        bus.send(createTunnelMsg, new CloudBusCallBack(null) {
            @Override
            public void run(MessageReply reply) {
                if (reply.isSuccess()) {
                    completion.success(TunnelInventory.valueOf(dbf.reload(vo2)));
                } else {
                    completion.success(TunnelInventory.valueOf(dbf.reload(vo2)));
                }
            }
        });
    }

    /**
     * 切换云专线已有的物理接口和修改端口VLAN（下发-保存）
     *
     **/
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
                    TaskResourceVO taskResourceVO = new TunnelBase().newTaskResourceVO(vo, TaskType.ModifyPorts);

                    ModifyTunnelPortsMsg modifyTunnelPortsMsg = new ModifyTunnelPortsMsg();
                    modifyTunnelPortsMsg.setTunnelUuid(vo.getUuid());
                    modifyTunnelPortsMsg.setTaskUuid(taskResourceVO.getUuid());
                    bus.makeLocalServiceId(modifyTunnelPortsMsg, TunnelConstant.SERVICE_ID);
                    bus.send(modifyTunnelPortsMsg, new CloudBusCallBack(null) {
                        @Override
                        public void run(MessageReply reply) {
                            if (reply.isSuccess()) {
                                logger.info(String.format("Successfully update vlan of tunnel[uuid:%s].", vo.getUuid()));
                                trigger.next();
                            } else {
                                logger.info(String.format("Failed update vlan of tunnel[uuid:%s].", vo.getUuid()));
                                trigger.fail(reply.getError());
                            }
                        }
                    });
                }else{
                    trigger.next();
                }


            }

            @Override
            public void rollback(FlowRollback trigger, Map data) {
                trigger.rollback();
            }
        }).done(new FlowDoneHandler(null) {
            @Override
            public void handle(Map data) {
                evt.setInventory(TunnelInventory.valueOf(dbf.reload(vo)));
                bus.publish(evt);
            }
        }).error(new FlowErrorHandler(null) {
            @Override
            public void handle(ErrorCode errCode, Map data) {
                evt.setError(errf.stringToOperationError("update vlan failed!"));
                bus.publish(evt);
            }
        }).start();
    }

    /**
     * 强制切换云专线已有的物理接口和修改端口VLAN（仅保存）
     *
     **/
    @Transactional
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
            dbf.getEntityManager().merge(tunnelSwitchPortA);
        if (updateZ)
            dbf.getEntityManager().merge(tunnelSwitchPortZ);

        APIUpdateForciblyTunnelVlanEvent evt = new APIUpdateForciblyTunnelVlanEvent(msg.getId());
        evt.setInventory(TunnelInventory.valueOf(vo));
        bus.publish(evt);

    }

    /**
     * 修改云专线的名称和描述
     *
     **/
    private void handle(APIUpdateTunnelMsg msg) {
        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);
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

        APIUpdateTunnelEvent evt = new APIUpdateTunnelEvent(msg.getId());
        evt.setInventory(TunnelInventory.valueOf(vo));
        bus.publish(evt);
    }

    /**
     * 调整云专线带宽
     *
     **/
    private void handle(APIUpdateTunnelBandwidthMsg msg) {
        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();
        TunnelBase tunnelBase = new TunnelBase();
        APIUpdateTunnelBandwidthEvent evt = new APIUpdateTunnelBandwidthEvent(msg.getId());

        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);

        String nodeAUuid = tunnelBase.getNodeUuid(vo, "A");
        String nodeZUuid = tunnelBase.getNodeUuid(vo, "Z");

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
        orderMsg.setDescriptionData(tunnelBillingBase.getDescriptionForTunnel(vo));
        orderMsg.setCallBackData(RESTApiDecoder.dump(uc));
        orderMsg.setUnits(tunnelBillingBase.getTunnelPriceUnit(msg.getBandwidthOfferingUuid(), nodeAUuid,
                nodeZUuid, innerEndpointUuid));
        orderMsg.setProductType(ProductType.TUNNEL);
        orderMsg.setAccountUuid(msg.getAccountUuid());
        orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
        orderMsg.setStartTime(dbf.getCurrentSqlTime());
        orderMsg.setExpiredTime(vo.getExpireDate());
        orderMsg.setNotifyUrl(restf.getSendCommandUrl());

        OrderInventory orderInventory = tunnelBillingBase.createOrder(orderMsg);
        if (orderInventory != null) {
            //付款成功,记录生效订单
            tunnelBillingBase.saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());

            vo.setBandwidth(bandwidthOfferingVO.getBandwidth());
            vo = dbf.updateAndRefresh(vo);

            //创建任务
            TaskResourceVO taskResourceVO = new TunnelBase().newTaskResourceVO(vo, TaskType.ModifyBandwidth);

            ModifyTunnelBandwidthMsg modifyTunnelBandwidthMsg = new ModifyTunnelBandwidthMsg();
            modifyTunnelBandwidthMsg.setTunnelUuid(vo.getUuid());
            modifyTunnelBandwidthMsg.setTaskUuid(taskResourceVO.getUuid());
            bus.makeLocalServiceId(modifyTunnelBandwidthMsg, TunnelConstant.SERVICE_ID);
            bus.send(modifyTunnelBandwidthMsg);
            evt.setInventory(TunnelInventory.valueOf(vo));
            bus.publish(evt);
        } else {
            evt.setError(errf.stringToOperationError("订单操作失败"));
            bus.publish(evt);
        }
    }

    /**
     * 云专线续费
     *
     **/
    private void handle(APIRenewTunnelMsg msg) {
        APIRenewTunnelReply reply
                = renewTunnel(msg.getUuid(),
                msg.getDuration(),
                msg.getProductChargeModel(),
                msg.getAccountUuid(),
                msg.getSession().getAccountUuid());

        bus.reply(msg, reply);
    }

    /**
     * 云专线自动续费
     *
     **/
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

        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();
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
        renewOrderMsg.setDescriptionData(tunnelBillingBase.getDescriptionForTunnel(vo));
        renewOrderMsg.setProductChargeModel(productChargeModel);
        renewOrderMsg.setAccountUuid(accountUuid);
        renewOrderMsg.setOpAccountUuid(opAccountUuid);
        renewOrderMsg.setStartTime(dbf.getCurrentSqlTime());
        renewOrderMsg.setExpiredTime(vo.getExpireDate());
        renewOrderMsg.setNotifyUrl(restf.getSendCommandUrl());
        renewOrderMsg.setCallBackData(RESTApiDecoder.dump(rc));

        OrderInventory orderInventory = tunnelBillingBase.createOrder(renewOrderMsg);

        if (orderInventory != null) {
            //续费或者自动续费成功,记录生效订单
            tunnelBillingBase.saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());
            //更新到期时间
            vo.setDuration(duration);
            vo.setProductChargeModel(productChargeModel);
            vo.setExpireDate(tunnelBillingBase.getExpireDate(newTime, productChargeModel, duration));

            vo = dbf.updateAndRefresh(vo);
            reply.setInventory(TunnelInventory.valueOf(vo));
        } else {
            reply.setError(errf.stringToOperationError("订单操作失败"));
        }

        return reply;
    }

    /**
     * 云专线赔偿
     *
     **/
    private void handle(APISLATunnelMsg msg) {
        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();

        APISLATunnelReply reply = new APISLATunnelReply();

        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);
        Timestamp newTime = vo.getExpireDate();

        //赔偿
        SalTunnelCallBack sc = new SalTunnelCallBack();
        APICreateSLACompensationOrderMsg slaCompensationOrderMsg =
                new APICreateSLACompensationOrderMsg();
        slaCompensationOrderMsg.setProductUuid(vo.getUuid());
        slaCompensationOrderMsg.setProductName(vo.getName());
        slaCompensationOrderMsg.setDescriptionData(tunnelBillingBase.getDescriptionForTunnel(vo));
        slaCompensationOrderMsg.setProductType(ProductType.TUNNEL);
        slaCompensationOrderMsg.setDuration(msg.getDuration());
        slaCompensationOrderMsg.setAccountUuid(msg.getAccountUuid());
        slaCompensationOrderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
        slaCompensationOrderMsg.setStartTime(dbf.getCurrentSqlTime());
        slaCompensationOrderMsg.setExpiredTime(vo.getExpireDate());
        slaCompensationOrderMsg.setCallBackData(RESTApiDecoder.dump(sc));
        slaCompensationOrderMsg.setNotifyUrl(restf.getSendCommandUrl());

        OrderInventory orderInventory = tunnelBillingBase.createOrder(slaCompensationOrderMsg);

        if (orderInventory != null) {
            //续费或者赔偿成功,记录生效订单
            tunnelBillingBase.saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());
            //更新到期时间
            vo.setDuration(msg.getDuration());
            vo.setProductChargeModel(ProductChargeModel.BY_DAY);
            vo.setExpireDate(tunnelBillingBase.getExpireDate(newTime, ProductChargeModel.BY_DAY, msg.getDuration()));

            vo = dbf.updateAndRefresh(vo);
            reply.setInventory(TunnelInventory.valueOf(vo));
        } else {
            reply.setError(errf.stringToOperationError("订单操作失败"));
        }

        bus.reply(msg, reply);
    }

    /**
     * 云专线的退订和删除
     * 1.Unsupport    (无法开通，未连接)  删除
     * 2.Enabled    accountUuid != null  （已开通）   退订，下发，删除
     * 3.Disabled   (已关闭)   退订，删除
     * 4.Enabled    accountUuid = null  (退订成功，下发失败)     下发，删除
     **/
    private void handle(APIDeleteTunnelMsg msg) {
        APIDeleteTunnelEvent evt = new APIDeleteTunnelEvent(msg.getId());
        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();
        TunnelBase tunnelBase = new TunnelBase();

        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);

        for (TunnelDeletionExtensionPoint extp : pluginRgty.getExtensionList(TunnelDeletionExtensionPoint.class)) {
            extp.preDelete();
        }

        CollectionUtils.safeForEach(pluginRgty.getExtensionList(TunnelDeletionExtensionPoint.class), new ForEachFunction<TunnelDeletionExtensionPoint>() {
            @Override
            public void run(TunnelDeletionExtensionPoint arg) {
                arg.beforeDelete();
            }
        });

        if (vo.getState() == TunnelState.Unsupport) {         //1
            tunnelBase.deleteTunnel(vo);
            evt.setInventory(TunnelInventory.valueOf(vo));
            bus.publish(evt);
        }else if(vo.getState() == TunnelState.Enabled && vo.getAccountUuid() == null){          //4
            //下发删除
            taskDeleteTunnel(vo,new ReturnValueCompletion<TunnelInventory>(msg) {
                @Override
                public void success(TunnelInventory inv) {
                    evt.setInventory(inv);
                    bus.publish(evt);
                }

                @Override
                public void fail(ErrorCode errorCode) {
                    evt.setError(errorCode);
                    bus.publish(evt);
                }
            });

        }else if((vo.getState() == TunnelState.Enabled && vo.getAccountUuid() != null) || vo.getState() == TunnelState.Disabled){  //2,3
            if (vo.getExpireDate().before(Timestamp.valueOf(LocalDateTime.now()))){     //专线已到期，不用退订
                if (vo.getState() == TunnelState.Enabled) {

                    vo.setAccountUuid(null);
                    final TunnelVO vo2 = dbf.updateAndRefresh(vo);

                    //下发删除
                    taskDeleteTunnel(vo2,new ReturnValueCompletion<TunnelInventory>(msg) {
                        @Override
                        public void success(TunnelInventory inv) {
                            evt.setInventory(inv);
                            bus.publish(evt);
                        }

                        @Override
                        public void fail(ErrorCode errorCode) {
                            evt.setError(errorCode);
                            bus.publish(evt);
                        }
                    });

                }else{
                    tunnelBase.deleteTunnel(vo);
                    evt.setInventory(TunnelInventory.valueOf(vo));
                    bus.publish(evt);
                }
            }else{
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
                orderMsg.setDescriptionData(tunnelBillingBase.getDescriptionForTunnel(vo));
                orderMsg.setCallBackData(RESTApiDecoder.dump(dc));
                orderMsg.setNotifyUrl(restf.getSendCommandUrl());

                OrderInventory orderInventory = tunnelBillingBase.createOrder(orderMsg);
                if (orderInventory == null) {
                    evt.setError(errf.stringToOperationError("退订失败"));
                    bus.publish(evt);
                } else {
                    if (vo.getState() == TunnelState.Enabled) {
                        //退订成功,记录生效订单
                        tunnelBillingBase.saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());

                        vo.setAccountUuid(null);
                        final TunnelVO vo2 = dbf.updateAndRefresh(vo);

                        //下发删除
                        taskDeleteTunnel(vo2,new ReturnValueCompletion<TunnelInventory>(msg) {
                            @Override
                            public void success(TunnelInventory inv) {
                                evt.setInventory(inv);
                                bus.publish(evt);
                            }

                            @Override
                            public void fail(ErrorCode errorCode) {
                                evt.setError(errorCode);
                                bus.publish(evt);
                            }
                        });

                    } else {
                        //退订成功,记录生效订单
                        tunnelBillingBase.saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());

                        tunnelBase.deleteTunnel(vo);
                        evt.setInventory(TunnelInventory.valueOf(vo));
                        bus.publish(evt);
                    }
                }
            }
        }else{
            evt.setError(errf.stringToOperationError("该删除专线操作没有符合正确的条件！"));
            bus.publish(evt);
        }
    }

    private void taskDeleteTunnel(TunnelVO vo, ReturnValueCompletion<TunnelInventory> completion){
        //创建任务
        TaskResourceVO taskResourceVO = new TunnelBase().newTaskResourceVO(vo, TaskType.Delete);

        DeleteTunnelMsg deleteTunnelMsg = new DeleteTunnelMsg();
        deleteTunnelMsg.setTunnelUuid(vo.getUuid());
        deleteTunnelMsg.setTaskUuid(taskResourceVO.getUuid());
        bus.makeLocalServiceId(deleteTunnelMsg, TunnelConstant.SERVICE_ID);
        bus.send(deleteTunnelMsg, new CloudBusCallBack(null) {
            @Override
            public void run(MessageReply reply) {
                if (reply.isSuccess()) {
                    completion.success(TunnelInventory.valueOf(vo));
                }else{
                    completion.fail(reply.getError());
                }
            }
        });
    }

    /**
     * 云专线强制删除，只退订，不下发
     * Deployfailure
     * Enabled
     * Disabled
     *
     **/
    private void handle(APIDeleteForciblyTunnelMsg msg) {
        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();
        TunnelBase tunnelBase = new TunnelBase();
        APIDeleteForciblyTunnelEvent evt = new APIDeleteForciblyTunnelEvent(msg.getId());

        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);

        if (vo.getExpireDate() != null && (vo.getExpireDate().before(Timestamp.valueOf(LocalDateTime.now())) || vo.getAccountUuid() == null)) {
            tunnelBase.deleteTunnel(vo);
            evt.setInventory(TunnelInventory.valueOf(vo));
            bus.publish(evt);
            return;
        }

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
        orderMsg.setDescriptionData(tunnelBillingBase.getDescriptionForTunnel(vo));
        orderMsg.setCallBackData(RESTApiDecoder.dump(dc));
        orderMsg.setNotifyUrl(restf.getSendCommandUrl());

        OrderInventory orderInventory = tunnelBillingBase.createOrder(orderMsg);
        if (orderInventory != null) {
            //退订成功,记录生效订单
            tunnelBillingBase.saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());

            tunnelBase.deleteTunnel(vo);

            evt.setInventory(TunnelInventory.valueOf(vo));
        } else {
            evt.setError(errf.stringToOperationError("退订失败"));
        }
        bus.publish(evt);
    }

    /**
     * 更改云专线的状况：恢复链接，关闭连接，开通（是否仅保存），无法开通
     *
     **/
    private void handle(APIUpdateTunnelStateMsg msg) {
        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();
        TunnelBase tunnelBase = new TunnelBase();

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
                orderMsg.setDescriptionData(tunnelBillingBase.getDescriptionForTunnel(vo));
                orderMsg.setCallBackData(RESTApiDecoder.dump(dc));
                orderMsg.setNotifyUrl(restf.getSendCommandUrl());

                OrderInventory orderInventory = tunnelBillingBase.createOrder(orderMsg);
                if (orderInventory == null) {
                    evt.setError(errf.stringToOperationError("退订失败"));
                    bus.publish(evt);
                } else {
                    //退订成功,记录生效订单
                    tunnelBillingBase.saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());
                    vo.setState(TunnelState.Unsupport);
                    vo.setExpireDate(dbf.getCurrentSqlTime());
                    vo = dbf.updateAndRefresh(vo);

                    evt.setInventory(TunnelInventory.valueOf(vo));
                    bus.publish(evt);
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

                    vo = dbf.updateAndRefresh(vo);

                    evt.setInventory(TunnelInventory.valueOf(vo));
                    bus.publish(evt);
                } else {
                    //创建任务
                    TaskResourceVO taskResourceVO = tunnelBase.newTaskResourceVO(vo, TaskType.Enabled);

                    taskEnableTunnel(vo,taskResourceVO,new ReturnValueCompletion<TunnelInventory>(msg) {
                        @Override
                        public void success(TunnelInventory inv) {
                            evt.setInventory(inv);
                            bus.publish(evt);
                        }

                        @Override
                        public void fail(ErrorCode errorCode) {
                            evt.setError(errorCode);
                            bus.publish(evt);
                        }
                    });
                }
            }

        } else {                                           //恢复连接和关闭连接
            //创建任务
            TaskResourceVO taskResourceVO = tunnelBase.newTaskResourceVO(vo, TaskType.valueOf(msg.getState().toString()));

            if (msg.getState() == TunnelState.Enabled) {
                taskEnableTunnel(vo,taskResourceVO,new ReturnValueCompletion<TunnelInventory>(msg) {
                    @Override
                    public void success(TunnelInventory inv) {
                        evt.setInventory(inv);
                        bus.publish(evt);
                    }

                    @Override
                    public void fail(ErrorCode errorCode) {
                        evt.setError(errorCode);
                        bus.publish(evt);
                    }
                });
            } else {
                taskDisableTunnel(vo,taskResourceVO,new ReturnValueCompletion<TunnelInventory>(msg) {
                    @Override
                    public void success(TunnelInventory inv) {
                        evt.setInventory(inv);
                        bus.publish(evt);
                    }

                    @Override
                    public void fail(ErrorCode errorCode) {
                        evt.setError(errorCode);
                        bus.publish(evt);
                    }
                });
            }
        }
    }

    private void taskEnableTunnel(TunnelVO vo,TaskResourceVO taskResourceVO,ReturnValueCompletion<TunnelInventory> completion){
        EnabledTunnelMsg enabledTunnelMsg = new EnabledTunnelMsg();
        enabledTunnelMsg.setTunnelUuid(vo.getUuid());
        enabledTunnelMsg.setTaskUuid(taskResourceVO.getUuid());
        bus.makeLocalServiceId(enabledTunnelMsg, TunnelConstant.SERVICE_ID);
        bus.send(enabledTunnelMsg, new CloudBusCallBack(null) {
            @Override
            public void run(MessageReply reply) {
                if (reply.isSuccess()) {
                    completion.success(TunnelInventory.valueOf(dbf.reload(vo)));
                }else{
                    completion.fail(reply.getError());
                }
            }
        });
    }

    private void taskDisableTunnel(TunnelVO vo,TaskResourceVO taskResourceVO,ReturnValueCompletion<TunnelInventory> completion){
        DisabledTunnelMsg disabledTunnelMsg = new DisabledTunnelMsg();
        disabledTunnelMsg.setTunnelUuid(vo.getUuid());
        disabledTunnelMsg.setTaskUuid(taskResourceVO.getUuid());
        bus.makeLocalServiceId(disabledTunnelMsg, TunnelConstant.SERVICE_ID);
        bus.send(disabledTunnelMsg, new CloudBusCallBack(null) {
            @Override
            public void run(MessageReply reply) {
                if (reply.isSuccess()) {
                    completion.success(TunnelInventory.valueOf(dbf.reload(vo)));
                }else{
                    completion.fail(reply.getError());
                }
            }
        });
    }

    /**
     * QINQ专线新增内部VLAN段
     *
     **/
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
                TaskResourceVO taskResourceVO = new TunnelBase().newTaskResourceVO(vo, TaskType.ModifyPorts);

                ModifyTunnelPortsMsg modifyTunnelPortsMsg = new ModifyTunnelPortsMsg();
                modifyTunnelPortsMsg.setTunnelUuid(vo.getUuid());
                modifyTunnelPortsMsg.setTaskUuid(taskResourceVO.getUuid());
                bus.makeLocalServiceId(modifyTunnelPortsMsg, TunnelConstant.SERVICE_ID);
                bus.send(modifyTunnelPortsMsg, new CloudBusCallBack(null) {
                    @Override
                    public void run(MessageReply reply) {
                        if (reply.isSuccess()) {
                            logger.info(String.format("新增 tunnel[uuid:%s] 内部VLAN 成功.", vo.getUuid()));
                            trigger.next();
                        } else {
                            logger.info(String.format("新增 tunnel[uuid:%s] 内部VLAN 失败.", vo.getUuid()));
                            trigger.fail(reply.getError());
                        }
                    }
                });


            }

            @Override
            public void rollback(FlowRollback trigger, Map data) {
                trigger.rollback();
            }
        }).done(new FlowDoneHandler(null) {
            @Override
            public void handle(Map data) {
                evt.setInventory(QinqInventory.valueOf(qinqVO));
                bus.publish(evt);
            }
        }).error(new FlowErrorHandler(null) {
            @Override
            public void handle(ErrorCode errCode, Map data) {
                evt.setError(errf.stringToOperationError("create innervlan failed!"));
                bus.publish(evt);
            }
        }).start();


    }

    /**
     * QINQ专线删除内部VLAN段
     *
     **/
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
                TaskResourceVO taskResourceVO = new TunnelBase().newTaskResourceVO(vo, TaskType.ModifyPorts);

                ModifyTunnelPortsMsg modifyTunnelPortsMsg = new ModifyTunnelPortsMsg();
                modifyTunnelPortsMsg.setTunnelUuid(vo.getUuid());
                modifyTunnelPortsMsg.setTaskUuid(taskResourceVO.getUuid());
                bus.makeLocalServiceId(modifyTunnelPortsMsg, TunnelConstant.SERVICE_ID);
                bus.send(modifyTunnelPortsMsg, new CloudBusCallBack(null) {
                    @Override
                    public void run(MessageReply reply) {
                        if (reply.isSuccess()) {
                            logger.info(String.format("删除 tunnel[uuid:%s] 内部VLAN 成功.", vo.getUuid()));

                            trigger.next();
                        } else {
                            logger.info(String.format("删除 tunnel[uuid:%s] 内部VLAN 失败.", vo.getUuid()));

                            trigger.fail(reply.getError());
                        }
                    }
                });


            }

            @Override
            public void rollback(FlowRollback trigger, Map data) {
                trigger.rollback();
            }
        }).done(new FlowDoneHandler(null) {
            @Override
            public void handle(Map data) {
                evt.setInventory(QinqInventory.valueOf(qinqVO));
                bus.publish(evt);

            }
        }).error(new FlowErrorHandler(null) {
            @Override
            public void handle(ErrorCode errCode, Map data) {
                evt.setError(errf.stringToOperationError("delete innervlan failed!"));
                bus.publish(evt);
            }
        }).start();


    }

    /*****************************  Get/Query/List Message For Interface and Tunnel **************************/

    /**
     * 通过连接点和端口规格获取可用的端口
     */
    private void handle(APIListSwitchPortByTypeMsg msg) {
        List<SwitchPortVO> ports = new TunnelBase().getSwitchPortByType(msg.getUuid(), msg.getType());
        APIListSwitchPortByTypeReply reply = new APIListSwitchPortByTypeReply();
        reply.setInventories(SwitchPortInventory.valueOf(ports));
        bus.reply(msg, reply);
    }

    /**
     * 通过连接点获取可用的端口规格
     */
    private void handle(APIGetInterfaceTypeMsg msg) {
        APIGetInterfaceTypeReply reply = new APIGetInterfaceTypeReply();
        reply.setInventories(PortOfferingInventory.valueOf(new TunnelBase().getPortTypeByEndpoint(msg.getUuid())));
        bus.reply(msg, reply);
    }

    /**
     * 通过共点接口查询共点专线
     */
    private void handle(APIListCrossTunnelMsg msg) {
        APIListCrossTunnelReply reply = new APIListCrossTunnelReply();
        List<String> tunnelUuids = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.interfaceUuid, msg.getUuid())
                .select(TunnelSwitchPortVO_.tunnelUuid)
                .listValues();
        List<TunnelVO> tunnelVOS = new ArrayList<>();
        if (!tunnelUuids.isEmpty()) {
            tunnelVOS = Q.New(TunnelVO.class).
                    in(TunnelVO_.uuid, tunnelUuids).
                    eq(TunnelVO_.accountUuid, msg.getAccountUuid()).
                    eq(TunnelVO_.state, TunnelState.Enabled)
                    .list();
        }
        reply.setInventories(TunnelInventory.valueOf(tunnelVOS));
        bus.reply(msg, reply);
    }

    /**
     * 通过通道两端连接点查询存在的互联连接点
     */
    private void handle(APIListInnerEndpointMsg msg) {
        APIListInnerEndpointReply reply = new APIListInnerEndpointReply();

        //根据连接点查询节点
        EndpointVO endpointVOA = dbf.findByUuid(msg.getEndpointAUuid(), EndpointVO.class);
        EndpointVO endpointVOZ = dbf.findByUuid(msg.getEndpointZUuid(), EndpointVO.class);

        NodeVO nodeVOA = dbf.findByUuid(endpointVOA.getNodeUuid(), NodeVO.class);
        NodeVO nodeVOZ = dbf.findByUuid(endpointVOZ.getNodeUuid(), NodeVO.class);

        String conntectedEndpoint = null;
        List<InnerConnectedEndpointVO> innerEndpoints = new ArrayList<>();
        if (nodeVOA.getCountry().equals("CHINA") && !nodeVOZ.getCountry().equals("CHINA")) {
            conntectedEndpoint = msg.getEndpointZUuid();
        }
        if (!nodeVOA.getCountry().equals("CHINA") && nodeVOZ.getCountry().equals("CHINA")) {
            conntectedEndpoint = msg.getEndpointAUuid();
        }
        if (conntectedEndpoint != null) {
            innerEndpoints = Q.New(InnerConnectedEndpointVO.class)
                    .eq(InnerConnectedEndpointVO_.connectedEndpointUuid, conntectedEndpoint)
                    .list();
        }

        reply.setInventories(InnerConnectedEndpointInventory.valueOf(innerEndpoints));
        bus.reply(msg, reply);
    }

    /**
     * 自动分配VLAN消息接口
     */
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

    /**
     * 购买时查询物理接口价格
     */
    private void handle(APIGetInterfacePriceMsg msg) {

        APIGetProductPriceMsg pmsg = new APIGetProductPriceMsg();
        pmsg.setProductChargeModel(msg.getProductChargeModel());
        pmsg.setDuration(msg.getDuration());
        pmsg.setAccountUuid(msg.getAccountUuid());
        pmsg.setUnits(new TunnelBillingBase().getInterfacePriceUnit(msg.getPortOfferingUuid()));
        APIGetProductPriceReply reply = new TunnelRESTCaller().syncJsonPost(pmsg);
        bus.reply(msg, new APIGetInterfacePriceReply(reply));
    }

    /**
     * 退订时查询物理接口差价
     */
    private void handle(APIGetUnscribeInterfacePriceDiffMsg msg) {

        InterfaceVO vo = dbf.findByUuid(msg.getUuid(), InterfaceVO.class);

        APIGetUnscribeProductPriceDiffMsg upmsg = new APIGetUnscribeProductPriceDiffMsg();

        upmsg.setAccountUuid(vo.getAccountUuid());
        upmsg.setProductUuid(msg.getUuid());
        upmsg.setExpiredTime(vo.getExpireDate());

        APIGetUnscribeProductPriceDiffReply reply = new TunnelRESTCaller().syncJsonPost(upmsg);

        bus.reply(msg, new APIGetUnscribeInterfacePriceDiffReply(reply));
    }

    /**
     * 购买时查询云专线价格
     */
    private void handle(APIGetTunnelPriceMsg msg) {

        APIGetProductPriceMsg pmsg = new APIGetProductPriceMsg();
        pmsg.setProductChargeModel(msg.getProductChargeModel());
        pmsg.setDuration(msg.getDuration());
        pmsg.setAccountUuid(msg.getAccountUuid());
        pmsg.setUnits(new TunnelBillingBase().getTunnelPriceUnit(msg.getBandwidthOfferingUuid(), msg.getNodeAUuid(),
                msg.getNodeZUuid(), msg.getInnerEndpointUuid()));
        APIGetProductPriceReply reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(pmsg);
        bus.reply(msg, new APIGetTunnelPriceReply(reply));
    }

    /**
     * 调整带宽时查询云专线差价
     */
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
        pmsg.setUnits(new TunnelBillingBase().getTunnelPriceUnit(msg.getBandwidthOfferingUuid(), endpointVOA.getNodeUuid(),
                endpointVOZ.getNodeUuid(), innerEndpointUuid));
        pmsg.setProductUuid(msg.getUuid());
        pmsg.setAccountUuid(msg.getAccountUuid());
        pmsg.setExpiredTime(dbf.findByUuid(msg.getUuid(), TunnelVO.class).getExpireDate());

        APIGetModifyProductPriceDiffReply reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(pmsg);
        bus.reply(msg, new APIGetModifyTunnelPriceDiffReply(reply));
    }

    /**
     * 退订时查询云专线差价
     */
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

    /**
     * Alarm查询云专线信息
     */
    private void handle(APIQueryTunnelDetailForAlarmMsg msg) {
        TunnelBase tunnelBase = new TunnelBase();
        Map map = new HashMap();
        List<FalconApiCommands.Tunnel> tunnels = new ArrayList<>();

        for (String tunnelUuid : msg.getTunnelUuidList()) {
            TunnelVO tunnel = Q.New(TunnelVO.class).eq(TunnelVO_.uuid, tunnelUuid).findValue();

            FalconApiCommands.Tunnel tunnelCmd = new FalconApiCommands.Tunnel();
            if(tunnel == null)
                throw new IllegalArgumentException(String.format("tunnel %s not exist!",tunnelUuid));

            tunnelCmd.setTunnel_id(tunnel.getUuid());
            tunnelCmd.setBandwidth(tunnel.getBandwidth());
            tunnelCmd.setUser_id(null);
            tunnelCmd.setRules(null);

            List<TunnelSwitchPortVO> tunnelSwitchPortVOS = Q.New(TunnelSwitchPortVO.class).eq(TunnelSwitchPortVO_.tunnelUuid, tunnelUuid).list();
            for (TunnelSwitchPortVO vo : tunnelSwitchPortVOS) {
                if ("A".equals(vo.getSortTag())) {
                    tunnelCmd.setEndpointA_ip(tunnelBase.getPhysicalSwitch(vo.getSwitchPortUuid()));
                    tunnelCmd.setEndpointA_vid(vo.getVlan());
                } else if ("Z".equals(vo.getSortTag())) {
                    tunnelCmd.setEndpointB_ip(tunnelBase.getPhysicalSwitch(vo.getSwitchPortUuid()));
                    tunnelCmd.setEndpointB_vid(vo.getVlan());
                }
            }

            map.put(tunnelUuid, JSON.toJSONString(tunnelCmd, SerializerFeature.WriteMapNullValue));
        }

        APIQueryTunnelDetailForAlarmReply reply = new APIQueryTunnelDetailForAlarmReply();
        reply.setMap(map);
        bus.reply(msg, reply);
    }

    /**
     * 查询已开通云专线的TraceRoute
     */
    private void handle(APIListTraceRouteMsg msg){
        APIListTraceRouteReply traceRouteReply = new APIListTraceRouteReply();
        List<TraceRouteVO> traceRouteVOS = Q.New(TraceRouteVO.class)
                .eq(TraceRouteVO_.tunnelUuid,msg.getTunnelUuid())
                .list();
        if(traceRouteVOS.isEmpty() || msg.isTraceAgain()){
            ListTraceRouteMsg listTraceRouteMsg = new ListTraceRouteMsg();
            listTraceRouteMsg.setTunnelUuid(msg.getTunnelUuid());

            bus.makeLocalServiceId(listTraceRouteMsg, TunnelConstant.SERVICE_ID);
            bus.send(listTraceRouteMsg, new CloudBusCallBack(null) {
                @Override
                public void run(MessageReply reply) {
                    if (reply.isSuccess()) {
                        if(!traceRouteVOS.isEmpty() && msg.isTraceAgain()){
                            UpdateQuery.New(TraceRouteVO.class)
                                    .eq(TraceRouteVO_.tunnelUuid,msg.getTunnelUuid())
                                    .delete();
                        }
                        ListTraceRouteReply listTraceRouteReply = reply.castReply();
                        List<List<String>> results = listTraceRouteReply.getResults();
                        for(List<String> result :results){
                            for(String s: result){
                                s = s.trim().replaceAll("\\s+ms","ms");
                                String[] arrS = s.split("\\s+");
                                if(!arrS[1].equals("*")){
                                    TraceRouteVO vo = new TraceRouteVO();
                                    vo.setUuid(Platform.getUuid());
                                    vo.setTunnelUuid(msg.getTunnelUuid());
                                    vo.setTraceSort(new Integer(arrS[0]));
                                    vo.setRouteIP(arrS[1]);
                                    vo.setTimesFirst(arrS[2]);
                                    vo.setTimesSecond(arrS[3]);
                                    vo.setTimesThird(arrS[4]);
                                    dbf.persistAndRefresh(vo);
                                }
                            }
                        }
                        List<TraceRouteVO> traceRouteList = Q.New(TraceRouteVO.class)
                                .eq(TraceRouteVO_.tunnelUuid,msg.getTunnelUuid())
                                .list();
                        traceRouteReply.setInventories(TraceRouteInventory.valueOf(traceRouteList));
                        bus.reply(msg, traceRouteReply);
                    }else{
                        traceRouteReply.setError(reply.getError());
                        bus.reply(msg, traceRouteReply);
                    }
                }
            });

        }else{
            traceRouteReply.setInventories(TraceRouteInventory.valueOf(traceRouteVOS));
            bus.reply(msg, traceRouteReply);
        }

    }

    /*******************************    The Following billing call back  processing **************************************************/

    private boolean orderIsExist(String orderUuid) {
        return Q.New(ResourceOrderEffectiveVO.class)
                .eq(ResourceOrderEffectiveVO_.orderUuid, orderUuid)
                .isExists();
    }

    private void renewOrSla(OrderCallbackCmd cmd) {
        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();

        if (cmd.getProductType() == ProductType.PORT) {
            InterfaceVO vo = dbf.findByUuid(cmd.getPorductUuid(), InterfaceVO.class);
            vo.setExpireDate(tunnelBillingBase.getExpireDate(vo.getExpireDate(), cmd.getProductChargeModel(), cmd.getDuration()));
            vo.setDuration(cmd.getDuration());
            vo.setProductChargeModel(cmd.getProductChargeModel());
            dbf.updateAndRefresh(vo);
            //付款成功,记录生效订单
            tunnelBillingBase.saveResourceOrderEffective(cmd.getOrderUuid(), vo.getUuid(), vo.getClass().getSimpleName());

        } else if (cmd.getProductType() == ProductType.TUNNEL) {
            TunnelVO vo = dbf.findByUuid(cmd.getPorductUuid(), TunnelVO.class);
            vo.setExpireDate(tunnelBillingBase.getExpireDate(vo.getExpireDate(), cmd.getProductChargeModel(), cmd.getDuration()));
            vo.setDuration(cmd.getDuration());
            vo.setProductChargeModel(cmd.getProductChargeModel());
            dbf.updateAndRefresh(vo);
            //付款成功,记录生效订单
            tunnelBillingBase.saveResourceOrderEffective(cmd.getOrderUuid(), vo.getUuid(), vo.getClass().getSimpleName());
        }
    }

    private void buyTunnel(OrderCallbackCmd cmd, CreateTunnelCallBack message) {
        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();

        if (message.isNewBuyInterfaceA()) {
            InterfaceVO vo = dbf.findByUuid(message.getInterfaceAUuid(), InterfaceVO.class);
            vo.setAccountUuid(vo.getOwnerAccountUuid());
            vo.setState(InterfaceState.Paid);
            vo.setExpireDate(tunnelBillingBase.getExpireDate(dbf.getCurrentSqlTime(), cmd.getProductChargeModel(), cmd.getDuration()));
            dbf.updateAndRefresh(vo);
        }
        if (message.isNewBuyInterfaceZ()) {
            InterfaceVO vo = dbf.findByUuid(message.getInterfaceZUuid(), InterfaceVO.class);
            vo.setAccountUuid(vo.getOwnerAccountUuid());
            vo.setState(InterfaceState.Paid);
            vo.setExpireDate(tunnelBillingBase.getExpireDate(dbf.getCurrentSqlTime(), cmd.getProductChargeModel(), cmd.getDuration()));
            dbf.updateAndRefresh(vo);
        }

        TunnelVO vo = dbf.findByUuid(cmd.getPorductUuid(), TunnelVO.class);
        vo.setAccountUuid(vo.getOwnerAccountUuid());
        vo.setState(TunnelState.Deploying);
        vo.setStatus(TunnelStatus.Connecting);
        dbf.updateAndRefresh(vo);
        //付款成功,记录生效订单
        tunnelBillingBase.saveResourceOrderEffective(cmd.getOrderUuid(), vo.getUuid(), vo.getClass().getSimpleName());

        //创建任务
        TaskResourceVO taskResourceVO = new TunnelBase().newTaskResourceVO(vo, TaskType.Create);

        CreateTunnelMsg createTunnelMsg = new CreateTunnelMsg();
        createTunnelMsg.setTunnelUuid(vo.getUuid());
        createTunnelMsg.setTaskUuid(taskResourceVO.getUuid());
        bus.makeLocalServiceId(createTunnelMsg, TunnelConstant.SERVICE_ID);
        bus.send(createTunnelMsg, new CloudBusCallBack(null) {
            @Override
            public void run(MessageReply reply) {
                if (reply.isSuccess()) {
                    logger.info("billing通知回调并请求下发成功");
                } else {
                    logger.info("billing通知回调并请求下发失败");
                }
            }
        });

    }

    private void buyInterface(OrderCallbackCmd cmd) {
        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();

        InterfaceVO vo = dbf.findByUuid(cmd.getPorductUuid(), InterfaceVO.class);
        vo.setAccountUuid(vo.getOwnerAccountUuid());
        vo.setState(InterfaceState.Paid);
        vo.setExpireDate(tunnelBillingBase.getExpireDate(dbf.getCurrentSqlTime(), cmd.getProductChargeModel(), cmd.getDuration()));
        dbf.updateAndRefresh(vo);
        //付款成功,记录生效订单
        tunnelBillingBase.saveResourceOrderEffective(cmd.getOrderUuid(), vo.getUuid(), vo.getClass().getSimpleName());

    }

    private void unsubcribeInterface(OrderCallbackCmd cmd) {
        InterfaceVO vo = dbf.findByUuid(cmd.getPorductUuid(), InterfaceVO.class);
        new TunnelBillingBase().saveResourceOrderEffective(cmd.getOrderUuid(), vo.getUuid(), vo.getClass().getSimpleName());
        dbf.remove(vo);

    }

    private void unsubcribeTunnel(OrderCallbackCmd cmd, DeleteTunnelCallBack message) {
        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();
        TunnelBase tunnelBase = new TunnelBase();
        TunnelVO vo = dbf.findByUuid(cmd.getPorductUuid(), TunnelVO.class);

        //退订成功，记录生效订单
        tunnelBillingBase.saveResourceOrderEffective(cmd.getOrderUuid(), vo.getUuid(), vo.getClass().getSimpleName());
        if (message.getDescription().equals("forciblydelete")) {
            tunnelBase.deleteTunnel(vo);
        } else if (message.getDescription().equals("delete") && vo.getState() == TunnelState.Enabled) {
            vo.setAccountUuid(null);
            dbf.updateAndRefresh(vo);

            //创建任务
            TaskResourceVO taskResourceVO = new TunnelBase().newTaskResourceVO(vo, TaskType.Delete);

            DeleteTunnelMsg deleteTunnelMsg = new DeleteTunnelMsg();
            deleteTunnelMsg.setTunnelUuid(vo.getUuid());
            deleteTunnelMsg.setTaskUuid(taskResourceVO.getUuid());
            bus.makeLocalServiceId(deleteTunnelMsg, TunnelConstant.SERVICE_ID);
            bus.send(deleteTunnelMsg, new CloudBusCallBack(null) {
                @Override
                public void run(MessageReply reply) {
                    if (reply.isSuccess()) {
                        logger.info("billing通知回调并请求下发成功");
                    } else {
                        logger.info("billing通知回调并请求下发失败");
                    }
                }
            });
        } else if (message.getDescription().equals("delete") && vo.getState() == TunnelState.Disabled) {
            tunnelBase.deleteTunnel(vo);
        } else if (message.getDescription().equals("unsupport")) {
            vo.setState(TunnelState.Unsupport);
            vo.setExpireDate(dbf.getCurrentSqlTime());
            dbf.updateAndRefresh(vo);
        }

    }

    private void modifyBandwidth(OrderCallbackCmd cmd, UpdateTunnelBandwidthCallBack message) {
        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();

        TunnelVO vo = dbf.findByUuid(cmd.getPorductUuid(), TunnelVO.class);
        vo.setBandwidth(message.getBandwidth());
        dbf.updateAndRefresh(vo);
        //付款成功,记录生效订单
        tunnelBillingBase.saveResourceOrderEffective(cmd.getOrderUuid(), vo.getUuid(), vo.getClass().getSimpleName());

        //创建任务
        TaskResourceVO taskResourceVO = new TunnelBase().newTaskResourceVO(vo, TaskType.ModifyBandwidth);

        ModifyTunnelBandwidthMsg modifyTunnelBandwidthMsg = new ModifyTunnelBandwidthMsg();
        modifyTunnelBandwidthMsg.setTunnelUuid(vo.getUuid());
        modifyTunnelBandwidthMsg.setTaskUuid(taskResourceVO.getUuid());
        bus.makeLocalServiceId(modifyTunnelBandwidthMsg, TunnelConstant.SERVICE_ID);
        bus.send(modifyTunnelBandwidthMsg);
    }

    @Override
    public boolean start() {
        startCleanExpiredProduct();

        restf.registerSyncHttpCallHandler("billing", OrderCallbackCmd.class,
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

    /**************************************** The following clean the expired Products **************************************************/

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
            TunnelBase tunnelBase = new TunnelBase();
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
                        TaskResourceVO task = tunnelBase.newTaskResourceVO(vo, TaskType.Delete);
                        DeleteTunnelMsg msg = new DeleteTunnelMsg();
                        msg.setTaskUuid(task.getUuid());
                        msg.setTunnelUuid(vo.getUuid());
                        bus.makeLocalServiceId(msg, TunnelConstant.SERVICE_ID);
                        msgs.add(msg);
                    }
                    if (vo.getExpireDate().before(closeTime)) {
                        if (vo.getState() == TunnelState.Unpaid) {
                            new TunnelBase().deleteTunnel(vo);
                        } else {
                            TaskResourceVO task = tunnelBase.newTaskResourceVO(vo, TaskType.Delete);
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
        TunnelValidateBase tunnelValidateBase = new TunnelValidateBase();
        if (msg instanceof APICreateInterfaceMsg) {
            tunnelValidateBase.validate((APICreateInterfaceMsg) msg);
        } else if (msg instanceof APICreateInterfaceManualMsg) {
            tunnelValidateBase.validate((APICreateInterfaceManualMsg) msg);
        } else if (msg instanceof APIUpdateInterfaceMsg) {
            tunnelValidateBase.validate((APIUpdateInterfaceMsg) msg);
        } else if (msg instanceof APIRenewInterfaceMsg) {
            tunnelValidateBase.validate((APIRenewInterfaceMsg) msg);
        } else if (msg instanceof APIRenewAutoInterfaceMsg) {
            tunnelValidateBase.validate((APIRenewAutoInterfaceMsg) msg);
        } else if (msg instanceof APISLAInterfaceMsg) {
            tunnelValidateBase.validate((APISLAInterfaceMsg) msg);
        } else if (msg instanceof APIDeleteInterfaceMsg) {
            tunnelValidateBase.validate((APIDeleteInterfaceMsg) msg);
        } else if (msg instanceof APICreateTunnelMsg) {
            tunnelValidateBase.validate((APICreateTunnelMsg) msg);
        } else if (msg instanceof APICreateTunnelManualMsg) {
            tunnelValidateBase.validate((APICreateTunnelManualMsg) msg);
        } else if (msg instanceof APIUpdateTunnelMsg) {
            tunnelValidateBase.validate((APIUpdateTunnelMsg) msg);
        } else if (msg instanceof APIUpdateTunnelBandwidthMsg) {
            tunnelValidateBase.validate((APIUpdateTunnelBandwidthMsg) msg);
        } else if (msg instanceof APIRenewTunnelMsg) {
            tunnelValidateBase.validate((APIRenewTunnelMsg) msg);
        } else if (msg instanceof APIRenewAutoTunnelMsg) {
            tunnelValidateBase.validate((APIRenewAutoTunnelMsg) msg);
        } else if (msg instanceof APISLATunnelMsg) {
            tunnelValidateBase.validate((APISLATunnelMsg) msg);
        } else if (msg instanceof APIDeleteTunnelMsg) {
            tunnelValidateBase.validate((APIDeleteTunnelMsg) msg);
        } else if (msg instanceof APIDeleteForciblyTunnelMsg) {
            tunnelValidateBase.validate((APIDeleteForciblyTunnelMsg) msg);
        } else if (msg instanceof APIUpdateTunnelStateMsg) {
            tunnelValidateBase.validate((APIUpdateTunnelStateMsg) msg);
        } else if (msg instanceof APICreateQinqMsg) {
            tunnelValidateBase.validate((APICreateQinqMsg) msg);
        } else if (msg instanceof APIDeleteQinqMsg) {
            tunnelValidateBase.validate((APIDeleteQinqMsg) msg);
        } else if (msg instanceof APIUpdateTunnelVlanMsg) {
            tunnelValidateBase.validate((APIUpdateTunnelVlanMsg) msg);
        } else if (msg instanceof APIUpdateForciblyTunnelVlanMsg) {
            tunnelValidateBase.validate((APIUpdateForciblyTunnelVlanMsg) msg);
        } else if (msg instanceof APIUpdateInterfacePortMsg) {
            tunnelValidateBase.validate((APIUpdateInterfacePortMsg) msg);
        }
        return msg;
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

    public void preDelete(){};

    public void beforeDelete(){};

    public void afterDelete(){};
}
