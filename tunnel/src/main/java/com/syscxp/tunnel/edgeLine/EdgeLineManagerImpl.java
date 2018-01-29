package com.syscxp.tunnel.edgeLine;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.job.JobQueueFacade;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.billing.*;
import com.syscxp.header.identity.AccountType;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.tunnel.EdgeLineConstant;
import com.syscxp.header.tunnel.billingCallBack.*;
import com.syscxp.header.tunnel.edgeLine.*;
import com.syscxp.header.tunnel.tunnel.*;
import com.syscxp.tunnel.tunnel.TunnelBillingBase;
import com.syscxp.tunnel.tunnel.TunnelRESTCaller;
import com.syscxp.tunnel.tunnel.TunnelValidateBase;
import com.syscxp.tunnel.tunnel.job.DeleteRenewVOAfterDeleteResourceJob;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.syscxp.core.Platform.argerr;

/**
 * Create by DCY on 2018/1/29
 */
public class EdgeLineManagerImpl extends AbstractService implements EdgeLineManager, ApiMessageInterceptor {
    private static final CLogger logger = Utils.getLogger(EdgeLineManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private RESTFacade restf;
    @Autowired
    private ErrorFacade errf;
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

    }

    private void handleApiMessage(APIMessage msg) {

        if (msg instanceof APICreateEdgeLineMsg) {
            handle((APICreateEdgeLineMsg) msg);
        } else if (msg instanceof APIUpdateEdgeLineMsg) {
            handle((APIUpdateEdgeLineMsg) msg);
        } else if (msg instanceof APIOpenEdgeLineMsg) {
            handle((APIOpenEdgeLineMsg) msg);
        } else if (msg instanceof APIRenewEdgeLineMsg) {
            handle((APIRenewEdgeLineMsg) msg);
        } else if (msg instanceof APIRenewAutoEdgeLineMsg) {
            handle((APIRenewAutoEdgeLineMsg) msg);
        } else if (msg instanceof APISLAEdgeLineMsg) {
            handle((APISLAEdgeLineMsg) msg);
        } else if (msg instanceof APIGetUnscribeEdgeLinePriceDiffMsg) {
            handle((APIGetUnscribeEdgeLinePriceDiffMsg) msg);
        } else if (msg instanceof APIDeleteEdgeLineMsg) {
            handle((APIDeleteEdgeLineMsg) msg);
        }  else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    /**
     * 创建（申请）最后一公里
     * */
    private void handle(APICreateEdgeLineMsg msg){
        APICreateEdgeLineEvent evt = new APICreateEdgeLineEvent(msg.getId());

        EdgeLineVO vo = new EdgeLineVO();
        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(msg.getAccountUuid());
        vo.setInterfaceUuid(msg.getInterfaceUuid());
        vo.setEndpointUuid(dbf.findByUuid(msg.getInterfaceUuid(), InterfaceVO.class).getEndpointUuid());
        vo.setType(msg.getType());
        vo.setDestinationInfo(msg.getDestinationInfo());
        vo.setDescription(msg.getDescription());
        vo.setState(EdgeLineState.Applying);
        vo.setPrices(null);
        vo.setExpireDate(null);

        vo = dbf.persistAndRefresh(vo);
        evt.setInventory(EdgeLineInventory.valueOf(vo));
        bus.publish(evt);
    }

    /**
     * 修改最后一公里
     * */
    private void handle(APIUpdateEdgeLineMsg msg){
        APIUpdateEdgeLineEvent evt = new APIUpdateEdgeLineEvent(msg.getId());

        EdgeLineVO vo = dbf.findByUuid(msg.getUuid(), EdgeLineVO.class);
        boolean update = false;
        if(msg.getDescription() != null){
            vo.setDescription(msg.getDescription());
            update = true;
        }
        if(msg.getDestinationInfo() != null){
            vo.setDestinationInfo(msg.getDestinationInfo());
            update = true;
        }
        if(msg.getPrices() != null){
            vo.setPrices(msg.getPrices());
            update = true;
        }
        if(msg.getType() != null){
            vo.setType(msg.getType());
            update = true;
        }

        if(update)
            vo = dbf.updateAndRefresh(vo);

        evt.setInventory(EdgeLineInventory.valueOf(vo));
        bus.publish(evt);
    }

    /**
     * 开通最后一公里
     * */
    private void handle(APIOpenEdgeLineMsg msg){
        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();
        APIOpenEdgeLineEvent evt = new APIOpenEdgeLineEvent(msg.getId());
        EdgeLineVO vo = dbf.findByUuid(msg.getUuid(), EdgeLineVO.class);

        //订单信息
        APICreateBuyEdgeLineOrderMsg orderMsg = new APICreateBuyEdgeLineOrderMsg();
        orderMsg.setPrice(vo.getPrices());
        orderMsg.setProductName("最后一公里-"+vo.getInterfaceVO().getName());
        orderMsg.setProductUuid(vo.getUuid());
        orderMsg.setDescriptionData(tunnelBillingBase.getDescriptionForEdgeLine(vo));
        orderMsg.setCallBackData(RESTApiDecoder.dump(new CreateEdgeLineCallBack()));
        orderMsg.setAccountUuid(vo.getAccountUuid());
        orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
        orderMsg.setNotifyUrl(restf.getSendCommandUrl());
        orderMsg.setProductChargeModel(msg.getProductChargeModel());
        orderMsg.setDuration(msg.getDuration());

        //支付
        OrderInventory orderInventory = tunnelBillingBase.createOrderForEdgeLine(orderMsg);
        if (orderInventory != null) {
            //开通成功,记录生效订单
            tunnelBillingBase.saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());

            vo.setExpireDate(tunnelBillingBase.getExpireDate(dbf.getCurrentSqlTime(),msg.getProductChargeModel(),msg.getDuration()));
            vo.setState(EdgeLineState.Opened);
            vo = dbf.updateAndRefresh(vo);

            InterfaceVO interfaceVO = dbf.findByUuid(vo.getInterfaceUuid(), InterfaceVO.class);
            interfaceVO.setState(InterfaceState.Up);

            if(interfaceVO.getExpireDate() == null){
                interfaceVO.setExpireDate(tunnelBillingBase.getExpireDate(dbf.getCurrentSqlTime(),interfaceVO.getProductChargeModel(),interfaceVO.getDuration()));
            }

            dbf.updateAndRefresh(interfaceVO);

            evt.setInventory(EdgeLineInventory.valueOf(vo));
            bus.publish(evt);
        } else {
            evt.setError(errf.stringToOperationError("付款失败"));
            bus.publish(evt);
        }
    }

