package com.syscxp.tunnel.tunnel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.CloudBusCallBack;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.componentloader.PluginRegistry;
import com.syscxp.core.config.GlobalConfig;
import com.syscxp.core.config.GlobalConfigUpdateExtensionPoint;
import com.syscxp.core.db.*;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.identity.InnerMessageHelper;
import com.syscxp.core.job.JobQueueEntryVO;
import com.syscxp.core.job.JobQueueEntryVO_;
import com.syscxp.core.job.JobQueueFacade;
import com.syscxp.core.keyvalue.Op;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.core.thread.PeriodicTask;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.core.workflow.FlowChainBuilder;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.billing.*;
import com.syscxp.header.configuration.BandwidthOfferingVO;
import com.syscxp.header.core.Completion;
import com.syscxp.header.configuration.MotifyType;
import com.syscxp.header.configuration.ResourceMotifyRecordVO;
import com.syscxp.header.configuration.ResourceMotifyRecordVO_;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.quota.Quota;
import com.syscxp.header.quota.QuotaConstant;
import com.syscxp.header.quota.ReportQuotaExtensionPoint;
import com.syscxp.header.message.*;
import com.syscxp.header.rest.RestAPIResponse;
import com.syscxp.header.tunnel.billingCallBack.*;
import com.syscxp.header.core.workflow.*;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.falconapi.FalconApiCommands;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.tunnel.*;
import com.syscxp.header.tunnel.edgeLine.EdgeLineVO;
import com.syscxp.header.tunnel.edgeLine.EdgeLineVO_;
import com.syscxp.header.tunnel.endpoint.*;
import com.syscxp.header.tunnel.node.NodeVO;
import com.syscxp.header.tunnel.switchs.*;
import com.syscxp.header.tunnel.tunnel.*;
import com.syscxp.header.vpn.vpn.APICheckVpnForTunnelMsg;
import com.syscxp.header.vpn.vpn.APICheckVpnForTunnelReply;
import com.syscxp.header.vpn.vpn.APIDestroyVpnMsg;
import com.syscxp.tunnel.identity.TunnelGlobalConfig;
import com.syscxp.tunnel.quota.InterfaceQuotaOperator;
import com.syscxp.tunnel.quota.TunnelQuotaOperator;
import com.syscxp.tunnel.tunnel.job.*;
import com.syscxp.utils.CollectionDSL;
import com.syscxp.utils.URLBuilder;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.math.BigDecimal;
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
    @Autowired
    private JobQueueFacade jobf;

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
        } else if (msg instanceof APIOpenTunnelMsg) {
            handle((APIOpenTunnelMsg) msg);
        } else if (msg instanceof APIUnsupportTunnelMsg) {
            handle((APIUnsupportTunnelMsg) msg);
        } else if (msg instanceof APIEnableTunnelMsg) {
            handle((APIEnableTunnelMsg) msg);
        } else if (msg instanceof APIDisableTunnelMsg) {
            handle((APIDisableTunnelMsg) msg);
        } else if (msg instanceof APIUpdateQinqMsg) {
            handle((APIUpdateQinqMsg) msg);
        } else if (msg instanceof APIQueryTunnelDetailForAlarmMsg) {
            handle((APIQueryTunnelDetailForAlarmMsg) msg);
        } else if (msg instanceof APIListSwitchPortByTypeMsg) {
            handle((APIListSwitchPortByTypeMsg) msg);
        } else if (msg instanceof APIUpdateTunnelVlanMsg) {
            handle((APIUpdateTunnelVlanMsg) msg);
        } else if (msg instanceof APIListCrossTunnelMsg) {
            handle((APIListCrossTunnelMsg) msg);
        } else if (msg instanceof APIListInnerEndpointMsg) {
            handle((APIListInnerEndpointMsg) msg);
        } else if (msg instanceof APIListTraceRouteMsg) {
            handle((APIListTraceRouteMsg) msg);
        } else if (msg instanceof APIGetRenewInterfacePriceMsg) {
            handle((APIGetRenewInterfacePriceMsg) msg);
        } else if (msg instanceof APIGetRenewTunnelPriceMsg) {
            handle((APIGetRenewTunnelPriceMsg) msg);
        } else if (msg instanceof APIGetModifyBandwidthNumMsg) {
            handle((APIGetModifyBandwidthNumMsg) msg);
        } else if (msg instanceof APIRunDataForTunnelTypeMsg) {
            handle((APIRunDataForTunnelTypeMsg) msg);
        } else if (msg instanceof APIRunDataForTunnelZKMsg) {
            handle((APIRunDataForTunnelZKMsg) msg);
        } else if (msg instanceof APIUpdateResourceUuidMsg) {
            handle((APIUpdateResourceUuidMsg) msg);
        }  else {
            bus.dealWithUnknownMessage(msg);
        }
    }


    /*****************************  Interface The following processing   *************************************/

    /**
     * 自动创建物理接口
     * 仅独享端口，无共享端口
     **/
    private void handle(APICreateInterfaceMsg msg) {
        APICreateInterfaceEvent evt = new APICreateInterfaceEvent(msg.getId());

        String interfaceUuid = doCreateInterfaceVO(msg);
        InterfaceVO vo = dbf.findByUuid(interfaceUuid,InterfaceVO.class);

        if(msg.getPortOfferingUuid().equals("SHARE")){
            evt.setInventory(InterfaceInventory.valueOf(vo));
            bus.publish(evt);
        }else{
            afterCreateInterface(vo, msg.getSession().getAccountUuid(), msg.getPortOfferingUuid(), new ReturnValueCompletion<InterfaceInventory>(null) {
                @Override
                public void success(InterfaceInventory inv) {
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

    /**
     * 手动创建物理接口
     * 独享端口和共享端口
     **/
    private void handle(APICreateInterfaceManualMsg msg) {
        APICreateInterfaceManualEvent evt = new APICreateInterfaceManualEvent(msg.getId());

        String portType = Q.New(SwitchPortVO.class)
                .eq(SwitchPortVO_.uuid, msg.getSwitchPortUuid())
                .select(SwitchPortVO_.portType).find();

        String interfaceUuid = doCreateInterfaceVOManual(msg);
        InterfaceVO vo = dbf.findByUuid(interfaceUuid,InterfaceVO.class);

        if(portType.equals("SHARE")){
            evt.setInventory(InterfaceInventory.valueOf(vo));
            bus.publish(evt);
        }else{
            afterCreateInterface(vo, msg.getSession().getAccountUuid(), portType, new ReturnValueCompletion<InterfaceInventory>(null) {
                @Override
                public void success(InterfaceInventory inv) {
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

    private String doCreateInterfaceVO(APICreateInterfaceMsg msg){

        //分配端口
        TunnelStrategy ts = new TunnelStrategy();
        String switchPortUuid = ts.getSwitchPortByStrategy(msg.getAccountUuid(), msg.getEndpointUuid(), msg.getPortOfferingUuid());
        if (switchPortUuid == null) {
            throw new ApiMessageInterceptionException(argerr("该连接点下无可用的端口"));
        }

        //创建接口
        InterfaceVO vo = new InterfaceVO();
        vo.setUuid(Platform.getUuid());
        vo.setOwnerAccountUuid(msg.getAccountUuid());
        vo.setName(msg.getName());
        vo.setEndpointUuid(msg.getEndpointUuid());
        vo.setSwitchPortUuid(switchPortUuid);
        vo.setType(NetworkType.TRUNK);
        vo.setDescription(msg.getDescription());
        vo.setMaxModifies(CoreGlobalProperty.INTERFACE_MAX_MOTIFIES);
        vo.setExpireDate(null);

        if(msg.getPortOfferingUuid().equals("SHARE")){
            vo.setAccountUuid(msg.getAccountUuid());
            vo.setDuration(1);
            vo.setProductChargeModel(ProductChargeModel.BY_MONTH);
            vo.setState(InterfaceState.Up);
        }else{
            vo.setAccountUuid(null);
            vo.setDuration(msg.getDuration());
            vo.setProductChargeModel(msg.getProductChargeModel());
            vo.setState(InterfaceState.Unpaid);
        }
        vo = dbf.persistAndRefresh(vo);

        return vo.getUuid();
    }

    private String doCreateInterfaceVOManual(APICreateInterfaceManualMsg msg){

        String portType = Q.New(SwitchPortVO.class)
                .eq(SwitchPortVO_.uuid, msg.getSwitchPortUuid())
                .select(SwitchPortVO_.portType).find();

        InterfaceVO vo = new InterfaceVO();
        vo.setUuid(Platform.getUuid());
        vo.setOwnerAccountUuid(msg.getAccountUuid());
        vo.setName(msg.getName());
        vo.setEndpointUuid(msg.getEndpointUuid());
        vo.setSwitchPortUuid(msg.getSwitchPortUuid());
        vo.setDescription(msg.getDescription());
        vo.setExpireDate(null);
        vo.setMaxModifies(CoreGlobalProperty.INTERFACE_MAX_MOTIFIES);

        if(portType.equals("SHARE")){
            vo.setAccountUuid(msg.getAccountUuid());
            vo.setType(NetworkType.TRUNK);
            vo.setDuration(1);
            vo.setProductChargeModel(ProductChargeModel.BY_MONTH);
            vo.setState(InterfaceState.Up);
        }else{
            vo.setAccountUuid(null);
            vo.setType(msg.getNetworkType());
            vo.setDuration(msg.getDuration());
            vo.setProductChargeModel(msg.getProductChargeModel());
            vo.setState(InterfaceState.Unpaid);
        }

        vo = dbf.persistAndRefresh(vo);

        return vo.getUuid();
    }

    private void afterCreateInterface(InterfaceVO vo, String opAccountUuid, String portOfferingUuid, ReturnValueCompletion<InterfaceInventory> completion){
        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();

        //订单信息
        APICreateBuyOrderMsg orderMsg = new APICreateBuyOrderMsg();
        ProductInfoForOrder productInfoForOrder = tunnelBillingBase.createBuyOrderForInterface(vo, portOfferingUuid, new CreateInterfaceCallBack());
        productInfoForOrder.setOpAccountUuid(opAccountUuid);
        productInfoForOrder.setNotifyUrl(restf.getSendCommandUrl());
        orderMsg.setProducts(CollectionDSL.list(productInfoForOrder));
        //支付
        List<OrderInventory> inventories = tunnelBillingBase.createBuyOrder(orderMsg);

        if (!inventories.isEmpty()) {       //付款成功
            //记录生效订单
            tunnelBillingBase.saveResourceOrderEffective(inventories.get(0).getUuid(), vo.getUuid(), vo.getClass().getSimpleName());

            vo.setAccountUuid(vo.getOwnerAccountUuid());
            vo.setState(InterfaceState.Down);

            vo = dbf.updateAndRefresh(vo);

            completion.success(InterfaceInventory.valueOf(vo));
        }else{                              //付款失败
            vo.setExpireDate(dbf.getCurrentSqlTime());
            dbf.updateAndRefresh(vo);

            completion.fail(errf.stringToOperationError("付款失败"));
        }
    }


    /**
     * 修改物理接口名称和描述
     **/
    private void handle(APIUpdateInterfaceMsg msg) {
        InterfaceVO vo = dbf.findByUuid(msg.getUuid(), InterfaceVO.class);
        boolean update = false;
        boolean nameChange = false;
        if (msg.getName() != null && !msg.getName().equals(vo.getName())) {
            vo.setName(msg.getName());
            update = true;
            nameChange = true;
        }
        if (msg.getDescription() != null) {
            vo.setDescription(msg.getDescription());
            update = true;
        }

        if (update) {
            vo = dbf.updateAndRefresh(vo);
            if (nameChange) {
                RenameBillingProductNameJob.executeJob(jobf, vo.getUuid(), vo.getName());
                SimpleQuery<EdgeLineVO> q = dbf.createQuery(EdgeLineVO.class);
                q.add(EdgeLineVO_.interfaceUuid, SimpleQuery.Op.EQ, vo.getUuid());

                EdgeLineVO eg = q.find();
                if (eg != null){
                    RenameBillingProductNameJob.executeJob(jobf, eg.getUuid(), "最后一公里-" + vo.getName());
                }
            }
        }

        APIUpdateInterfaceEvent evt = new APIUpdateInterfaceEvent(msg.getId());
        evt.setInventory(InterfaceInventory.valueOf(vo));
        bus.publish(evt);
    }

    /**
     * 物理接口续费
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
        orderMsg.setAutoRenew(true);

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
     **/
    private void handle(APISLAInterfaceMsg msg) {
        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();
        APISLAInterfaceReply reply = new APISLAInterfaceReply();

        InterfaceVO vo = dbf.findByUuid(msg.getUuid(), InterfaceVO.class);
        Timestamp newTime = vo.getExpireDate();
        APICreateSLACompensationOrderMsg orderMsg = new APICreateSLACompensationOrderMsg(tunnelBillingBase.getOrderMsgForInterface(vo, new SlaInterfaceCallBack()));
        orderMsg.setSlaUuid(msg.getSlaUuid());
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
     **/
    private void handle(APIDeleteInterfaceMsg msg) {
        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();
        APIDeleteInterfaceEvent evt = new APIDeleteInterfaceEvent(msg.getId());

        InterfaceVO vo = dbf.findByUuid(msg.getUuid(), InterfaceVO.class);

        String portType = Q.New(SwitchPortVO.class)
                .eq(SwitchPortVO_.uuid, vo.getSwitchPortUuid())
                .select(SwitchPortVO_.portType).find();

        if(portType.equals("SHARE")){
            dbf.remove(vo);
            bus.publish(evt);
        }else{
            if(vo.getExpireDate() != null && !vo.getExpireDate().after(Timestamp.valueOf(LocalDateTime.now()))){
                dbf.remove(vo);

                //删除续费表
                logger.info("删除接口成功，并创建任务：DeleteRenewVOAfterDeleteResourceJob");
                DeleteRenewVOAfterDeleteResourceJob job = new DeleteRenewVOAfterDeleteResourceJob();
                job.setAccountUuid(vo.getOwnerAccountUuid());
                job.setResourceType(vo.getClass().getSimpleName());
                job.setResourceUuid(vo.getUuid());
                jobf.execute("删除物理接口-删除续费表", Platform.getManagementServerId(), job);

                bus.publish(evt);
            }else{
                //调用退订
                APICreateUnsubcribeOrderMsg orderMsg = new APICreateUnsubcribeOrderMsg(tunnelBillingBase.getOrderMsgForInterface(vo, new UnsubcribeInterfaceCallBack()));
                orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
                orderMsg.setStartTime(dbf.getCurrentSqlTime());
                if (vo.getExpireDate() == null) {
                    orderMsg.setExpiredTime(dbf.getCurrentSqlTime());
                    orderMsg.setCreateFailure(true);
                } else {
                    orderMsg.setExpiredTime(vo.getExpireDate());
                }

                OrderInventory orderInventory = tunnelBillingBase.createOrder(orderMsg);
                if (orderInventory != null) {
                    vo.setExpireDate(dbf.getCurrentSqlTime());
                    dbf.updateAndRefresh(vo);
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
        }
    }

    /**
     * 更改物理接口的端口
     **/
    private void handle(APIUpdateInterfacePortMsg msg) {
        TunnelBase tunnelBase = new TunnelBase();
        TunnelJobAndTaskBase taskBase = new TunnelJobAndTaskBase();
        InterfaceVO iface = dbf.findByUuid(msg.getUuid(), InterfaceVO.class);
        logger.info(String.format("before update InterfaceVO: [%s]", iface));

        TunnelSwitchPortVO tsPort = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.interfaceUuid, msg.getUuid())
                .find();
        boolean isUsed = tsPort != null;

        TunnelSwitchPortVO tunnelSwitchPortB;
        TunnelSwitchPortVO remoteTunnelSwitchPort = null;
        PhysicalSwitchVO physicalSwitch = null;

        if(isUsed){
            TunnelVO vo = Q.New(TunnelVO.class)
                    .eq(TunnelVO_.uuid, tsPort.getTunnelUuid())
                    .find();

            //判断该专线是否还有未完成任务
            if(Q.New(JobQueueEntryVO.class).eq(JobQueueEntryVO_.resourceUuid, tsPort.getTunnelUuid()).eq(JobQueueEntryVO_.restartable, true).isExists()){
                throw new ApiMessageInterceptionException(argerr("该专线有未完成任务，请稍后再操作！"));
            }

            //判断该专线是否中止
            if(vo.getState() == TunnelState.Enabled){
                throw new ApiMessageInterceptionException(argerr("该接口已有运行的专线[uuid:%s]，请先断开连接！",vo.getUuid()));
            }

            tunnelSwitchPortB = Q.New(TunnelSwitchPortVO.class)
                    .eq(TunnelSwitchPortVO_.tunnelUuid, vo.getUuid())
                    .eq(TunnelSwitchPortVO_.sortTag, "B")
                    .find();

            //修改后的所属物理交换机
            physicalSwitch = tunnelBase.getPhysicalSwitchBySwitchPortUuid(msg.getSwitchPortUuid());

            //找到对端TunnelSwitchPort
            if(tsPort.getSortTag().equals("A")){
                if(tunnelSwitchPortB != null){
                    remoteTunnelSwitchPort = tunnelSwitchPortB;

                }else{
                    remoteTunnelSwitchPort = Q.New(TunnelSwitchPortVO.class)
                            .eq(TunnelSwitchPortVO_.tunnelUuid, vo.getUuid())
                            .eq(TunnelSwitchPortVO_.sortTag, "Z")
                            .find();
                }
            }else{
                if(tunnelSwitchPortB != null){
                    remoteTunnelSwitchPort = Q.New(TunnelSwitchPortVO.class)
                            .eq(TunnelSwitchPortVO_.tunnelUuid, vo.getUuid())
                            .eq(TunnelSwitchPortVO_.sortTag, "C")
                            .find();
                }else{
                    remoteTunnelSwitchPort = Q.New(TunnelSwitchPortVO.class)
                            .eq(TunnelSwitchPortVO_.tunnelUuid, vo.getUuid())
                            .eq(TunnelSwitchPortVO_.sortTag, "A")
                            .find();
                }
            }

            //端口改变，验证VLAN
            if (!Objects.equals(msg.getSwitchPortUuid(), iface.getSwitchPortUuid())){
                String switchUuid = Q.New(SwitchPortVO.class).eq(SwitchPortVO_.uuid, msg.getSwitchPortUuid()).select(SwitchPortVO_.switchUuid).findValue();

                SwitchPortVO peerSwitchPort = dbf.findByUuid(remoteTunnelSwitchPort.getSwitchPortUuid(), SwitchPortVO.class);
                new TunnelValidateBase().validateVlanForUpdateVlanOrInterface(switchUuid, peerSwitchPort.getSwitchUuid(), tunnelBase.findSwitchByInterface(msg.getUuid()), tsPort.getVlan(), tsPort.getVlan());
            }

        }

        final TunnelSwitchPortVO remoteTunnelSwitchPortVO = remoteTunnelSwitchPort;
        final PhysicalSwitchVO physicalSwitchVO = physicalSwitch;

        Map<String, Object> rollback = new HashMap<>();

        APIUpdateInterfacePortEvent evt = new APIUpdateInterfacePortEvent(msg.getId());
        FlowChain updateInterface = FlowChainBuilder.newSimpleFlowChain();
        updateInterface.setName(String.format("update-interfacePort-%s", msg.getUuid()));
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

                    UpdateQuery.New(InterfaceVO.class)
                            .set(InterfaceVO_.type, msg.getNetworkType())
                            .eq(InterfaceVO_.uuid, iface.getUuid())
                            .update();
                }
                logger.info(String.format("after update InterfaceVO[uuid: %s]", iface.getUuid()));
                trigger.next();
            }

            @Override
            public void rollback(FlowRollback trigger, Map data) {
                logger.info(String.format("rollback to update InterfaceVO[uuid: %s]", iface.getUuid()));
                dbf.updateAndRefresh(iface);

                trigger.rollback();
            }
        }).then(new Flow() {
            String __name__ = "update-tunnelSwitchPort-for-DB";

            @Override
            public void run(FlowTrigger trigger, Map data) {
                if (isUsed) {
                    if(msg.getNetworkType().equals(iface.getType())){
                        UpdateQuery.New(TunnelSwitchPortVO.class)
                                .set(TunnelSwitchPortVO_.switchPortUuid, msg.getSwitchPortUuid())
                                .set(TunnelSwitchPortVO_.physicalSwitchUuid, physicalSwitchVO.getUuid())
                                .set(TunnelSwitchPortVO_.ownerMplsSwitchUuid, tunnelBase.getUplinkMplsSwitchByPhysicalSwitch(physicalSwitchVO).getUuid())
                                .eq(TunnelSwitchPortVO_.interfaceUuid, msg.getUuid())
                                .update();
                    }else{
                        UpdateQuery.New(TunnelSwitchPortVO.class)
                                .set(TunnelSwitchPortVO_.switchPortUuid, msg.getSwitchPortUuid())
                                .set(TunnelSwitchPortVO_.type, msg.getNetworkType())
                                .set(TunnelSwitchPortVO_.physicalSwitchUuid, physicalSwitchVO.getUuid())
                                .set(TunnelSwitchPortVO_.ownerMplsSwitchUuid, tunnelBase.getUplinkMplsSwitchByPhysicalSwitch(physicalSwitchVO).getUuid())
                                .eq(TunnelSwitchPortVO_.interfaceUuid, msg.getUuid())
                                .update();
                    }

                    UpdateQuery.New(TunnelSwitchPortVO.class)
                            .set(TunnelSwitchPortVO_.peerMplsSwitchUuid, tunnelBase.getUplinkMplsSwitchByPhysicalSwitch(physicalSwitchVO).getUuid())
                            .eq(TunnelSwitchPortVO_.uuid, remoteTunnelSwitchPortVO.getUuid())
                            .update();
                    logger.info(String.format("after update TunnelSwitchPortVO[uuid: %s]", tsPort.getUuid()));
                }
                trigger.next();
            }

            @Override
            public void rollback(FlowRollback trigger, Map data) {
                if (isUsed) {
                    dbf.updateAndRefresh(tsPort);
                    dbf.updateAndRefresh(remoteTunnelSwitchPortVO);
                    logger.info(String.format("rollback to update TunnelSwitchPortVO[uuid: %s]", tsPort.getUuid()));
                }
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
     **/
    private void handle(APICreateTunnelMsg msg) {

        //创建Tunnel,Interface,TunnelSwitchPort
        String tunnelUuid = doCreateTunnelVO(msg);

        TunnelVO vo = dbf.findByUuid(tunnelUuid, TunnelVO.class);

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

        BandwidthOfferingVO bandwidthOfferingVO = dbf.findByUuid(msg.getBandwidthOfferingUuid(), BandwidthOfferingVO.class);
        EndpointVO evoA = dbf.findByUuid(msg.getEndpointAUuid(), EndpointVO.class);
        EndpointVO evoZ = dbf.findByUuid(msg.getEndpointZUuid(), EndpointVO.class);
        NodeVO nvoA = dbf.findByUuid(evoA.getNodeUuid(), NodeVO.class);
        NodeVO nvoZ = dbf.findByUuid(evoZ.getNodeUuid(), NodeVO.class);

        boolean crossTunnel = false;        //是否存在共点专线
        Integer vsi;                        //VSI
        Integer crossVlan = null;           //共点VLAN

        TunnelType tunnelType = tunnelBase.getTunnelType(nvoA, nvoZ, msg.getInnerConnectedEndpointUuid());

        //首先判断是否存在共点专线，若存在，获取VSI和VLAN
        if (msg.getCrossTunnelUuid() != null) {
            crossTunnel = true;
            vsi = Q.New(TunnelVO.class)
                    .eq(TunnelVO_.uuid, msg.getCrossTunnelUuid())
                    .select(TunnelVO_.vsi)
                    .findValue();
            crossVlan = Q.New(TunnelSwitchPortVO.class)
                    .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getCrossTunnelUuid())
                    .eq(TunnelSwitchPortVO_.interfaceUuid, msg.getCrossInterfaceUuid())
                    .select(TunnelSwitchPortVO_.vlan)
                    .findValue();
        } else {
            vsi = tunnelBase.getVsiAuto();
        }

        //若新购，创建物理接口
        boolean isIfaceANew = false;
        if(msg.getInterfaceAUuid() == null){
            isIfaceANew = true;
        }
        boolean isIfaceZNew = false;
        if(msg.getInterfaceZUuid() == null){
            isIfaceZNew = true;
        }
        InterfaceVO interfaceVOA;
        InterfaceVO interfaceVOZ;
        if (!isIfaceANew){
            interfaceVOA = dbf.findByUuid(msg.getInterfaceAUuid(), InterfaceVO.class);
        } else {
            interfaceVOA = tunnelBase.createInterfaceByTunnel(msg.getEndpointAUuid(), msg);
        }
        if (!isIfaceZNew){
            interfaceVOZ = dbf.findByUuid(msg.getInterfaceZUuid(), InterfaceVO.class);
        } else {
            interfaceVOZ = tunnelBase.createInterfaceByTunnel(msg.getEndpointZUuid(), msg);
        }

        if (msg.getCrossTunnelUuid() != null) {
            if (msg.getCrossInterfaceUuid().equals(msg.getInterfaceAUuid())){
                validateDuplicateVsiVlan(interfaceVOZ.getSwitchPortUuid(), vsi);
            }else{
                validateDuplicateVsiVlan(interfaceVOA.getSwitchPortUuid(), vsi);
            }
        }

        SwitchPortVO switchPortVOA = dbf.findByUuid(interfaceVOA.getSwitchPortUuid(),SwitchPortVO.class);
        SwitchPortVO switchPortVOZ = dbf.findByUuid(interfaceVOZ.getSwitchPortUuid(),SwitchPortVO.class);

        //创建专线
        TunnelVO vo = new TunnelVO();
        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(null);
        vo.setOwnerAccountUuid(msg.getAccountUuid());
        vo.setVsi(vsi);
        vo.setMonitorCidr(null);
        vo.setName(msg.getName());
        vo.setBandwidthOffering(msg.getBandwidthOfferingUuid());
        vo.setBandwidth(bandwidthOfferingVO.getBandwidth());
        vo.setDuration(msg.getDuration());
        vo.setState(TunnelState.Unpaid);
        vo.setStatus(TunnelStatus.Disconnected);
        vo.setType(tunnelType);
        vo.setInnerEndpointUuid(msg.getInnerConnectedEndpointUuid());
        vo.setExpireDate(null);
        vo.setDescription(msg.getDescription());
        vo.setProductChargeModel(msg.getProductChargeModel());
        vo.setMonitorState(TunnelMonitorState.Disabled);
        vo.setMaxModifies(CoreGlobalProperty.TUNNEL_MAX_MOTIFIES);
        vo.setDistance(Distance.getDistance(nvoA.getLongitude(), nvoA.getLatitude(), nvoZ.getLongitude(), nvoZ.getLatitude()));
        dbf.getEntityManager().persist(vo);

        //创建TunnelSwitchPort
        Integer vlanA;
        Integer vlanZ;
        String peerSwitchUuidA;
        String peerSwitchUuidZ;
        if(tunnelType == TunnelType.CHINA2ABROAD){

            SwitchVO innerSwitch = Q.New(SwitchVO.class)
                    .eq(SwitchVO_.endpointUuid, msg.getInnerConnectedEndpointUuid())
                    .eq(SwitchVO_.type, SwitchType.INNER)
                    .find();

            SwitchVO outerSwitch = Q.New(SwitchVO.class)
                    .eq(SwitchVO_.endpointUuid, msg.getInnerConnectedEndpointUuid())
                    .eq(SwitchVO_.type, SwitchType.OUTER)
                    .find();
            if(nvoA.getCountry().equals("CHINA")){
                peerSwitchUuidA = innerSwitch.getUuid();
                peerSwitchUuidZ = outerSwitch.getUuid();
            }else{
                peerSwitchUuidA = outerSwitch.getUuid();
                peerSwitchUuidZ = innerSwitch.getUuid();
            }
        }else{
            peerSwitchUuidA = switchPortVOZ.getSwitchUuid();
            peerSwitchUuidZ = switchPortVOA.getSwitchUuid();
        }

        if(crossTunnel){    //共点专线
            if(tunnelBase.isSamePhysicalSwitchForTunnel(switchPortVOA,switchPortVOZ)){     //从同一个物理交换机进和出，不可能跨国
                vlanA = crossVlan;
                vlanZ = vlanA;
            }else{
                if (!isIfaceANew && msg.getInterfaceAUuid().equals(msg.getCrossInterfaceUuid())){   //A是共点
                    vlanA = crossVlan;

                    if(ts.vlanIsAvailable(switchPortVOZ.getSwitchUuid(), peerSwitchUuidZ, vlanA)){
                        vlanZ = vlanA;
                    }else{
                        vlanZ = ts.getVlanByStrategy(switchPortVOZ.getSwitchUuid(), peerSwitchUuidZ);
                        if (vlanZ == 0) {
                            throw new ApiMessageInterceptionException(argerr("该端口[%s]所属虚拟交换机下已无可使用的VLAN，请联系系统管理员",switchPortVOZ.getUuid()));
                        }
                    }

                }else{
                    vlanZ = crossVlan;

                    if(ts.vlanIsAvailable(switchPortVOA.getSwitchUuid(), peerSwitchUuidA, vlanZ)){
                        vlanA = vlanZ;
                    }else{
                        vlanA = ts.getVlanByStrategy(switchPortVOA.getSwitchUuid(), peerSwitchUuidA);
                        if (vlanA == 0) {
                            throw new ApiMessageInterceptionException(argerr("该端口[%s]所属虚拟交换机下已无可使用的VLAN，请联系系统管理员",switchPortVOA.getUuid()));
                        }
                    }

                }
            }
        }else{
            if(tunnelBase.isSamePhysicalSwitchForTunnel(switchPortVOA,switchPortVOZ)) {     //从同一个物理交换机进和出，不可能跨国
                vlanA = ts.getVlanByStrategy(switchPortVOA.getSwitchUuid(), peerSwitchUuidA);
                if (vlanA == 0) {
                    throw new ApiMessageInterceptionException(argerr("该端口[%s]所属虚拟交换机下已无可使用的VLAN，请联系系统管理员",switchPortVOA.getUuid()));
                }
                vlanZ = vlanA;
            }else{
                vlanA = ts.getVlanByStrategy(switchPortVOA.getSwitchUuid(), peerSwitchUuidA);
                if (vlanA == 0) {
                    throw new ApiMessageInterceptionException(argerr("该端口[%s]所属虚拟交换机下已无可使用的VLAN，请联系系统管理员",switchPortVOA.getUuid()));
                }

                if(ts.vlanIsAvailable(switchPortVOZ.getSwitchUuid(), peerSwitchUuidZ, vlanA)){
                    vlanZ = vlanA;
                }else{
                    vlanZ = ts.getVlanByStrategy(switchPortVOZ.getSwitchUuid(), peerSwitchUuidZ);
                    if (vlanZ == 0) {
                        throw new ApiMessageInterceptionException(argerr("该端口[%s]所属虚拟交换机下已无可使用的VLAN，请联系系统管理员",switchPortVOZ.getUuid()));
                    }
                }
            }
        }

        if(tunnelType == TunnelType.CHINA2ABROAD){     //跨国互联
            doAbroad(msg.getInnerConnectedEndpointUuid(),nvoA,vlanA,vlanZ,interfaceVOA,interfaceVOZ,switchPortVOA,switchPortVOZ,false,false,vo);
        }else{
            tunnelBase.createTunnelSwitchPort(vo,interfaceVOA,switchPortVOZ.getUuid(),vlanA,"A",false);
            tunnelBase.createTunnelSwitchPort(vo,interfaceVOZ,switchPortVOA.getUuid(),vlanZ,"Z",false);
        }

        return vo.getUuid();
    }

    private void validateDuplicateVsiVlan(String switchPortUuid, Integer vsi){
        TunnelBase tunnelBase = new TunnelBase();
        PhysicalSwitchVO physicalSwitchVO = tunnelBase.getPhysicalSwitchBySwitchPortUuid(switchPortUuid);

        physicalSwitchVO = tunnelBase.getUplinkMplsSwitchByPhysicalSwitch(physicalSwitchVO);

        String sql = "select count(*) from TunnelSwitchPortVO tp, TunnelVO t" +
                " where tp.tunnelUuid = t.uuid" +
                " and tp.ownerMplsSwitchUuid = :ownerMplsSwitchUuid" +
                " and t.vsi = :vsi";
        TypedQuery<Long> tq = dbf.getEntityManager().createQuery(sql, Long.class);
        tq.setParameter("ownerMplsSwitchUuid", physicalSwitchVO.getUuid());
        tq.setParameter("vsi", vsi);
        if (tq.getSingleResult() > 0){
            throw new ApiMessageInterceptionException(argerr("同一设备下VSI只允许绑定一个Vlan"));
        }
    }

    private void doAbroad(String innerConnectedEndpointUuid,
                          NodeVO nvoA,
                          Integer vlanA,
                          Integer vlanZ,
                          InterfaceVO interfaceVOA,
                          InterfaceVO interfaceVOZ,
                          SwitchPortVO switchPortVOA,
                          SwitchPortVO switchPortVOZ,
                          boolean isQinqA,
                          boolean isQinqZ,
                          TunnelVO vo){
        TunnelBase tunnelBase = new TunnelBase();
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
        Integer vlanBC;

        if(nvoA.getCountry().equals("CHINA")){  //A端国内Z端国外
            if(tunnelBase.isSamePhysicalSwitchForTunnel(switchPortVOA,innerSwitchPort)){
                vlanBC = vlanA;
            }else{
                if(ts.vlanIsAvailable(innerSwitch.getUuid(), switchPortVOA.getSwitchUuid(), vlanA) && ts.vlanIsAvailable(outerSwitch.getUuid(), switchPortVOZ.getUuid(), vlanA)){
                    vlanBC = vlanA;
                }else{
                    vlanBC = ts.getVlanByStrategy(innerSwitch.getUuid(), switchPortVOA.getSwitchUuid());
                    if (vlanBC == 0) {
                        throw new ApiMessageInterceptionException(argerr("该端口[%s]所属虚拟交换机下已无可使用的VLAN，请联系系统管理员",innerSwitchPort.getUuid()));
                    }
                }

            }
            tunnelBase.createTunnelSwitchPortForAbroad(vo,innerConnectedEndpointUuid,innerSwitchPort.getUuid(),switchPortVOA.getUuid(),vlanBC,"B");
            tunnelBase.createTunnelSwitchPortForAbroad(vo,innerConnectedEndpointUuid,outerSwitchPort.getUuid(),switchPortVOZ.getUuid(),vlanBC,"C");
            tunnelBase.createTunnelSwitchPort(vo,interfaceVOA,innerSwitchPort.getUuid(),vlanA,"A",isQinqA);
            tunnelBase.createTunnelSwitchPort(vo,interfaceVOZ,outerSwitchPort.getUuid(),vlanZ,"Z",isQinqZ);
        }else{                                  //A端国外Z端国内
            if(tunnelBase.isSamePhysicalSwitchForTunnel(switchPortVOZ,innerSwitchPort)){
                vlanBC = vlanZ;
            }else{
                if(ts.vlanIsAvailable(innerSwitch.getUuid(), switchPortVOZ.getSwitchUuid(), vlanZ) && ts.vlanIsAvailable(outerSwitch.getUuid(), switchPortVOA.getSwitchUuid(), vlanZ)){
                    vlanBC = vlanZ;
                }else{
                    vlanBC = ts.getVlanByStrategy(innerSwitch.getUuid(), switchPortVOZ.getSwitchUuid());
                    if (vlanBC == 0) {
                        throw new ApiMessageInterceptionException(argerr("该端口[%s]所属虚拟交换机下已无可使用的VLAN，请联系系统管理员!",innerSwitchPort.getUuid()));
                    }
                }

            }
            tunnelBase.createTunnelSwitchPort(vo,interfaceVOA,outerSwitchPort.getUuid(),vlanA,"A",isQinqA);
            tunnelBase.createTunnelSwitchPort(vo,interfaceVOZ,innerSwitchPort.getUuid(),vlanZ,"Z",isQinqZ);
            tunnelBase.createTunnelSwitchPortForAbroad(vo,innerConnectedEndpointUuid,outerSwitchPort.getUuid(),switchPortVOA.getUuid(),vlanBC,"B");
            tunnelBase.createTunnelSwitchPortForAbroad(vo,innerConnectedEndpointUuid,innerSwitchPort.getUuid(),switchPortVOZ.getUuid(),vlanBC,"C");
        }
    }

    private void afterCreateTunnel(TunnelVO vo, APICreateTunnelMsg msg, ReturnValueCompletion<TunnelInventory> completion) {

        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();
        TunnelBase tunnelBase = new TunnelBase();

        //支付专线订单
        APICreateBuyOrderMsg orderMsg = new APICreateBuyOrderMsg();
        List<ProductInfoForOrder> products = new ArrayList<>();

        ProductInfoForOrder productInfoForOrderTunnel = tunnelBillingBase.createBuyOrderForTunnel(vo, msg, new CreateTunnelCallBack());

        products.add(productInfoForOrderTunnel);
        orderMsg.setProducts(products);

        //调用支付
        List<OrderInventory> inventories = tunnelBillingBase.createBuyOrder(orderMsg);

        if (!inventories.isEmpty()) {     //付款成功
            //支付成功修改状态,记录生效订单

            tunnelBillingBase.saveResourceOrderEffective(inventories.get(0).getUuid(), vo.getUuid(), vo.getClass().getSimpleName());
            //修改tunnel状态
            vo.setAccountUuid(vo.getOwnerAccountUuid());
            vo.setState(TunnelState.Deploying);
            vo.setStatus(TunnelStatus.Connecting);
            final TunnelVO vo2 = dbf.updateAndRefresh(vo);

            //创建任务
            TaskResourceVO taskResourceVO = tunnelBase.newTaskResourceVO(vo2, TaskType.Create);

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

                        if(reply.getError().getDetails().contains("failed to execute the command and rollback")){
                            logger.info("创建专线失败，控制器回滚失败，开始回滚控制器");
                            new TunnelJobAndTaskBase().taskRollBackCreateTunnel(vo2.getUuid());
                        }

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
     **/
    private void handle(APICreateTunnelManualMsg msg) {

        //创建Tunnel,TunnelSwitchPort
        String tunnelUuid = doCreateTunnelVOManual(msg);

        TunnelVO vo = dbf.findByUuid(tunnelUuid, TunnelVO.class);

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
    private String doCreateTunnelVOManual(APICreateTunnelManualMsg msg) {
        TunnelBase tunnelBase = new TunnelBase();

        BandwidthOfferingVO bandwidthOfferingVO = dbf.findByUuid(msg.getBandwidthOfferingUuid(), BandwidthOfferingVO.class);
        InterfaceVO interfaceVOA = dbf.findByUuid(msg.getInterfaceAUuid(), InterfaceVO.class);
        InterfaceVO interfaceVOZ = dbf.findByUuid(msg.getInterfaceZUuid(), InterfaceVO.class);
        SwitchPortVO switchPortVOA = dbf.findByUuid(interfaceVOA.getSwitchPortUuid(),SwitchPortVO.class);
        SwitchPortVO switchPortVOZ = dbf.findByUuid(interfaceVOZ.getSwitchPortUuid(),SwitchPortVO.class);
        EndpointVO evoA = dbf.findByUuid(interfaceVOA.getEndpointUuid(), EndpointVO.class);
        EndpointVO evoZ = dbf.findByUuid(interfaceVOZ.getEndpointUuid(), EndpointVO.class);
        NodeVO nvoA = dbf.findByUuid(evoA.getNodeUuid(), NodeVO.class);
        NodeVO nvoZ = dbf.findByUuid(evoZ.getNodeUuid(), NodeVO.class);

        TunnelType tunnelType = tunnelBase.getTunnelType(nvoA, nvoZ, msg.getInnerConnectedEndpointUuid());

        TunnelVO vo = new TunnelVO();
        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(null);
        vo.setOwnerAccountUuid(msg.getAccountUuid());
        vo.setVsi(tunnelBase.getVsiAuto());
        vo.setMonitorCidr(null);
        vo.setName(msg.getName());
        vo.setDuration(msg.getDuration());
        vo.setBandwidthOffering(msg.getBandwidthOfferingUuid());
        vo.setBandwidth(bandwidthOfferingVO.getBandwidth());
        vo.setState(TunnelState.Unpaid);
        vo.setStatus(TunnelStatus.Disconnected);
        vo.setType(tunnelType);
        vo.setInnerEndpointUuid(msg.getInnerConnectedEndpointUuid());
        vo.setExpireDate(null);
        vo.setDescription(msg.getDescription());
        vo.setProductChargeModel(msg.getProductChargeModel());
        vo.setMonitorState(TunnelMonitorState.Disabled);
        vo.setMaxModifies(CoreGlobalProperty.TUNNEL_MAX_MOTIFIES);
        vo.setDistance(Distance.getDistance(nvoA.getLongitude(), nvoA.getLatitude(), nvoZ.getLongitude(), nvoZ.getLatitude()));
        dbf.getEntityManager().persist(vo);

        if(tunnelType == TunnelType.CHINA2ABROAD){     //跨国互联
            doAbroad(msg.getInnerConnectedEndpointUuid(),nvoA,msg.getaVlan(),msg.getzVlan(),interfaceVOA,interfaceVOZ,switchPortVOA,switchPortVOZ,msg.isQinqA(),msg.isQinqZ(),vo);
        }else{
            tunnelBase.createTunnelSwitchPort(vo,interfaceVOA,switchPortVOZ.getUuid(),msg.getaVlan(),"A",msg.isQinqA());
            tunnelBase.createTunnelSwitchPort(vo,interfaceVOZ,switchPortVOA.getUuid(),msg.getzVlan(),"Z",msg.isQinqZ());
        }

        //如果开启Qinq,需要指定内部vlan段
        if (msg.isQinqA() || msg.isQinqZ()) {
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

        return vo.getUuid();
    }

    private void afterCreateTunnelManual(TunnelVO vo, APICreateTunnelManualMsg msg, ReturnValueCompletion<TunnelInventory> completion) {
        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();

        //调用支付
        APICreateBuyOrderMsg orderMsg = new APICreateBuyOrderMsg();
        List<ProductInfoForOrder> products = new ArrayList<>();
        ProductInfoForOrder productInfoForOrderTunnel = tunnelBillingBase.createBuyOrderForTunnelManual(vo, msg, new CreateTunnelCallBack());

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

                    if(reply.getError().getDetails().contains("failed to execute the command and rollback")){
                        logger.info("创建专线失败，控制器回滚失败，开始回滚控制器.");
                        new TunnelJobAndTaskBase().taskRollBackCreateTunnel(vo2.getUuid());
                    }

                    completion.success(TunnelInventory.valueOf(dbf.reload(vo2)));
                }
            }
        });
    }

    /**
     * 切换云专线已有的物理接口和修改端口VLAN
     **/
    private void handle(APIUpdateTunnelVlanMsg msg) {
        APIUpdateTunnelVlanEvent evt = new APIUpdateTunnelVlanEvent(msg.getId());
        TunnelBase tunnelBase = new TunnelBase();
        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);

        //如果vlan改变，问专线是否被互联云，VPN使用
        if (!msg.getaVlan().equals(msg.getOldAVlan()) || !msg.getzVlan().equals(msg.getOldZVlan())) {
            for (TunnelDeletionExtensionPoint extp : pluginRgty.getExtensionList(TunnelDeletionExtensionPoint.class)) {
                extp.preDelete(vo);
            }
        }

        //修改前的TunnelSwitchPort
        TunnelSwitchPortVO tunnelSwitchPortA = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "A")
                .find();
        TunnelSwitchPortVO tunnelSwitchPortB = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "B")
                .find();
        TunnelSwitchPortVO tunnelSwitchPortC = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "C")
                .find();
        TunnelSwitchPortVO tunnelSwitchPortZ = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "Z")
                .find();

        //修改后的端口
        String switchPortUuidA = dbf.findByUuid(msg.getInterfaceAUuid(), InterfaceVO.class).getSwitchPortUuid();
        SwitchPortVO switchPortVOA = dbf.findByUuid(switchPortUuidA,SwitchPortVO.class);

        String switchPortUuidZ = dbf.findByUuid(msg.getInterfaceZUuid(), InterfaceVO.class).getSwitchPortUuid();
        SwitchPortVO switchPortVOZ = dbf.findByUuid(switchPortUuidZ,SwitchPortVO.class);

        //修改后的交换机
        PhysicalSwitchVO physicalSwitchA = tunnelBase.getPhysicalSwitchBySwitchPortUuid(switchPortUuidA);
        PhysicalSwitchVO remotePhysicalSwitchA ;

        PhysicalSwitchVO physicalSwitchZ = tunnelBase.getPhysicalSwitchBySwitchPortUuid(switchPortUuidZ);
        PhysicalSwitchVO remotePhysicalSwitchZ ;

        boolean isABsame = false;
        boolean isCZsame = false;
        if(tunnelSwitchPortB != null){
            if(tunnelBase.isSamePhysicalSwitchForTunnel(switchPortVOA,dbf.findByUuid(tunnelSwitchPortB.getSwitchPortUuid(),SwitchPortVO.class))){
                isABsame = true;
            }
            if(tunnelBase.isSamePhysicalSwitchForTunnel(switchPortVOZ,dbf.findByUuid(tunnelSwitchPortC.getSwitchPortUuid(),SwitchPortVO.class))){
                isCZsame = true;
            }

            remotePhysicalSwitchA = tunnelBase.getPhysicalSwitchBySwitchPortUuid(tunnelSwitchPortB.getSwitchPortUuid());
            remotePhysicalSwitchZ = tunnelBase.getPhysicalSwitchBySwitchPortUuid(tunnelSwitchPortC.getSwitchPortUuid());
        }else{
            remotePhysicalSwitchA = tunnelBase.getPhysicalSwitchBySwitchPortUuid(switchPortUuidZ);
            remotePhysicalSwitchZ = tunnelBase.getPhysicalSwitchBySwitchPortUuid(switchPortUuidA);
        }

        FlowChain updatevlan = FlowChainBuilder.newSimpleFlowChain();
        updatevlan.setName(String.format("update-tunnelvlan-%s", msg.getUuid()));
        boolean finalIsABsame = isABsame;
        boolean finalIsCZsame = isCZsame;
        updatevlan.then(new Flow() {
            @Override
            public void run(FlowTrigger trigger, Map data) {
                if (!Objects.equals(msg.getInterfaceAUuid(), msg.getOldInterfaceAUuid()) || !Objects.equals(msg.getaVlan(), msg.getOldAVlan())) {
                    UpdateQuery.New(TunnelSwitchPortVO.class)
                            .set(TunnelSwitchPortVO_.interfaceUuid,msg.getInterfaceAUuid())
                            .set(TunnelSwitchPortVO_.switchPortUuid, dbf.findByUuid(msg.getInterfaceAUuid(), InterfaceVO.class).getSwitchPortUuid())
                            .set(TunnelSwitchPortVO_.vlan, msg.getaVlan())
                            .set(TunnelSwitchPortVO_.physicalSwitchUuid, physicalSwitchA.getUuid())
                            .set(TunnelSwitchPortVO_.ownerMplsSwitchUuid, tunnelBase.getUplinkMplsSwitchByPhysicalSwitch(physicalSwitchA).getUuid())
                            .set(TunnelSwitchPortVO_.peerMplsSwitchUuid, tunnelBase.getUplinkMplsSwitchByPhysicalSwitch(remotePhysicalSwitchA).getUuid())
                            .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                            .eq(TunnelSwitchPortVO_.sortTag, "A")
                            .update();

                    if(tunnelSwitchPortB != null){
                        if(finalIsABsame){
                            UpdateQuery.New(TunnelSwitchPortVO.class)
                                    .set(TunnelSwitchPortVO_.vlan, msg.getaVlan())
                                    .set(TunnelSwitchPortVO_.peerMplsSwitchUuid, tunnelBase.getUplinkMplsSwitchByPhysicalSwitch(physicalSwitchA).getUuid())
                                    .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                                    .eq(TunnelSwitchPortVO_.sortTag, "B")
                                    .update();
                            UpdateQuery.New(TunnelSwitchPortVO.class)
                                    .set(TunnelSwitchPortVO_.vlan, msg.getaVlan())
                                    .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                                    .eq(TunnelSwitchPortVO_.sortTag, "C")
                                    .update();
                        }else{
                            UpdateQuery.New(TunnelSwitchPortVO.class)
                                    .set(TunnelSwitchPortVO_.peerMplsSwitchUuid, tunnelBase.getUplinkMplsSwitchByPhysicalSwitch(physicalSwitchA).getUuid())
                                    .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                                    .eq(TunnelSwitchPortVO_.sortTag, "B")
                                    .update();
                        }
                    }

                    logger.info("修改A端物理接口或VLAN");
                }

                trigger.next();
            }

            @Override
            public void rollback(FlowRollback trigger, Map data) {
                if (!Objects.equals(msg.getInterfaceAUuid(), msg.getOldInterfaceAUuid()) || !Objects.equals(msg.getaVlan(), msg.getOldAVlan())) {

                    dbf.updateAndRefresh(tunnelSwitchPortA);
                    if(tunnelSwitchPortB != null){
                        if(finalIsABsame){
                            dbf.updateAndRefresh(tunnelSwitchPortB);
                            dbf.updateAndRefresh(tunnelSwitchPortC);
                        }else{
                            dbf.updateAndRefresh(tunnelSwitchPortB);
                        }
                    }

                    logger.info("回滚A端物理接口或VLAN");
                }

                trigger.rollback();
            }
        }).then(new Flow() {
            @Override
            public void run(FlowTrigger trigger, Map data) {
                if (!Objects.equals(msg.getInterfaceZUuid(), msg.getOldInterfaceZUuid()) || !Objects.equals(msg.getzVlan(), msg.getOldZVlan())) {
                    UpdateQuery.New(TunnelSwitchPortVO.class)
                            .set(TunnelSwitchPortVO_.interfaceUuid,msg.getInterfaceZUuid())
                            .set(TunnelSwitchPortVO_.switchPortUuid, dbf.findByUuid(msg.getInterfaceZUuid(), InterfaceVO.class).getSwitchPortUuid())
                            .set(TunnelSwitchPortVO_.vlan, msg.getzVlan())
                            .set(TunnelSwitchPortVO_.physicalSwitchUuid, physicalSwitchZ.getUuid())
                            .set(TunnelSwitchPortVO_.ownerMplsSwitchUuid, tunnelBase.getUplinkMplsSwitchByPhysicalSwitch(physicalSwitchZ).getUuid())
                            .set(TunnelSwitchPortVO_.peerMplsSwitchUuid, tunnelBase.getUplinkMplsSwitchByPhysicalSwitch(remotePhysicalSwitchZ).getUuid())
                            .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                            .eq(TunnelSwitchPortVO_.sortTag, "Z")
                            .update();

                    if(tunnelSwitchPortB != null){
                        if(finalIsCZsame){
                            UpdateQuery.New(TunnelSwitchPortVO.class)
                                    .set(TunnelSwitchPortVO_.vlan, msg.getzVlan())
                                    .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                                    .eq(TunnelSwitchPortVO_.sortTag, "B")
                                    .update();
                            UpdateQuery.New(TunnelSwitchPortVO.class)
                                    .set(TunnelSwitchPortVO_.vlan, msg.getzVlan())
                                    .set(TunnelSwitchPortVO_.peerMplsSwitchUuid, tunnelBase.getUplinkMplsSwitchByPhysicalSwitch(physicalSwitchZ).getUuid())
                                    .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                                    .eq(TunnelSwitchPortVO_.sortTag, "C")
                                    .update();
                        }else{
                            UpdateQuery.New(TunnelSwitchPortVO.class)
                                    .set(TunnelSwitchPortVO_.peerMplsSwitchUuid, tunnelBase.getUplinkMplsSwitchByPhysicalSwitch(physicalSwitchZ).getUuid())
                                    .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                                    .eq(TunnelSwitchPortVO_.sortTag, "C")
                                    .update();
                        }
                    }

                    logger.info("修改Z端物理接口或VLAN");
                }

                trigger.next();
            }

            @Override
            public void rollback(FlowRollback trigger, Map data) {
                if (!Objects.equals(msg.getInterfaceZUuid(), msg.getOldInterfaceZUuid()) || !Objects.equals(msg.getzVlan(), msg.getOldZVlan())) {

                    dbf.updateAndRefresh(tunnelSwitchPortZ);
                    if(tunnelSwitchPortB != null){
                        if(finalIsCZsame){
                            dbf.updateAndRefresh(tunnelSwitchPortB);
                            dbf.updateAndRefresh(tunnelSwitchPortC);
                        }else{
                            dbf.updateAndRefresh(tunnelSwitchPortC);
                        }
                    }

                    logger.info("回滚Z端物理接口或VLAN");
                }
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
     * 修改云专线的名称和描述
     **/
    private void handle(APIUpdateTunnelMsg msg) {
        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);
        boolean update = false;
        boolean nameChange = false;

        if (msg.getName() != null && !msg.getName().equals(vo.getName())) {
            vo.setName(msg.getName());
            update = true;
            nameChange = true;
        }

        if (msg.getDescription() != null) {
            vo.setDescription(msg.getDescription());
            update = true;
        }

        if (update) {
            vo = dbf.updateAndRefresh(vo);
            if (nameChange)
                RenameBillingProductNameJob.executeJob(jobf, vo.getUuid(), vo.getName());
        }

        APIUpdateTunnelEvent evt = new APIUpdateTunnelEvent(msg.getId());
        evt.setInventory(TunnelInventory.valueOf(vo));
        bus.publish(evt);
    }

    /**
     * 调整云专线带宽
     **/
    private void handle(APIUpdateTunnelBandwidthMsg msg) {
        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();
        APIUpdateTunnelBandwidthEvent evt = new APIUpdateTunnelBandwidthEvent(msg.getId());

        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);

        TunnelSwitchPortVO tunnelSwitchPortA = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, vo.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "A")
                .find();
        EndpointVO evoA = dbf.findByUuid(tunnelSwitchPortA.getEndpointUuid(),EndpointVO.class);
        String interfaceUuidA = tunnelSwitchPortA.getInterfaceUuid();

        TunnelSwitchPortVO tunnelSwitchPortZ = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, vo.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "Z")
                .find();
        EndpointVO evoZ = dbf.findByUuid(tunnelSwitchPortZ.getEndpointUuid(),EndpointVO.class);
        String interfaceUuidZ = tunnelSwitchPortZ.getInterfaceUuid();

        BandwidthOfferingVO bandwidthOfferingVO = dbf.findByUuid(msg.getBandwidthOfferingUuid(), BandwidthOfferingVO.class);

        //调整次数记录表
        ResourceMotifyRecordVO record = new ResourceMotifyRecordVO();
        record.setUuid(Platform.getUuid());
        record.setResourceUuid(vo.getUuid());
        record.setResourceType("TunnelVO");
        record.setOpAccountUuid(msg.getSession().getAccountUuid());
        record.setMotifyType(bandwidthOfferingVO.getBandwidth() > vo.getBandwidth() ? MotifyType.UPGRADE : MotifyType.DOWNGRADE);
        dbf.persistAndRefresh(record);

        //调用支付-调整带宽
        UpdateTunnelBandwidthCallBack uc = new UpdateTunnelBandwidthCallBack();
        uc.setBandwidthOffering(msg.getBandwidthOfferingUuid());
        uc.setBandwidth(bandwidthOfferingVO.getBandwidth());

        APICreateModifyOrderMsg orderMsg = new APICreateModifyOrderMsg();
        orderMsg.setProductUuid(vo.getUuid());
        orderMsg.setProductName(vo.getName());
        orderMsg.setDescriptionData(tunnelBillingBase.getDescriptionForTunnel(vo,bandwidthOfferingVO.getBandwidth()));
        orderMsg.setCallBackData(RESTApiDecoder.dump(uc));
        orderMsg.setUnits(tunnelBillingBase.getTunnelPriceUnit(msg.getBandwidthOfferingUuid(), interfaceUuidA, interfaceUuidZ, evoA, evoZ, vo.getInnerEndpointUuid()));
        orderMsg.setProductType(ProductType.TUNNEL);
        orderMsg.setAccountUuid(vo.getOwnerAccountUuid());
        orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
        orderMsg.setStartTime(dbf.getCurrentSqlTime());
        orderMsg.setExpiredTime(vo.getExpireDate());
        orderMsg.setNotifyUrl(restf.getSendCommandUrl());

        OrderInventory orderInventory = tunnelBillingBase.createOrder(orderMsg);
        if (orderInventory != null) {
            //付款成功,记录生效订单
            tunnelBillingBase.saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());

            vo.setBandwidthOffering(msg.getBandwidthOfferingUuid());
            vo.setBandwidth(bandwidthOfferingVO.getBandwidth());
            vo = dbf.updateAndRefresh(vo);

            logger.info("修改带宽支付成功，并创建任务：UpdateBandwidthJob");
            UpdateBandwidthJob job = new UpdateBandwidthJob(vo.getUuid());
            jobf.execute("调整专线带宽-控制器下发", Platform.getManagementServerId(), job);

            evt.setInventory(TunnelInventory.valueOf(vo));
            bus.publish(evt);

        } else {
            evt.setError(errf.stringToOperationError("订单操作失败"));
            bus.publish(evt);
        }
    }

    /**
     * 云专线续费
     **/
    private void handle(APIRenewTunnelMsg msg) {
        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);
        APIRenewTunnelReply reply
                = renewTunnel(msg.getUuid(),
                msg.getDuration(),
                msg.getProductChargeModel(),
                vo.getOwnerAccountUuid(),
                msg.getSession().getAccountUuid(),false);

        bus.reply(msg, reply);
    }

    /**
     * 云专线自动续费
     **/
    private void handle(APIRenewAutoTunnelMsg msg) {
        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);
        APIRenewTunnelReply reply
                = renewTunnel(msg.getUuid(),
                msg.getDuration(),
                msg.getProductChargeModel(),
                vo.getOwnerAccountUuid(),
                vo.getAccountUuid(),true);

        bus.reply(msg, reply);
    }

    private APIRenewTunnelReply renewTunnel(String uuid,
                                            Integer duration,
                                            ProductChargeModel productChargeModel,
                                            String accountUuid,
                                            String opAccountUuid,boolean isAutoRenew) {

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
        renewOrderMsg.setDescriptionData(tunnelBillingBase.getDescriptionForTunnel(vo,null));
        renewOrderMsg.setProductChargeModel(productChargeModel);
        renewOrderMsg.setAccountUuid(accountUuid);
        renewOrderMsg.setOpAccountUuid(opAccountUuid);
        renewOrderMsg.setStartTime(dbf.getCurrentSqlTime());
        renewOrderMsg.setExpiredTime(vo.getExpireDate());
        renewOrderMsg.setNotifyUrl(restf.getSendCommandUrl());
        renewOrderMsg.setCallBackData(RESTApiDecoder.dump(rc));
        renewOrderMsg.setAutoRenew(isAutoRenew);

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
        slaCompensationOrderMsg.setSlaUuid(msg.getSlaUuid());
        slaCompensationOrderMsg.setProductUuid(vo.getUuid());
        slaCompensationOrderMsg.setProductName(vo.getName());
        slaCompensationOrderMsg.setDescriptionData(tunnelBillingBase.getDescriptionForTunnel(vo,null));
        slaCompensationOrderMsg.setProductType(ProductType.TUNNEL);
        slaCompensationOrderMsg.setDuration(msg.getDuration());
        slaCompensationOrderMsg.setAccountUuid(vo.getOwnerAccountUuid());
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

        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);

        for (TunnelDeletionExtensionPoint extp : pluginRgty.getExtensionList(TunnelDeletionExtensionPoint.class)) {
            extp.preDelete(vo);
        }

        doDeleteTunnel(vo, false, msg.getSession().getAccountUuid(), new ReturnValueCompletion<TunnelInventory>(null) {

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

    /**
     * 云专线强制删除，只退订，不下发
     * Deployfailure
     * Enabled
     * Disabled
     **/
    private void handle(APIDeleteForciblyTunnelMsg msg) {
        APIDeleteForciblyTunnelEvent evt = new APIDeleteForciblyTunnelEvent(msg.getId());

        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);

        for (TunnelDeletionExtensionPoint extp : pluginRgty.getExtensionList(TunnelDeletionExtensionPoint.class)) {
            extp.preDelete(vo);
        }

        doDeleteTunnel(vo, true, msg.getSession().getAccountUuid(), new ReturnValueCompletion<TunnelInventory>(null) {

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

    private void doDeleteTunnel(TunnelVO vo, boolean isForcibly, String opAccountUuid, ReturnValueCompletion<TunnelInventory> completion){
        TunnelBase tunnelBase = new TunnelBase();

        try {
            for (TunnelDeletionExtensionPoint extp : pluginRgty.getExtensionList(TunnelDeletionExtensionPoint.class)) {
                extp.beforeDelete(vo);
            }
        } catch (Throwable t) {
            completion.fail(errf.stringToOperationError("删除专线相关联互联云失败"));
            return;
        }

        //退订，删除
        doUnsubcribeTunnel(vo, isForcibly,false,opAccountUuid,new Completion(null) {
            @Override
            public void success() {
                final TunnelVO vo2;

                if (isForcibly){
                    vo2 = tunnelBase.doDeleteTunnelDBAfterUnsubcribeSuccess(vo);
                }else{
                    vo2 = tunnelBase.doControlDeleteTunnelAfterUnsubcribeSuccess(vo);
                }
                completion.success(TunnelInventory.valueOf(vo2));
            }

            @Override
            public void fail(ErrorCode errorCode) {
                completion.fail(errorCode);
            }
        });

    }

    private void doUnsubcribeTunnel(TunnelVO vo, boolean isForcibly, boolean isUnsupport, String opAccountUuid,Completion completionUnsub){
        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();

        //已经退订过的或者已经到期的专线不用退订
        if (vo.getExpireDate() != null && (!vo.getExpireDate().after(Timestamp.valueOf(LocalDateTime.now())) || vo.getAccountUuid() == null)) {
            completionUnsub.success();
            return;
        }

        DeleteTunnelCallBack dc = new DeleteTunnelCallBack();
        if(isForcibly){
            dc.setDescription("forciblydelete");
        }else if(isUnsupport){
            dc.setDescription("unsupport");
        }else{
            dc.setDescription("delete");
        }

        APICreateUnsubcribeOrderMsg orderMsg = new APICreateUnsubcribeOrderMsg();
        orderMsg.setProductUuid(vo.getUuid());
        orderMsg.setProductType(ProductType.TUNNEL);
        orderMsg.setProductName(vo.getName());
        orderMsg.setAccountUuid(vo.getOwnerAccountUuid());
        orderMsg.setOpAccountUuid(opAccountUuid);
        orderMsg.setStartTime(dbf.getCurrentSqlTime());
        if (vo.getExpireDate() == null) {
            orderMsg.setExpiredTime(dbf.getCurrentSqlTime());
            orderMsg.setCreateFailure(true);
        } else {
            orderMsg.setExpiredTime(vo.getExpireDate());
        }
        orderMsg.setDescriptionData(tunnelBillingBase.getDescriptionForTunnel(vo,null));
        orderMsg.setCallBackData(RESTApiDecoder.dump(dc));
        orderMsg.setNotifyUrl(restf.getSendCommandUrl());

        OrderInventory orderInventory = tunnelBillingBase.createOrder(orderMsg);
        if (orderInventory != null) {
            //退订成功,记录生效订单
            tunnelBillingBase.saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());
            completionUnsub.success();
        } else {
            completionUnsub.fail(errf.stringToOperationError("退订失败"));
        }
    }


    /**
     * 恢复链接
     * */
    private void handle(APIEnableTunnelMsg msg){
        APIEnableTunnelEvent evt = new APIEnableTunnelEvent(msg.getId());

        TunnelJobAndTaskBase taskBase = new TunnelJobAndTaskBase();

        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);

        if(msg.isSaveOnly()){

            taskBase.taskEnableTunnelZK(vo.getUuid());

            vo.setState(TunnelState.Enabled);
            vo.setStatus(TunnelStatus.Connected);
            dbf.updateAndRefresh(vo);

            evt.setInventory(TunnelInventory.valueOf(vo));
            bus.publish(evt);

        }else{
            taskBase.taskEnableTunnel(vo,new ReturnValueCompletion<TunnelInventory>(null) {
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

    /**
     * 关闭连接
     * */
    private void handle(APIDisableTunnelMsg msg){
        APIDisableTunnelEvent evt = new APIDisableTunnelEvent(msg.getId());

        TunnelJobAndTaskBase taskBase = new TunnelJobAndTaskBase();

        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);


        if(msg.isSaveOnly()){

            taskBase.taskDisableTunnelZK(vo.getUuid());

            vo.setState(TunnelState.Disabled);
            vo.setStatus(TunnelStatus.Disconnected);
            vo = dbf.updateAndRefresh(vo);

            evt.setInventory(TunnelInventory.valueOf(vo));
            bus.publish(evt);

        }else{
            taskBase.taskDisableTunnel(vo,new ReturnValueCompletion<TunnelInventory>(null) {
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

    /**
     * 开通（是否仅保存）
     **/
    private void handle(APIOpenTunnelMsg msg) {
        TunnelJobAndTaskBase taskBase = new TunnelJobAndTaskBase();
        APIOpenTunnelEvent evt = new APIOpenTunnelEvent(msg.getId());

        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);

        if (msg.isSaveOnly()) {

            vo.setState(TunnelState.Enabled);
            vo.setStatus(TunnelStatus.Connected);
            if (vo.getProductChargeModel() == ProductChargeModel.BY_MONTH) {
                vo.setExpireDate(Timestamp.valueOf(LocalDateTime.now().plusMonths(vo.getDuration())));
            } else if (vo.getProductChargeModel() == ProductChargeModel.BY_YEAR) {
                vo.setExpireDate(Timestamp.valueOf(LocalDateTime.now().plusYears(vo.getDuration())));
            }

            vo = dbf.updateAndRefresh(vo);

            new TunnelJobAndTaskBase().taskCreateTunnelZK(vo.getUuid());

            //开通成功后修改订单到期时间
            logger.info("修改订单到期时间，并创建任务：UpdateOrderExpiredTimeJob");
            UpdateOrderExpiredTimeJob job = new UpdateOrderExpiredTimeJob();
            job.setResourceUuid(vo.getUuid());
            job.setStartTime(dbf.getCurrentSqlTime());
            job.setEndTime(vo.getExpireDate());
            jobf.execute("修改订单到期时间", Platform.getManagementServerId(), job);

            evt.setInventory(TunnelInventory.valueOf(vo));
            bus.publish(evt);
        } else {
            final TunnelVO vo2 = vo;
            taskBase.taskOpenTunnel(vo2,new ReturnValueCompletion<TunnelInventory>(null) {
                @Override
                public void success(TunnelInventory inv) {

                    //开通成功后修改订单到期时间
                    logger.info("修改订单到期时间，并创建任务：UpdateOrderExpiredTimeJob");
                    final TunnelVO vo3 = dbf.findByUuid(vo2.getUuid(), TunnelVO.class);
                    UpdateOrderExpiredTimeJob job = new UpdateOrderExpiredTimeJob();
                    job.setResourceUuid(vo3.getUuid());
                    job.setStartTime(dbf.getCurrentSqlTime());
                    job.setEndTime(vo3.getExpireDate());
                    jobf.execute("修改订单到期时间", Platform.getManagementServerId(), job);

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

    /**
     * 无法开通
     **/
    private void handle(APIUnsupportTunnelMsg msg) {
        APIUnsupportTunnelEvent evt = new APIUnsupportTunnelEvent(msg.getId());

        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);

        doUnsubcribeTunnel(vo,false,true,msg.getSession().getAccountUuid(),new Completion(null) {
            @Override
            public void success() {
                vo.setState(TunnelState.Unsupport);
                vo.setStatus(TunnelStatus.Disconnected);
                vo.setExpireDate(dbf.getCurrentSqlTime());
                final TunnelVO vo2 = dbf.updateAndRefresh(vo);

                evt.setInventory(TunnelInventory.valueOf(vo2));
                bus.publish(evt);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                evt.setError(errf.stringToOperationError("退订失败"));
                bus.publish(evt);
            }
        });

    }

    /**
     * 设置QINQ
     * */
    private void handle(APIUpdateQinqMsg msg){
        APIUpdateQinqEvent evt = new APIUpdateQinqEvent(msg.getId());

        InterfaceVO interfaceVOA = dbf.findByUuid(msg.getInterfaceUuidA(), InterfaceVO.class);
        InterfaceVO interfaceVOZ = dbf.findByUuid(msg.getInterfaceUuidZ(), InterfaceVO.class);

        //将内部VLAN段转为QinqVO
        List<QinqVO> qinqVOList = new ArrayList<>();
        if(msg.getVlanSegment() != null && !msg.getVlanSegment().isEmpty()){
            for(InnerVlanSegment innerVlanSegment : msg.getVlanSegment()){
                String uuid = Platform.getUuid();
                QinqVO qinqVO = new QinqVO();
                qinqVO.setUuid(uuid);
                qinqVO.setTunnelUuid(msg.getUuid());
                qinqVO.setStartVlan(innerVlanSegment.getStartVlan());
                qinqVO.setEndVlan(innerVlanSegment.getEndVlan());

                qinqVOList.add(qinqVO);
            }
        }

        //修改前的TunnelSwitchPortVO
        TunnelSwitchPortVO tunnelSwitchPortA = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "A")
                .find();
        TunnelSwitchPortVO tunnelSwitchPortZ = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "Z")
                .find();

        //修改前的QinqVO
        List<QinqVO> qinqVOs = Q.New(QinqVO.class)
                .eq(QinqVO_.tunnelUuid,msg.getUuid())
                .list();

        FlowChain updateQinq = FlowChainBuilder.newSimpleFlowChain();
        updateQinq.setName(String.format("update-tunnel-%s-innervlan", msg.getUuid()));
        updateQinq.then(new Flow() {
            @Override
            public void run(FlowTrigger trigger, Map data) {
                logger.info("修改TunnelSwitchPortVO-A");

                if(msg.isQinqA()){
                    UpdateQuery.New(TunnelSwitchPortVO.class)
                            .set(TunnelSwitchPortVO_.type,NetworkType.QINQ)
                            .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                            .eq(TunnelSwitchPortVO_.sortTag, "A")
                            .update();
                }else{
                    UpdateQuery.New(TunnelSwitchPortVO.class)
                            .set(TunnelSwitchPortVO_.type,NetworkType.TRUNK)
                            .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                            .eq(TunnelSwitchPortVO_.sortTag, "A")
                            .update();
                }

                trigger.next();
            }

            @Override
            public void rollback(FlowRollback trigger, Map data) {
                logger.info("还原TunnelSwitchPortVO-A");
                dbf.updateAndRefresh(tunnelSwitchPortA);

                trigger.rollback();
            }
        }).then(new Flow() {
            @Override
            public void run(FlowTrigger trigger, Map data) {
                logger.info("修改TunnelSwitchPortVO-Z");

                if(msg.isQinqZ()){
                    UpdateQuery.New(TunnelSwitchPortVO.class)
                            .set(TunnelSwitchPortVO_.type,NetworkType.QINQ)
                            .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                            .eq(TunnelSwitchPortVO_.sortTag, "Z")
                            .update();
                }else{
                    UpdateQuery.New(TunnelSwitchPortVO.class)
                            .set(TunnelSwitchPortVO_.type,NetworkType.TRUNK)
                            .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                            .eq(TunnelSwitchPortVO_.sortTag, "Z")
                            .update();
                }

                trigger.next();
            }

            @Override
            public void rollback(FlowRollback trigger, Map data) {
                logger.info("还原TunnelSwitchPortVO-Z");
                dbf.updateAndRefresh(tunnelSwitchPortZ);

                trigger.rollback();
            }
        }).then(new Flow() {
            @Override
            public void run(FlowTrigger trigger, Map data) {
                logger.info("删除QINQ配置");
                if(!qinqVOs.isEmpty()){
                    dbf.removeCollection(qinqVOs,QinqVO.class);
                }

                trigger.next();
            }

            @Override
            public void rollback(FlowRollback trigger, Map data) {
                logger.info("还原QINQ配置");
                if(!qinqVOs.isEmpty()){
                    dbf.persistCollection(qinqVOs);
                }

                trigger.rollback();
            }
        }).then(new Flow() {
            @Override
            public void run(FlowTrigger trigger, Map data) {
                logger.info("更新QINQ配置前的验证");
                if(msg.getVlanSegment() != null && !msg.getVlanSegment().isEmpty()){
                    new TunnelValidateBase().validateInnerVlan(msg.isQinqA(),
                            interfaceVOA.getSwitchPortUuid(),
                            msg.isQinqZ(),
                            interfaceVOZ.getSwitchPortUuid(),
                            msg.getVlanSegment());
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
                logger.info("保存更新QINQ配置");

                if(msg.getVlanSegment() != null && !msg.getVlanSegment().isEmpty()){
                    dbf.persistCollection(qinqVOList);
                }

                trigger.next();
            }

            @Override
            public void rollback(FlowRollback trigger, Map data) {
                logger.info("取消更新QINQ配置");

                if(msg.getVlanSegment() != null && !msg.getVlanSegment().isEmpty()){
                    dbf.removeCollection(qinqVOList, QinqVO.class);
                }

                trigger.rollback();
            }
        }).done(new FlowDoneHandler(null) {
            @Override
            public void handle(Map data) {

                if(msg.getVlanSegment() != null && !msg.getVlanSegment().isEmpty()){
                    evt.setInventories(QinqInventory.valueOf(qinqVOList));
                }
                bus.publish(evt);
            }
        }).error(new FlowErrorHandler(null) {
            @Override
            public void handle(ErrorCode errCode, Map data) {
                evt.setError(errf.stringToOperationError("内部VLAN段有冲突!"));
                bus.publish(evt);
            }
        }).start();
    }

    /*****************************  Get/Query/List Message For Interface and Tunnel **************************/

    /**
     * 通过连接点和端口规格获取可用的端口
     */
    private void handle(APIListSwitchPortByTypeMsg msg) {
        List<SwitchPortVO> ports = new TunnelBase().getSwitchPortByType(msg.getUuid(), msg.getType(), msg.getStart(), msg.getLimit());
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
        InterfaceVO vo = dbf.findByUuid(msg.getUuid(), InterfaceVO.class);
        APIListCrossTunnelReply reply = new APIListCrossTunnelReply();
        List<String> tunnelUuids = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.interfaceUuid, msg.getUuid())
                .select(TunnelSwitchPortVO_.tunnelUuid)
                .listValues();
        List<TunnelVO> tunnelVOS = new ArrayList<>();
        if (!tunnelUuids.isEmpty()) {
            tunnelVOS = Q.New(TunnelVO.class).
                    in(TunnelVO_.uuid, tunnelUuids).
                    eq(TunnelVO_.accountUuid, vo.getOwnerAccountUuid()).
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
        TunnelBase tunnelBase = new TunnelBase();

        InterfaceVO interfaceVOA = dbf.findByUuid(msg.getInterfaceUuidA(), InterfaceVO.class);
        InterfaceVO interfaceVOZ = dbf.findByUuid(msg.getInterfaceUuidZ(), InterfaceVO.class);

        EndpointVO evoA = dbf.findByUuid(interfaceVOA.getEndpointUuid(), EndpointVO.class);
        NodeVO nvoA = dbf.findByUuid(evoA.getNodeUuid(), NodeVO.class);

        SwitchPortVO switchPortVOA = dbf.findByUuid(interfaceVOA.getSwitchPortUuid(),SwitchPortVO.class);
        SwitchPortVO switchPortVOZ = dbf.findByUuid(interfaceVOZ.getSwitchPortUuid(),SwitchPortVO.class);

        Integer vlanA;
        Integer vlanZ;
        String peerSwitchUuidA;
        String peerSwitchUuidZ;

        if(msg.getInnerConnectedEndpointUuid() != null){
            EndpointVO endpointVO = dbf.findByUuid(msg.getInnerConnectedEndpointUuid(), EndpointVO.class);
            if(endpointVO.getEndpointType() == EndpointType.INTERCONNECTED){
                SwitchVO innerSwitch = Q.New(SwitchVO.class)
                        .eq(SwitchVO_.endpointUuid, msg.getInnerConnectedEndpointUuid())
                        .eq(SwitchVO_.type, SwitchType.INNER)
                        .find();

                SwitchVO outerSwitch = Q.New(SwitchVO.class)
                        .eq(SwitchVO_.endpointUuid, msg.getInnerConnectedEndpointUuid())
                        .eq(SwitchVO_.type, SwitchType.OUTER)
                        .find();
                if(nvoA.getCountry().equals("CHINA")){
                    peerSwitchUuidA = innerSwitch.getUuid();
                    peerSwitchUuidZ = outerSwitch.getUuid();
                }else{
                    peerSwitchUuidA = outerSwitch.getUuid();
                    peerSwitchUuidZ = innerSwitch.getUuid();
                }
            }else{
                peerSwitchUuidA = switchPortVOZ.getSwitchUuid();
                peerSwitchUuidZ = switchPortVOA.getSwitchUuid();
            }
        }else{
            peerSwitchUuidA = switchPortVOZ.getSwitchUuid();
            peerSwitchUuidZ = switchPortVOA.getSwitchUuid();
        }

        if(tunnelBase.isSamePhysicalSwitchForTunnel(switchPortVOA,switchPortVOZ)) {     //从同一个物理交换机进和出，不可能跨国
            vlanA = ts.getVlanByStrategy(switchPortVOA.getSwitchUuid(), peerSwitchUuidA);
            if (vlanA == 0) {
                throw new ApiMessageInterceptionException(argerr("该端口[%s]所属虚拟交换机下已无可使用的VLAN",switchPortVOA.getUuid()));
            }
            vlanZ = vlanA;
        }else{
            vlanA = ts.getVlanByStrategy(switchPortVOA.getSwitchUuid(), peerSwitchUuidA);
            if (vlanA == 0) {
                throw new ApiMessageInterceptionException(argerr("该端口[%s]所属虚拟交换机下已无可使用的VLAN",switchPortVOA.getUuid()));
            }

            if(ts.vlanIsAvailable(switchPortVOZ.getSwitchUuid(), peerSwitchUuidZ, vlanA)){
                vlanZ = vlanA;
            }else{
                vlanZ = ts.getVlanByStrategy(switchPortVOZ.getSwitchUuid(), peerSwitchUuidZ);
                if (vlanZ == 0) {
                    throw new ApiMessageInterceptionException(argerr("该端口[%s]所属虚拟交换机下已无可使用的VLAN",switchPortVOZ.getUuid()));
                }
            }
        }

        reply.setVlanA(vlanA);
        reply.setVlanZ(vlanZ);
        bus.reply(msg, reply);
    }

    /**
     * 购买时查询物理接口价格
     */
    private void handle(APIGetInterfacePriceMsg msg) {

        if(msg.getPortOfferingUuid().equals("SHARE")){
            APIGetProductPriceReply reply = new APIGetProductPriceReply();
            reply.setOriginalPrice(BigDecimal.ZERO);
            reply.setDiscountPrice(BigDecimal.ZERO);
            reply.setPayable(true);
            bus.reply(msg, new APIGetInterfacePriceReply(reply));
        }else{
            APIGetProductPriceMsg pmsg = new APIGetProductPriceMsg();
            pmsg.setProductChargeModel(msg.getProductChargeModel());
            pmsg.setDuration(msg.getDuration());
            pmsg.setAccountUuid(msg.getAccountUuid());
            pmsg.setUnits(new TunnelBillingBase().getInterfacePriceUnit(msg.getPortOfferingUuid()));
            APIGetProductPriceReply reply = new BillingRESTCaller().syncJsonPost(pmsg);
            bus.reply(msg, new APIGetInterfacePriceReply(reply));
        }

    }

    /**
     * 退订时查询物理接口差价
     */
    private void handle(APIGetUnscribeInterfacePriceDiffMsg msg) {
        TunnelBase tunnelBase = new TunnelBase();
        InterfaceVO vo = dbf.findByUuid(msg.getUuid(), InterfaceVO.class);

        if(tunnelBase.isShareForInterface(vo.getUuid())){
            APIGetUnscribeProductPriceDiffReply reply = new APIGetUnscribeProductPriceDiffReply();
            reply.setInventory(BigDecimal.ZERO);
            reply.setReFoundMoney(BigDecimal.ZERO);

            bus.reply(msg, new APIGetUnscribeInterfacePriceDiffReply(reply));
        }else{
            APIGetUnscribeProductPriceDiffMsg upmsg = new APIGetUnscribeProductPriceDiffMsg();

            upmsg.setAccountUuid(vo.getAccountUuid());
            upmsg.setProductUuid(msg.getUuid());
            if (vo.getExpireDate() == null) {
                upmsg.setExpiredTime(dbf.getCurrentSqlTime());
                upmsg.setCreateFailure(true);
            } else {
                upmsg.setExpiredTime(vo.getExpireDate());
            }

            APIGetUnscribeProductPriceDiffReply reply = new BillingRESTCaller().syncJsonPost(upmsg);

            bus.reply(msg, new APIGetUnscribeInterfacePriceDiffReply(reply));
        }

    }

    /**
     * 续费时查询物理接口价格
     */
    private void handle(APIGetRenewInterfacePriceMsg msg) {

        InterfaceVO vo = dbf.findByUuid(msg.getUuid(), InterfaceVO.class);

        APIGetRenewProductPriceMsg rpmsg = new APIGetRenewProductPriceMsg();

        rpmsg.setAccountUuid(vo.getAccountUuid());
        rpmsg.setProductUuid(msg.getUuid());
        rpmsg.setDuration(msg.getDuration());
        rpmsg.setProductChargeModel(msg.getProductChargeModel());

        APIGetRenewProductPriceReply priceReply = new BillingRESTCaller().syncJsonPost(rpmsg);

        bus.reply(msg, new APIGetRenewInterfacePriceReply(priceReply));
    }

    /**
     * 续费时查询云专线价格
     */
    private void handle(APIGetRenewTunnelPriceMsg msg) {

        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);

        APIGetRenewProductPriceMsg rpmsg = new APIGetRenewProductPriceMsg();

        rpmsg.setAccountUuid(vo.getOwnerAccountUuid());
        rpmsg.setProductUuid(msg.getUuid());
        rpmsg.setDuration(msg.getDuration());
        rpmsg.setProductChargeModel(msg.getProductChargeModel());

        APIGetRenewProductPriceReply priceReply = new BillingRESTCaller().syncJsonPost(rpmsg);

        bus.reply(msg, new APIGetRenewTunnelPriceReply(priceReply));
    }

    /**
     * 购买时查询云专线价格
     */
    private void handle(APIGetTunnelPriceMsg msg) {
        EndpointVO evoA = dbf.findByUuid(msg.getEndpointAUuid(),EndpointVO.class);
        EndpointVO evoZ = dbf.findByUuid(msg.getEndpointZUuid(),EndpointVO.class);

        APIGetProductPriceMsg pmsg = new APIGetProductPriceMsg();
        pmsg.setProductChargeModel(msg.getProductChargeModel());
        pmsg.setDuration(msg.getDuration());
        pmsg.setAccountUuid(msg.getAccountUuid());
        pmsg.setUnits(new TunnelBillingBase().getTunnelPriceUnit(msg.getBandwidthOfferingUuid(), msg.getInterfaceAUuid(), msg.getInterfaceZUuid(), evoA, evoZ, msg.getInnerEndpointUuid()));
        APIGetProductPriceReply reply = new BillingRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(pmsg);
        bus.reply(msg, new APIGetTunnelPriceReply(reply));
    }

    /**
     * 调整带宽时查询云专线差价
     */
    private void handle(APIGetModifyTunnelPriceDiffMsg msg) {
        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);
        TunnelSwitchPortVO tunnelSwitchPortVOA = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "A")
                .find();
        TunnelSwitchPortVO tunnelSwitchPortVOZ = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, msg.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "Z")
                .find();

        EndpointVO endpointVOA = dbf.findByUuid(tunnelSwitchPortVOA.getEndpointUuid(), EndpointVO.class);
        EndpointVO endpointVOZ = dbf.findByUuid(tunnelSwitchPortVOZ.getEndpointUuid(), EndpointVO.class);

        APIGetModifyProductPriceDiffMsg pmsg = new APIGetModifyProductPriceDiffMsg();
        pmsg.setUnits(new TunnelBillingBase().getTunnelPriceUnit(msg.getBandwidthOfferingUuid(), tunnelSwitchPortVOA.getInterfaceUuid(), tunnelSwitchPortVOZ.getInterfaceUuid(), endpointVOA, endpointVOZ, vo.getInnerEndpointUuid()));
        pmsg.setProductUuid(msg.getUuid());
        pmsg.setAccountUuid(vo.getOwnerAccountUuid());
        pmsg.setExpiredTime(dbf.findByUuid(msg.getUuid(), TunnelVO.class).getExpireDate());

        APIGetModifyProductPriceDiffReply reply = new BillingRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(pmsg);
        bus.reply(msg, new APIGetModifyTunnelPriceDiffReply(reply));
    }

    /**
     * 退订时查询云专线差价
     */
    private void handle(APIGetUnscribeTunnelPriceDiffMsg msg) {
        TunnelVO vo = dbf.findByUuid(msg.getUuid(), TunnelVO.class);

        if (vo.getAccountUuid() == null) {
            APIGetUnscribeProductPriceDiffReply reply = new APIGetUnscribeProductPriceDiffReply();
            reply.setInventory(BigDecimal.ZERO);
            reply.setReFoundMoney(BigDecimal.ZERO);
            bus.reply(msg, reply);
            return;
        }

        APIGetUnscribeProductPriceDiffMsg upmsg = new APIGetUnscribeProductPriceDiffMsg();
        upmsg.setAccountUuid(vo.getOwnerAccountUuid());
        upmsg.setProductUuid(msg.getUuid());
        if (vo.getExpireDate() == null) {
            upmsg.setExpiredTime(dbf.getCurrentSqlTime());
            upmsg.setCreateFailure(true);
        } else {
            upmsg.setExpiredTime(vo.getExpireDate());
        }

        APIGetUnscribeProductPriceDiffReply reply = new BillingRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(upmsg);
        bus.reply(msg, new APIGetUnscribeTunnelPriceDiffReply(reply));

    }

    /**
     * Alarm查询云专线信息
     */
    private void handle(APIQueryTunnelDetailForAlarmMsg msg) {
        TunnelBase tunnelBase = new TunnelBase();
        Map map = new HashMap();

        for (String tunnelUuid : msg.getTunnelUuidList()) {
            TunnelVO tunnel = Q.New(TunnelVO.class).eq(TunnelVO_.uuid, tunnelUuid).findValue();

            FalconApiCommands.Tunnel tunnelCmd = new FalconApiCommands.Tunnel();
            if (tunnel == null)
                throw new IllegalArgumentException(String.format("tunnel %s not exist!", tunnelUuid));

            tunnelCmd.setTunnel_id(tunnel.getUuid());
            tunnelCmd.setBandwidth(tunnel.getBandwidth());
            tunnelCmd.setUser_id(tunnel.getOwnerAccountUuid());
            tunnelCmd.setRules(null);

            List<TunnelSwitchPortVO> tunnelSwitchPortVOS = Q.New(TunnelSwitchPortVO.class).eq(TunnelSwitchPortVO_.tunnelUuid, tunnelUuid).list();
            for (TunnelSwitchPortVO vo : tunnelSwitchPortVOS) {
                if ("A".equals(vo.getSortTag())) {
                    tunnelCmd.setEndpointA_ip(tunnelBase.getPhysicalSwitchMip(vo.getSwitchPortUuid()));
                    tunnelCmd.setEndpointA_vid(vo.getVlan());
                } else if ("Z".equals(vo.getSortTag())) {
                    tunnelCmd.setEndpointB_ip(tunnelBase.getPhysicalSwitchMip(vo.getSwitchPortUuid()));
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
    private void handle(APIListTraceRouteMsg msg) {
        APIListTraceRouteReply traceRouteReply = new APIListTraceRouteReply();
        List<TraceRouteVO> traceRouteVOS = Q.New(TraceRouteVO.class)
                .eq(TraceRouteVO_.tunnelUuid, msg.getTunnelUuid())
                .orderBy(TraceRouteVO_.traceSort, SimpleQuery.Od.ASC)
                .list();
        if (traceRouteVOS.isEmpty() || msg.isTraceAgain()) {
            ListTraceRouteMsg listTraceRouteMsg = new ListTraceRouteMsg();
            listTraceRouteMsg.setTunnelUuid(msg.getTunnelUuid());

            bus.makeLocalServiceId(listTraceRouteMsg, TunnelConstant.SERVICE_ID);
            bus.send(listTraceRouteMsg, new CloudBusCallBack(null) {
                @Override
                public void run(MessageReply reply) {
                    if (reply.isSuccess()) {
                        if (!traceRouteVOS.isEmpty() && msg.isTraceAgain()) {
                            UpdateQuery.New(TraceRouteVO.class)
                                    .eq(TraceRouteVO_.tunnelUuid, msg.getTunnelUuid())
                                    .delete();
                        }
                        ListTraceRouteReply listTraceRouteReply = reply.castReply();
                        List<List<String>> results = listTraceRouteReply.getMsg();
                        for (List<String> result : results) {
                            for (String s : result) {
                                s = s.trim().replaceAll("\\s+ms", "ms");
                                String[] arrS = s.split("\\s+");
                                if (!arrS[1].equals("*")) {
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
                                .eq(TraceRouteVO_.tunnelUuid, msg.getTunnelUuid())
                                .orderBy(TraceRouteVO_.traceSort, SimpleQuery.Od.ASC)
                                .list();
                        traceRouteReply.setInventories(TraceRouteInventory.valueOf(traceRouteList));
                        bus.reply(msg, traceRouteReply);
                    } else {
                        traceRouteReply.setError(reply.getError());
                        bus.reply(msg, traceRouteReply);
                    }
                }
            });

        } else {
            traceRouteReply.setInventories(TraceRouteInventory.valueOf(traceRouteVOS));
            bus.reply(msg, traceRouteReply);
        }

    }

    /**
     * 调整带宽的次数查询
     */
    private void handle(APIGetModifyBandwidthNumMsg msg) {
        LocalDateTime dateTime =
                LocalDate.now().withDayOfMonth(LocalDate.MIN.getDayOfMonth()).atTime(LocalTime.MIN);
        Long times = Q.New(ResourceMotifyRecordVO.class).eq(ResourceMotifyRecordVO_.resourceUuid, msg.getUuid())
                .gte(ResourceMotifyRecordVO_.createDate, Timestamp.valueOf(dateTime)).count();
        Integer maxModifies =
                Q.New(TunnelVO.class).eq(TunnelVO_.uuid, msg.getUuid()).select(TunnelVO_.maxModifies)
                        .findValue();
        APIGetModifyBandwidthNumReply reply = new APIGetModifyBandwidthNumReply();
        reply.setMaxModifies(maxModifies);
        reply.setHasModifies(Math.toIntExact(times));
        reply.setLeftModifies((int) (maxModifies - times));

        bus.reply(msg, reply);
    }

    /**
     * 数据迁移--跑 tunnelType
     */
    private void handle(APIRunDataForTunnelTypeMsg msg){
        TunnelBase tunnelBase = new TunnelBase();
        APIRunDataForTunnelTypeReply reply = new APIRunDataForTunnelTypeReply();

        List<TunnelVO> tunnelVOS = Q.New(TunnelVO.class)
                .notEq(TunnelVO_.type, TunnelType.CHINA1ABROAD)
                .list();
        for(TunnelVO tunnelVO : tunnelVOS){
            String endpointUuidA = Q.New(TunnelSwitchPortVO.class)
                    .eq(TunnelSwitchPortVO_.tunnelUuid, tunnelVO.getUuid())
                    .eq(TunnelSwitchPortVO_.sortTag, "A")
                    .select(TunnelSwitchPortVO_.endpointUuid)
                    .findValue();
            String endpointUuidZ = Q.New(TunnelSwitchPortVO.class)
                    .eq(TunnelSwitchPortVO_.tunnelUuid, tunnelVO.getUuid())
                    .eq(TunnelSwitchPortVO_.sortTag, "Z")
                    .select(TunnelSwitchPortVO_.endpointUuid)
                    .findValue();
            EndpointVO evoA = dbf.findByUuid(endpointUuidA, EndpointVO.class);
            EndpointVO evoZ = dbf.findByUuid(endpointUuidZ, EndpointVO.class);
            NodeVO nvoA = dbf.findByUuid(evoA.getNodeUuid(), NodeVO.class);
            NodeVO nvoZ = dbf.findByUuid(evoZ.getNodeUuid(), NodeVO.class);

            TunnelType tunnelType = tunnelBase.getTunnelType(nvoA, nvoZ, null);

            tunnelVO.setType(tunnelType);

            dbf.updateAndRefresh(tunnelVO);
        }

        bus.reply(msg, reply);
    }

    /**
     * 数据迁移--下发ZK
     */
    private void handle(APIRunDataForTunnelZKMsg msg){
        APIRunDataForTunnelZKEvent evt = new APIRunDataForTunnelZKEvent(msg.getId());
        TunnelJobAndTaskBase taskBase = new TunnelJobAndTaskBase();

        List<String> uuids = Q.New(TunnelVO.class)
                .eq(TunnelVO_.state, TunnelState.Enabled)
                .select(TunnelVO_.uuid)
                .listValues();
        for(String uuid : uuids){
            taskBase.taskCreateTunnelZK(uuid);
        }

        bus.publish(evt);
    }

    /**
     * 数据修正-修改资源UUID
     * */
    private void handle(APIUpdateResourceUuidMsg msg){
        APIUpdateResourceUuidReply reply = new APIUpdateResourceUuidReply();
        //修改最后一公里
        List<EdgeLineVO> edgeLineVOS = Q.New(EdgeLineVO.class).lt(EdgeLineVO_.createDate, Timestamp.valueOf("2018-03-03 11:11:11")).list();
        for (EdgeLineVO edgeLineVO : edgeLineVOS){
            edgeLineVO.setUuid(Platform.getUuid());
            dbf.updateAndRefresh(edgeLineVO);
        }

        //修改物理接口
        List<String> interfaceUuids = Q.New(InterfaceVO.class).select(InterfaceVO_.uuid).listValues();
        List<String> sameUuids = Q.New(TunnelVO.class).in(TunnelVO_.uuid, interfaceUuids).listValues();

        for(String sameUUid : sameUuids){
            InterfaceVO interfaceVO = dbf.findByUuid(sameUUid, InterfaceVO.class);
            String newInterfaceUuid = Platform.getUuid();
            interfaceVO.setUuid(newInterfaceUuid);
            dbf.updateAndRefresh(interfaceVO);

            UpdateQuery.New(EdgeLineVO.class)
                    .set(EdgeLineVO_.interfaceUuid, newInterfaceUuid)
                    .eq(EdgeLineVO_.interfaceUuid, sameUUid)
                    .update();
            UpdateQuery.New(TunnelSwitchPortVO.class)
                    .set(TunnelSwitchPortVO_.interfaceUuid, newInterfaceUuid)
                    .eq(TunnelSwitchPortVO_.interfaceUuid, sameUUid)
                    .update();
        }

        bus.reply(msg, reply);
    }

    /**************************************** The following clean the expired Products **************************************************/

    private Future<Void> cleanExpiredProductThread = null;
    private int cleanExpiredProductInterval;
    private int expiredProductCloseTime;
    private int expiredProductDeleteTime;

    private void startCleanExpiredProduct() {
        cleanExpiredProductInterval = TunnelGlobalConfig.CLEAN_EXPIRED_PRODUCT_INTERVAL.value(Integer.class);
        expiredProductCloseTime = TunnelGlobalConfig.EXPIRED_PRODUCT_CLOSE_TIME.value(Integer.class);
        expiredProductDeleteTime = TunnelGlobalConfig.EXPIRED_PRODUCT_DELETE_TIME.value(Integer.class);

        if (cleanExpiredProductThread != null) {
            cleanExpiredProductThread.cancel(true);
        }

        cleanExpiredProductThread = thdf.submitPeriodicTask(new CleanExpiredProductThread(), TimeUnit.SECONDS.toSeconds(3600));
        logger.debug(String
                .format("security group cleanExpiredProductThread starts[cleanExpiredProductInterval: %s hours]", cleanExpiredProductInterval));
    }

    private void restartCleanExpiredProduct() {

        startCleanExpiredProduct();

        TunnelGlobalConfig.CLEAN_EXPIRED_PRODUCT_INTERVAL.installUpdateExtension(new GlobalConfigUpdateExtensionPoint() {
            @Override
            public void updateGlobalConfig(GlobalConfig oldConfig, GlobalConfig newConfig) {
                logger.debug(String.format("%s change from %s to %s, restart tracker thread",
                        oldConfig.getCanonicalName(), oldConfig.value(), newConfig.value()));
                startCleanExpiredProduct();
            }
        });
        TunnelGlobalConfig.EXPIRED_PRODUCT_CLOSE_TIME.installUpdateExtension(new GlobalConfigUpdateExtensionPoint() {
            @Override
            public void updateGlobalConfig(GlobalConfig oldConfig, GlobalConfig newConfig) {
                logger.debug(String.format("%s change from %s to %s, restart tracker thread",
                        oldConfig.getCanonicalName(), oldConfig.value(), newConfig.value()));
                startCleanExpiredProduct();
            }
        });
        TunnelGlobalConfig.EXPIRED_PRODUCT_DELETE_TIME.installUpdateExtension(new GlobalConfigUpdateExtensionPoint() {
            @Override
            public void updateGlobalConfig(GlobalConfig oldConfig, GlobalConfig newConfig) {
                logger.debug(String.format("%s change from %s to %s, restart tracker thread",
                        oldConfig.getCanonicalName(), oldConfig.value(), newConfig.value()));
                startCleanExpiredProduct();
            }
        });
    }

    private class CleanExpiredProductThread implements PeriodicTask {

        @Override
        public TimeUnit getTimeUnit() {
            return TimeUnit.SECONDS;
        }

        @Override
        public long getInterval() {
            return TimeUnit.HOURS.toSeconds(cleanExpiredProductInterval);
        }

        @Override
        public String getName() {
            return "clean-expired-product-" + Platform.getManagementServerId();
        }

        private Timestamp getCloseTime(){
            Timestamp time = Timestamp.valueOf(dbf.getCurrentSqlTime().toLocalDateTime().minusDays(expiredProductCloseTime < expiredProductDeleteTime ? expiredProductCloseTime : expiredProductDeleteTime));
            return time;
        }

        private List<TunnelVO> getTunnels() {
            return Q.New(TunnelVO.class)
                    .lte(TunnelVO_.expireDate, getCloseTime())
                    .list();
        }

        private List<InterfaceVO> getInterfaces() {

            return Q.New(InterfaceVO.class)
                    .lte(InterfaceVO_.expireDate, getCloseTime())
                    .list();
        }

        private void deleteInterface(List<InterfaceVO> ifaces, Timestamp close, Timestamp delete) {

            for (InterfaceVO vo : ifaces) {
                if (vo.getExpireDate().before(delete)) {
                    if (!Q.New(TunnelSwitchPortVO.class).eq(TunnelSwitchPortVO_.interfaceUuid, vo.getUuid()).isExists()
                            && !Q.New(EdgeLineVO.class).eq(EdgeLineVO_.interfaceUuid, vo.getUuid()).isExists()) {
                        dbf.remove(vo);
                        //删除续费表
                        logger.info("删除接口成功，并创建任务：DeleteRenewVOAfterDeleteResourceJob");
                        DeleteRenewVOAfterDeleteResourceJob job = new DeleteRenewVOAfterDeleteResourceJob();
                        job.setAccountUuid(vo.getOwnerAccountUuid());
                        job.setResourceType(vo.getClass().getSimpleName());
                        job.setResourceUuid(vo.getUuid());
                        jobf.execute("删除物理接口-删除续费表", Platform.getManagementServerId(), job);
                    }

                }else if (vo.getExpireDate().before(close) && vo.getState() == InterfaceState.Unpaid) {
                    dbf.remove(vo);
                }
            }
        }

        @Override
        public void run() {
            if (!TunnelGlobalConfig.EXPIRED_PRODUCT_CLEAN_RUN.value(Boolean.class)){
                return;
            }

            TunnelBase tunnelBase = new TunnelBase();
            Timestamp closeTime = Timestamp.valueOf(LocalDateTime.now().minusDays(expiredProductCloseTime));
            Timestamp deleteTime = Timestamp.valueOf(LocalDateTime.now().minusDays(expiredProductDeleteTime));

            try {
                List<TunnelVO> tunnelVOs = getTunnels();
                logger.debug("delete expired tunnel.");
                if (tunnelVOs.isEmpty())
                    return;
                for (TunnelVO vo : tunnelVOs) {
                    if (vo.getExpireDate().before(deleteTime)) {
                        vo.setAccountUuid(null);
                        final TunnelVO vo2 = dbf.updateAndRefresh(vo);
                        doDeleteTunnel(vo2, false, vo.getOwnerAccountUuid(), new ReturnValueCompletion<TunnelInventory>(null) {
                            @Override
                            public void success(TunnelInventory inv) {
                            }

                            @Override
                            public void fail(ErrorCode errorCode) {
                            }
                        });
                    } else if (vo.getExpireDate().before(closeTime)) {
                        if (vo.getState() == TunnelState.Unpaid) {
                            tunnelBase.deleteTunnelDB(vo);
                        } else if (vo.getState() == TunnelState.Enabled) {
                            new TunnelJobAndTaskBase().taskDisableTunnel(vo,new ReturnValueCompletion<TunnelInventory>(null) {
                                @Override
                                public void success(TunnelInventory inv) {
                                }

                                @Override
                                public void fail(ErrorCode errorCode) {
                                }
                            });
                        }
                    }
                }

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
    public boolean start() {
        restartCleanExpiredProduct();
        return true;
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
        } else if (msg instanceof APIOpenTunnelMsg) {
            tunnelValidateBase.validate((APIOpenTunnelMsg) msg);
        } else if (msg instanceof APIUnsupportTunnelMsg) {
            tunnelValidateBase.validate((APIUnsupportTunnelMsg) msg);
        } else if (msg instanceof APIEnableTunnelMsg) {
            tunnelValidateBase.validate((APIEnableTunnelMsg) msg);
        } else if (msg instanceof APIDisableTunnelMsg) {
            tunnelValidateBase.validate((APIDisableTunnelMsg) msg);
        } else if (msg instanceof APIUpdateQinqMsg) {
            tunnelValidateBase.validate((APIUpdateQinqMsg) msg);
        } else if (msg instanceof APIUpdateTunnelVlanMsg) {
            tunnelValidateBase.validate((APIUpdateTunnelVlanMsg) msg);
        } else if (msg instanceof APIUpdateInterfacePortMsg) {
            tunnelValidateBase.validate((APIUpdateInterfacePortMsg) msg);
        } else if (msg instanceof APIGetVlanAutoMsg) {
            tunnelValidateBase.validate((APIGetVlanAutoMsg) msg);
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

    @Override
    public void preDelete(TunnelVO vo) {
        if (vo.getState() != TunnelState.Unsupport && vo.getState() != TunnelState.Deployfailure) {
            logger.info("【询问该专线是否被互联云占用】");

            Map<String, String> bodyMap = new HashMap<>();
            bodyMap.put("tunnelUuid", vo.getUuid());

            Map<String, String> headers = new HashMap<>();
            headers.put("commandPath", "tunnelCheck");

            Map rsp = restf.syncJsonPost(CoreGlobalProperty.ECP_SERVER_URL, JSONObjectUtil.toJsonString(bodyMap), headers, Map.class);

            if ((Boolean) rsp.get("success")) {
                if ((Boolean) rsp.get("isOccupied")) {
                    throw new ApiMessageInterceptionException(
                            argerr("该专线[uuid:%s]被互联云占用！", vo.getUuid()));
                }
            } else {
                throw new ApiMessageInterceptionException(
                        argerr("询问专线占用情况失败！"));
            }


            logger.info("【询问该专线是否被VPN占用】");
            APICheckVpnForTunnelMsg msg = new APICheckVpnForTunnelMsg();
            msg.setTunnelUuid(vo.getUuid());
            String url = URLBuilder.buildUrlFromBase(CoreGlobalProperty.VPN_SERVER_URL);
            InnerMessageHelper.setMD5(msg);

            RestAPIResponse restAPIResponse = restf.syncJsonPost(url, RESTApiDecoder.dump(msg), RestAPIResponse.class);
            APIReply reply = (APIReply) RESTApiDecoder.loads(restAPIResponse.getResult());

            if (!reply.isSuccess()) {
                throw new ApiMessageInterceptionException(
                        argerr("询问该专线[uuid:%s]是否被VPN占用失败！", vo.getUuid()));
            } else {
                APICheckVpnForTunnelReply apiCheckVpnForTunnelReply = reply.castReply();
                if (apiCheckVpnForTunnelReply.isUsed()) {
                    throw new ApiMessageInterceptionException(
                            argerr("该专线[uuid:%s]被VPN占用！", vo.getUuid()));
                }
            }

        }
    }

    @Override
    public void beforeDelete(TunnelVO vo) {
        logger.info("【删除专线对应的互联云】");
        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("tunnelUuid", vo.getUuid());

        Map<String, String> headers = new HashMap<>();
        headers.put("commandPath", "tunnelDelete");

        Map rsp = restf.syncJsonPost(CoreGlobalProperty.ECP_SERVER_URL, JSONObjectUtil.toJsonString(bodyMap), headers, Map.class);

        if (!(Boolean) rsp.get("result"))
            throw new ApiMessageInterceptionException(
                    argerr("该专线[uuid:%s]互联云删除失败！", vo.getUuid()));


        logger.info("【删除专线对应的VPN】");
        APIDestroyVpnMsg msg = new APIDestroyVpnMsg();
        msg.setTunnelUuid(vo.getUuid());
        String url = URLBuilder.buildUrlFromBase(CoreGlobalProperty.VPN_SERVER_URL);
        InnerMessageHelper.setMD5(msg);

        RestAPIResponse restAPIResponse = restf.syncJsonPost(url, RESTApiDecoder.dump(msg), RestAPIResponse.class);
        APIReply reply = (APIReply) RESTApiDecoder.loads(restAPIResponse.getResult());

        if (!reply.isSuccess()) {
            throw new ApiMessageInterceptionException(
                    argerr("该专线[uuid:%s]VPN删除失败！", vo.getUuid()));
        }
    }

    @Override
    public void afterDelete() {
    }
}
