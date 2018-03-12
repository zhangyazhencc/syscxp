package com.syscxp.tunnel.billingCallBack;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.CloudBusCallBack;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.job.JobQueueFacade;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.header.AbstractService;
import com.syscxp.header.agent.OrderCallbackCmd;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.billing.ProductType;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.message.MessageReply;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.tunnel.BillingCallBackConstant;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.billingCallBack.*;
import com.syscxp.header.tunnel.edgeLine.EdgeLineState;
import com.syscxp.header.tunnel.edgeLine.EdgeLineVO;
import com.syscxp.header.tunnel.tunnel.*;
import com.syscxp.tunnel.tunnel.TunnelBase;
import com.syscxp.tunnel.tunnel.TunnelBillingBase;
import com.syscxp.tunnel.tunnel.job.DeleteRenewVOAfterDeleteResourceJob;
import com.syscxp.tunnel.tunnel.job.UpdateBandwidthJob;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Create by DCY on 2018/1/29
 */
public class BillingCallBackManagerImpl extends AbstractService implements BillingCallBackManager, ApiMessageInterceptor {
    private static final CLogger logger = Utils.getLogger(BillingCallBackManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private JobQueueFacade jobf;
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

    }

    private void handleApiMessage(APIMessage msg) {

    }

    /***********************************************************************************************************/

    private void buyTunnel(OrderCallbackCmd cmd) {
        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();
        TunnelVO vo = dbf.findByUuid(cmd.getPorductUuid(), TunnelVO.class);
        //付款成功,记录生效订单
        tunnelBillingBase.saveResourceOrderEffective(cmd.getOrderUuid(), vo.getUuid(), vo.getClass().getSimpleName());

        if(vo.getState() != TunnelState.Enabled){

            vo.setAccountUuid(vo.getOwnerAccountUuid());
            vo.setState(TunnelState.Deploying);
            vo.setStatus(TunnelStatus.Connecting);
            dbf.updateAndRefresh(vo);

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

    }

    private void buyInterface(OrderCallbackCmd cmd) {
        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();

        InterfaceVO vo = dbf.findByUuid(cmd.getPorductUuid(), InterfaceVO.class);
        vo.setAccountUuid(vo.getOwnerAccountUuid());
        vo.setState(InterfaceState.Down);

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

            tunnelBase.doDeleteTunnelDBAfterUnsubcribeSuccess(vo);

        } else if (message.getDescription().equals("delete")) {

            tunnelBase.doControlDeleteTunnelAfterUnsubcribeSuccess(vo);

        }else if (message.getDescription().equals("unsupport")) {
            vo.setState(TunnelState.Unsupport);
            vo.setStatus(TunnelStatus.Disconnected);
            vo.setExpireDate(dbf.getCurrentSqlTime());
            dbf.updateAndRefresh(vo);
        }

    }

    private void modifyBandwidth(OrderCallbackCmd cmd, UpdateTunnelBandwidthCallBack message) {
        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();

        TunnelVO vo = dbf.findByUuid(cmd.getPorductUuid(), TunnelVO.class);
        vo.setBandwidthOffering(message.getBandwidthOffering());
        vo.setBandwidth(message.getBandwidth());
        vo = dbf.updateAndRefresh(vo);
        //付款成功,记录生效订单
        tunnelBillingBase.saveResourceOrderEffective(cmd.getOrderUuid(), vo.getUuid(), vo.getClass().getSimpleName());

        logger.info("修改带宽支付成功，并创建任务：UpdateBandwidthJob");
        UpdateBandwidthJob job = new UpdateBandwidthJob(vo.getUuid());
        jobf.execute("调整专线带宽-控制器下发", Platform.getManagementServerId(), job);
    }

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

        }else if (cmd.getProductType() == ProductType.EDGELINE) {
            EdgeLineVO vo = dbf.findByUuid(cmd.getPorductUuid(), EdgeLineVO.class);
            vo.setExpireDate(tunnelBillingBase.getExpireDate(vo.getExpireDate(), cmd.getProductChargeModel(), cmd.getDuration()));
            dbf.updateAndRefresh(vo);
            //付款成功,记录生效订单
            tunnelBillingBase.saveResourceOrderEffective(cmd.getOrderUuid(), vo.getUuid(), vo.getClass().getSimpleName());
        }
    }

    private void buyEdgeLine(OrderCallbackCmd cmd){
        TunnelBillingBase tunnelBillingBase = new TunnelBillingBase();

        EdgeLineVO vo = dbf.findByUuid(cmd.getPorductUuid(), EdgeLineVO.class);
        vo.setExpireDate(tunnelBillingBase.getExpireDate(dbf.getCurrentSqlTime(),cmd.getProductChargeModel(),cmd.getDuration()));
        vo.setState(EdgeLineState.Opened);
        vo = dbf.updateAndRefresh(vo);

        InterfaceVO interfaceVO = dbf.findByUuid(vo.getInterfaceUuid(), InterfaceVO.class);
        interfaceVO.setState(InterfaceState.Up);
        if(interfaceVO.getExpireDate() == null){
            interfaceVO.setExpireDate(tunnelBillingBase.getExpireDate(dbf.getCurrentSqlTime(),interfaceVO.getProductChargeModel(),interfaceVO.getDuration()));
        }
        dbf.updateAndRefresh(interfaceVO);

        //付款成功,记录生效订单
        tunnelBillingBase.saveResourceOrderEffective(cmd.getOrderUuid(), vo.getUuid(), vo.getClass().getSimpleName());
    }

    private void unsubcribeEdgeLine(OrderCallbackCmd cmd){
        EdgeLineVO vo = dbf.findByUuid(cmd.getPorductUuid(), EdgeLineVO.class);
        new TunnelBillingBase().saveResourceOrderEffective(cmd.getOrderUuid(), vo.getUuid(), vo.getClass().getSimpleName());
        dbf.remove(vo);

        InterfaceVO interfaceVO = dbf.findByUuid(vo.getInterfaceUuid(), InterfaceVO.class);
        interfaceVO.setState(InterfaceState.Down);
        dbf.updateAndRefresh(interfaceVO);
    }

    @Override
    public boolean start() {

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
                            buyTunnel(cmd);
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

                    } else if (message instanceof RenewEdgeLineCallBack) {
                        logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                        if (!orderIsExist(cmd.getOrderUuid())) {
                            renewOrSla(cmd);
                        }

                    } else if (message instanceof SalEdgeLineCallBack) {
                        logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                        if (!orderIsExist(cmd.getOrderUuid())) {
                            renewOrSla(cmd);
                        }

                    } else if (message instanceof UnsubcribeEdgeLineCallBack) {
                        logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                        if (!orderIsExist(cmd.getOrderUuid())) {
                            unsubcribeEdgeLine(cmd);
                        }

                    } else if (message instanceof CreateEdgeLineCallBack) {
                        logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                        if (!orderIsExist(cmd.getOrderUuid())) {
                            buyEdgeLine(cmd);
                        }

                    } else {
                        logger.debug("未知回调！！！！！！！！");
                    }

                    return null;
                });
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(BillingCallBackConstant.SERVICE_ID);
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        return msg;
    }
}