    /**
     * 最后一公里续费
     **/
    private void handle(APIRenewEdgeLineMsg msg) {
        EdgeLineVO vo = dbf.findByUuid(msg.getUuid(), EdgeLineVO.class);
        APIRenewEdgeLineReply reply
                = renewEdgeLine(msg.getUuid(),
                msg.getDuration(),
                msg.getProductChargeModel(),
                vo.getAccountUuid(),
                msg.getSession().getAccountUuid(),false);

        bus.reply(msg, reply);
    }

    /**
     * 最后一公里自动续费
     **/
    private void handle(APIRenewAutoEdgeLineMsg msg) {
        EdgeLineVO vo = dbf.findByUuid(msg.getUuid(), EdgeLineVO.class);
        APIRenewEdgeLineReply reply
                = renewEdgeLine(msg.getUuid(),
                msg.getDuration(),
                msg.getProductChargeModel(),
                vo.getAccountUuid(),
                "system",true);

        bus.reply(msg, reply);
    }

    private APIRenewEdgeLineReply renewEdgeLine(String uuid,
                                                Integer duration,
                                                ProductChargeModel productChargeModel,
                                                String accountUuid,
                                                String opAccountUuid,boolean isAutoRenew) {

        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();
        APIRenewEdgeLineReply reply = new APIRenewEdgeLineReply();

        EdgeLineVO vo = dbf.findByUuid(uuid, EdgeLineVO.class);
        Timestamp newTime = vo.getExpireDate();

        //续费
        RenewEdgeLineCallBack rc = new RenewEdgeLineCallBack();
        APICreateRenewOrderMsg renewOrderMsg = new APICreateRenewOrderMsg();
        renewOrderMsg.setProductUuid(vo.getUuid());
        renewOrderMsg.setProductName("最后一公里-"+vo.getInterfaceVO().getName());
        renewOrderMsg.setProductType(ProductType.EDGELINE);
        renewOrderMsg.setDuration(duration);
        renewOrderMsg.setDescriptionData(tunnelBillingBase.getDescriptionForEdgeLine(vo));
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
            vo.setExpireDate(tunnelBillingBase.getExpireDate(newTime, productChargeModel, duration));

            vo = dbf.updateAndRefresh(vo);
            reply.setInventory(EdgeLineInventory.valueOf(vo));
        } else {
            reply.setError(errf.stringToOperationError("订单操作失败"));
        }

        return reply;
    }

    /**
     * 最后一公里赔偿
     **/
    private void handle(APISLAEdgeLineMsg msg) {
        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();

        APISLAEdgeLineReply reply = new APISLAEdgeLineReply();

        EdgeLineVO vo = dbf.findByUuid(msg.getUuid(), EdgeLineVO.class);
        Timestamp newTime = vo.getExpireDate();

        //赔偿
        SalEdgeLineCallBack sc = new SalEdgeLineCallBack();
        APICreateSLACompensationOrderMsg slaCompensationOrderMsg =
                new APICreateSLACompensationOrderMsg();
        slaCompensationOrderMsg.setSlaUuid(msg.getSlaUuid());
        slaCompensationOrderMsg.setProductUuid(vo.getUuid());
        slaCompensationOrderMsg.setProductName("最后一公里-"+vo.getInterfaceVO().getName());
        slaCompensationOrderMsg.setDescriptionData(tunnelBillingBase.getDescriptionForEdgeLine(vo));
        slaCompensationOrderMsg.setProductType(ProductType.EDGELINE);
        slaCompensationOrderMsg.setDuration(msg.getDuration());
        slaCompensationOrderMsg.setAccountUuid(vo.getAccountUuid());
        slaCompensationOrderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
        slaCompensationOrderMsg.setStartTime(dbf.getCurrentSqlTime());
        slaCompensationOrderMsg.setExpiredTime(vo.getExpireDate());
        slaCompensationOrderMsg.setCallBackData(RESTApiDecoder.dump(sc));
        slaCompensationOrderMsg.setNotifyUrl(restf.getSendCommandUrl());

        OrderInventory orderInventory = tunnelBillingBase.createOrder(slaCompensationOrderMsg);

        if (orderInventory != null) {
            //赔偿成功,记录生效订单
            tunnelBillingBase.saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());
            //更新到期时间
            vo.setExpireDate(tunnelBillingBase.getExpireDate(newTime, ProductChargeModel.BY_DAY, msg.getDuration()));

            vo = dbf.updateAndRefresh(vo);
            reply.setInventory(EdgeLineInventory.valueOf(vo));
        } else {
            reply.setError(errf.stringToOperationError("订单操作失败"));
        }

        bus.reply(msg, reply);
    }

    /**
     * 最后一公里的退订和删除
     * */
    private void handle(APIDeleteEdgeLineMsg msg){
        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();
        APIDeleteEdgeLineEvent evt = new APIDeleteEdgeLineEvent(msg.getId());

        EdgeLineVO vo = dbf.findByUuid(msg.getUuid(), EdgeLineVO.class);

        if(vo.getExpireDate() ==null || (vo.getExpireDate() != null && !vo.getExpireDate().after(Timestamp.valueOf(LocalDateTime.now())))){
            dbf.remove(vo);

            InterfaceVO interfaceVO = dbf.findByUuid(vo.getInterfaceUuid(), InterfaceVO.class);
            interfaceVO.setState(InterfaceState.Down);
            dbf.updateAndRefresh(interfaceVO);

            //删除续费表
            logger.info("删除最后一公里成功，并创建任务：DeleteRenewVOAfterDeleteResourceJob");
            DeleteRenewVOAfterDeleteResourceJob job = new DeleteRenewVOAfterDeleteResourceJob();
            job.setAccountUuid(vo.getAccountUuid());
            job.setResourceType(vo.getClass().getSimpleName());
            job.setResourceUuid(vo.getUuid());
            jobf.execute("删除最后一公里-删除续费表", Platform.getManagementServerId(), job);

            bus.publish(evt);

        }else{
            //退订
            APICreateUnsubcribeOrderMsg orderMsg = new APICreateUnsubcribeOrderMsg();
            orderMsg.setProductName("最后一公里-"+vo.getInterfaceVO().getName());
            orderMsg.setProductUuid(vo.getUuid());
            orderMsg.setProductType(ProductType.EDGELINE);
            orderMsg.setDescriptionData(tunnelBillingBase.getDescriptionForEdgeLine(vo));
            orderMsg.setAccountUuid(vo.getAccountUuid());
            orderMsg.setCallBackData(RESTApiDecoder.dump(new UnsubcribeEdgeLineCallBack()));
            orderMsg.setNotifyUrl(restf.getSendCommandUrl());
            orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
            orderMsg.setStartTime(dbf.getCurrentSqlTime());
            orderMsg.setExpiredTime(vo.getExpireDate());


            OrderInventory orderInventory = tunnelBillingBase.createOrder(orderMsg);
            if (orderInventory != null) {
                vo.setExpireDate(dbf.getCurrentSqlTime());
                dbf.updateAndRefresh(vo);
                //退订成功,记录生效订单
                tunnelBillingBase.saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());
                //删除产品
                dbf.remove(vo);

                InterfaceVO interfaceVO = dbf.findByUuid(vo.getInterfaceUuid(), InterfaceVO.class);
                interfaceVO.setState(InterfaceState.Down);
                dbf.updateAndRefresh(interfaceVO);

                //删除续费表
                logger.info("删除最后一公里成功，并创建任务：DeleteRenewVOAfterDeleteResourceJob");
                DeleteRenewVOAfterDeleteResourceJob job = new DeleteRenewVOAfterDeleteResourceJob();
                job.setAccountUuid(vo.getAccountUuid());
                job.setResourceType(vo.getClass().getSimpleName());
                job.setResourceUuid(vo.getUuid());
                jobf.execute("删除最后一公里-删除续费表", Platform.getManagementServerId(), job);

                evt.setInventory(EdgeLineInventory.valueOf(vo));
            } else {
                //退订失败
                evt.setError(errf.stringToOperationError("退订失败"));
            }

            bus.publish(evt);
        }

    }

    /**
     * 退订时查询最后一公里差价
     */
    private void handle(APIGetUnscribeEdgeLinePriceDiffMsg msg){
        EdgeLineVO vo = dbf.findByUuid(msg.getUuid(), EdgeLineVO.class);
        APIGetUnscribeProductPriceDiffReply reply = new APIGetUnscribeProductPriceDiffReply();

        if(vo.getExpireDate() == null){
            reply.setReFoundMoney(BigDecimal.ZERO);
        }else{
            APIGetUnscribeProductPriceDiffMsg upmsg = new APIGetUnscribeProductPriceDiffMsg();
            upmsg.setAccountUuid(vo.getAccountUuid());
            upmsg.setProductUuid(msg.getUuid());
            upmsg.setExpiredTime(vo.getExpireDate());

            reply = new TunnelRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(upmsg);
        }

        bus.reply(msg, new APIGetUnscribeEdgeLinePriceDiffReply(reply));
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
    public String getId() {
        return bus.makeLocalServiceId(EdgeLineConstant.SERVICE_ID);
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if (msg instanceof APICreateEdgeLineMsg) {
            validate((APICreateEdgeLineMsg) msg);
        } else if (msg instanceof APIOpenEdgeLineMsg) {
            validate((APIOpenEdgeLineMsg) msg);
        } else if (msg instanceof APIRenewEdgeLineMsg) {
            validate((APIRenewEdgeLineMsg) msg);
        } else if (msg instanceof APIRenewAutoEdgeLineMsg) {
            validate((APIRenewAutoEdgeLineMsg) msg);
        } else if (msg instanceof APISLAEdgeLineMsg) {
            validate((APISLAEdgeLineMsg) msg);
        } else if (msg instanceof APIDeleteEdgeLineMsg) {
            validate((APIDeleteEdgeLineMsg) msg);
        }
        return msg;
    }

    public void validate(APIOpenEdgeLineMsg msg){
        EdgeLineVO vo = dbf.findByUuid(msg.getUuid() ,EdgeLineVO.class);
        if(vo.getPrices() == null){
            throw new ApiMessageInterceptionException(argerr("该最后一公里未设置金额！"));
        }
    }

    public void validate(APICreateEdgeLineMsg msg){
        //BOSS创建验证物理接口的账户是否一致
        if (msg.getSession().getType() == AccountType.SystemAdmin) {
            String accountUuid = dbf.findByUuid(msg.getInterfaceUuid(), InterfaceVO.class).getAccountUuid();
            if (!Objects.equals(msg.getAccountUuid(), accountUuid)) {
                throw new ApiMessageInterceptionException(argerr("物理接口不属于该用户！"));
            }
        }

        if(Q.New(EdgeLineVO.class).eq(EdgeLineVO_.interfaceUuid,msg.getInterfaceUuid()).isExists()){
            throw new ApiMessageInterceptionException(argerr("一个物理接口只能申请开通一次！"));
        }
    }

    public void validate(APIDeleteEdgeLineMsg msg) {
        EdgeLineVO vo = dbf.findByUuid(msg.getUuid(), EdgeLineVO.class);

        if(Q.New(TunnelSwitchPortVO.class).eq(TunnelSwitchPortVO_.interfaceUuid,vo.getInterfaceUuid()).isExists()){
            throw new ApiMessageInterceptionException(argerr("该最后一公里下的接口已经开通专线，不能删！"));
        }

        new TunnelValidateBase().checkOrderNoPay(vo.getAccountUuid(), msg.getUuid());
    }

    public void validate(APIRenewEdgeLineMsg msg){
        String accountUuid = Q.New(EdgeLineVO.class)
                .eq(EdgeLineVO_.uuid, msg.getUuid())
                .select(EdgeLineVO_.accountUuid)
                .findValue();
        new TunnelValidateBase().checkOrderNoPay(accountUuid, msg.getUuid());
    }

    public void validate(APIRenewAutoEdgeLineMsg msg){
        String accountUuid = Q.New(EdgeLineVO.class)
                .eq(EdgeLineVO_.uuid, msg.getUuid())
                .select(EdgeLineVO_.accountUuid)
                .findValue();
        new TunnelValidateBase().checkOrderNoPay(accountUuid, msg.getUuid());
    }

    public void validate(APISLAEdgeLineMsg msg){
        String accountUuid = Q.New(EdgeLineVO.class)
                .eq(EdgeLineVO_.uuid, msg.getUuid())
                .select(EdgeLineVO_.accountUuid)
                .findValue();
        new TunnelValidateBase().checkOrderNoPay(accountUuid, msg.getUuid());
    }
}
