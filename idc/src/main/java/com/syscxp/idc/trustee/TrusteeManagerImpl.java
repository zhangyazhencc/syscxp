package com.syscxp.idc.trustee;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.db.UpdateQuery;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.header.AbstractService;
import com.syscxp.header.agent.OrderCallbackCmd;
import com.syscxp.header.billing.*;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.tunnel.billingCallBack.*;
import com.syscxp.header.tunnel.tunnel.InterfaceVO;
import com.syscxp.header.tunnel.tunnel.ResourceOrderEffectiveVO_;
import com.syscxp.idc.header.trustee.*;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class TrusteeManagerImpl extends AbstractService implements TrusteeManager {

    @Autowired
    private CloudBus bus;

    @Autowired
    private DatabaseFacade dbf;

    @Autowired
    private ErrorFacade errf;

    @Autowired
    private RESTFacade restf;

    private static final CLogger logger = Utils.getLogger(TrusteeManagerImpl.class);

    private final VOAddAllOfMsg vOAddAllOfMsg = new VOAddAllOfMsg();

    private final TrusteeBase trusteeBase = new TrusteeBase();

    @Override
    public void handleMessage(Message msg) {

        if(msg instanceof APICreateTrusteeMsg){
            handle((APICreateTrusteeMsg)msg);
        } else if(msg instanceof APIDeleteTrusteeMsg){
            handle((APIDeleteTrusteeMsg)msg);
        } else if(msg instanceof APIUpdateTrusteeMsg){
            handle((APIUpdateTrusteeMsg)msg);
        } else if(msg instanceof APITrusteeRenewalMsg){
            handle((APITrusteeRenewalMsg)msg);
        } else if(msg instanceof APICreateTrusteeDetailMsg){
            handle((APICreateTrusteeDetailMsg)msg);
        } else if(msg instanceof APIDeleteTrusteeDetailMsg){
            handle((APIDeleteTrusteeDetailMsg)msg);
        } else{
            bus.dealWithUnknownMessage(msg);
        }

    }

    private void handle(APIDeleteTrusteeDetailMsg msg) {

        APIDeleteTrusteeDetailEvent event = new APIDeleteTrusteeDetailEvent(msg.getId());
        UpdateQuery.New(TrustDetailVO.class).condAnd(TrustDetailVO_.uuid, SimpleQuery.Op.EQ,msg.getUuid()).delete();
        bus.publish(event);
    }

    private void handle(APICreateTrusteeDetailMsg msg) {

        APICreateTrusteeDetailEvent event = new APICreateTrusteeDetailEvent(msg.getId());
        TrustDetailVO vo = new TrustDetailVO();
        vOAddAllOfMsg.addAll(msg,vo);
        vo.setUuid(Platform.getUuid());
        vo.setLastOpDate(dbf.getCurrentSqlTime());
        vo.setCreateDate(dbf.getCurrentSqlTime());
        event.setInventory(TrusteeDetailInventory.valueOf(dbf.persistAndRefresh(vo)));
        bus.publish(event);
    }

    private void handle(APITrusteeRenewalMsg msg) {

        APITrusteeRenewalEvent event = new APITrusteeRenewalEvent(msg.getId());

        TrusteeVO vo = dbf.findByUuid(msg.getUuid(), TrusteeVO.class);

        APICreateOrderMsg orderMsg = new APICreateOrderMsg();
        orderMsg.setProductName(vo.getName());
        orderMsg.setProductUuid(vo.getUuid());
        orderMsg.setProductType(ProductType.IDCTrustee);
        orderMsg.setDescriptionData(msg.getCost().toString());
        orderMsg.setAccountUuid(msg.getSession().getAccountUuid());
        orderMsg.setCallBackData("APITrusteeRenewalMsg");
        orderMsg.setNotifyUrl(restf.getSendCommandUrl());
        APICreateRenewOrderMsg RenewOrderMsg = new APICreateRenewOrderMsg(orderMsg);
        RenewOrderMsg.setDuration(msg.getDuration());
        RenewOrderMsg.setProductChargeModel(msg.getProductChargeModel());
        RenewOrderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
        RenewOrderMsg.setStartTime(dbf.getCurrentSqlTime());
        RenewOrderMsg.setExpiredTime(vo.getExpireDate());

        OrderInventory orderInventory = trusteeBase.createOrder(orderMsg);
        if (orderInventory != null) {
            trusteeBase.saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());
            vOAddAllOfMsg.addAll(msg, vo);
            vo.setExpireDate(trusteeBase.getExpireDate(vo.getExpireDate(),msg.getProductChargeModel(),msg.getDuration()));
            vo.setTotalCost(vo.getTotalCost().add(msg.getCost()));
            event.setInventory(TrusteeInventory.valueOf(dbf.updateAndRefresh(vo)));
        }

        bus.publish(event);

    }

    private void handle(APIUpdateTrusteeMsg msg) {

        APIUpdateTrusteeEvent event = new APIUpdateTrusteeEvent(msg.getId());
        TrusteeVO vo = dbf.findByUuid(msg.getUuid(), TrusteeVO.class);
        vOAddAllOfMsg.addAll(msg, vo);

        if(msg.getTotalCost() != null ){
            /**
             * 调用billing修改价格
             */
        }
        event.setInventory(TrusteeInventory.valueOf(dbf.persistAndRefresh(vo)));
        bus.publish(event);

    }

    private void handle(APIDeleteTrusteeMsg msg) {

        APIDeleteTrusteeEvent event = new APIDeleteTrusteeEvent(msg.getId());

        TrusteeVO vo = dbf.findByUuid(msg.getUuid(),TrusteeVO.class);

        if (vo.getExpireDate() != null &&
                !vo.getExpireDate().after(Timestamp.valueOf(LocalDateTime.now()))) {
            return;
        }else{
            //退订
            APICreateUnsubcribeOrderMsg orderMsg = new APICreateUnsubcribeOrderMsg();
            orderMsg.setProductName("IDC托管-"+vo.getName());
            orderMsg.setProductUuid(vo.getUuid());
            orderMsg.setProductType(ProductType.IDCTrustee);
            orderMsg.setDescriptionData(vo.getDescription());
            orderMsg.setAccountUuid(vo.getAccountUuid());
            orderMsg.setCallBackData(msg.getClass().getSimpleName());
            orderMsg.setNotifyUrl(restf.getSendCommandUrl());
            orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
            orderMsg.setStartTime(dbf.getCurrentSqlTime());
            orderMsg.setExpiredTime(vo.getExpireDate());

            OrderInventory orderInventory = trusteeBase.createOrder(orderMsg);
            if (orderInventory != null) {
                trusteeBase.saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());
                UpdateQuery.New(TrustDetailVO.class).condAnd(TrustDetailVO_.trusteeUuid, SimpleQuery.Op.EQ,msg.getUuid()).delete();
                UpdateQuery.New(TrusteeVO.class).condAnd(TrusteeVO_.uuid, SimpleQuery.Op.EQ,msg.getUuid()).delete();
            } else {
                event.setError(errf.stringToOperationError("退订失败"));
            }
        }

        bus.publish(event);
    }

    private CreateTrusteeCallback CreateTrustee(APICreateTrusteeMsg msg){
        TrusteeVO vo = new TrusteeVO();
        vOAddAllOfMsg.addAll(msg,vo);
        vo.setUuid(Platform.getUuid());
        vo.setExpireDate(trusteeBase.getExpireDate(dbf.getCurrentSqlTime(),msg.getProductChargeModel(),msg.getDuration()));

        List<TrustDetailVO> detailList = new ArrayList<>();
        if(msg.getTrusteeDetails() != null && msg.getTrusteeDetails().size()>0){
            msg.getTrusteeDetails().forEach((K,V)->{
                TrustDetailVO detailVO = new TrustDetailVO();
                detailVO.setUuid(Platform.getUuid());
                detailVO.setCost(V);
                detailVO.setName(K);
                detailVO.setCreateDate(dbf.getCurrentSqlTime());
                detailVO.setLastOpDate(dbf.getCurrentSqlTime());
                detailList.add(detailVO);
            });

        }

        CreateTrusteeCallback callBack = new CreateTrusteeCallback();
        callBack.setTrusteeVO(vo);
        callBack.setTrustDetailList(detailList);
        return callBack;
    }

    @Transactional
    private void handle(APICreateTrusteeMsg msg) {

        APICreateTrusteeEvent event = new APICreateTrusteeEvent(msg.getId());

        CreateTrusteeCallback callback = CreateTrustee(msg);
        TrusteeVO vo = callback.getTrusteeVO();
        List<TrustDetailVO> detailList = callback.getTrustDetailList();

        APICreateBuyIDCOrderMsg orderMsg = new APICreateBuyIDCOrderMsg();
        orderMsg.setPrice(msg.getTotalCost().intValue());
        orderMsg.setProductName("IDC托管-"+vo.getUuid());
        orderMsg.setProductUuid(vo.getUuid());
        orderMsg.setDescriptionData(vo.getDescription());
        orderMsg.setCallBackData(RESTApiDecoder.dump(callback));
        orderMsg.setAccountUuid(vo.getAccountUuid());
        orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
        orderMsg.setNotifyUrl(restf.getSendCommandUrl());
        orderMsg.setProductChargeModel(msg.getProductChargeModel());
        orderMsg.setDuration(msg.getDuration());

        OrderInventory orderInventory = trusteeBase.createOrderForIDCTrustee(orderMsg);
        if (orderInventory != null) {
            trusteeBase.saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());
            dbf.getEntityManager().persist(vo);
            dbf.persistCollection(detailList);
            dbf.getEntityManager().flush();
        } else {
            event.setError(errf.stringToOperationError("付款失败"));
        }

        bus.publish(event);
    }

    private boolean orderIsExist(String orderUuid) {
        return Q.New(com.syscxp.header.tunnel.tunnel.ResourceOrderEffectiveVO.class)
                .eq(ResourceOrderEffectiveVO_.orderUuid, orderUuid)
                .isExists();
    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(TrusteeConstant.SERVICE_ID);
    }

    @Override
    public boolean start() {
        /*
         * 清理过期产品
        */

        restf.registerSyncHttpCallHandler("billing", OrderCallbackCmd.class,
                cmd -> {
                    if (cmd.getCallBackData().equals("APIDeleteTrusteeMsg")) {
                        logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                        if (!orderIsExist(cmd.getOrderUuid())) {
                            trusteeBase.saveResourceOrderEffective(cmd.getOrderUuid(), cmd.getPorductUuid(), cmd.getProductType().toString());
                            UpdateQuery.New(TrustDetailVO.class).condAnd(TrustDetailVO_.trusteeUuid, SimpleQuery.Op.EQ,cmd.getPorductUuid()).delete();
                            UpdateQuery.New(TrusteeVO.class).condAnd(TrusteeVO_.uuid, SimpleQuery.Op.EQ,cmd.getPorductUuid()).delete();
                        }

                    } else if(cmd.getCallBackData().equals("APITrusteeRenewalMsg")){
                        logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                        if (!orderIsExist(cmd.getOrderUuid())) {

                            trusteeBase.saveResourceOrderEffective(cmd.getOrderUuid(), cmd.getPorductUuid(), cmd.getProductType().toString());
                            TrusteeVO vo = dbf.findByUuid(cmd.getPorductUuid(), TrusteeVO.class);
                            vo.setExpireDate(trusteeBase.getExpireDate(vo.getExpireDate(),cmd.getProductChargeModel(),cmd.getDuration()));
                            vo.setTotalCost(vo.getTotalCost().add(new BigDecimal(cmd.getDescriptionData())));
                            dbf.updateAndRefresh(vo);

                        }
                    }else {

                        Message message = RESTApiDecoder.loads(cmd.getCallBackData());
                        if(message instanceof CreateTrusteeCallback){
                            CreateTrusteeCallback callback = (CreateTrusteeCallback)message;
                            TrusteeVO vo = callback.getTrusteeVO();
                            List<TrustDetailVO> detailList = callback.getTrustDetailList();
                            trusteeBase.saveResourceOrderEffective(cmd.getOrderUuid(), vo.getUuid(), vo.getClass().getSimpleName());
                            dbf.getEntityManager().persist(vo);
                            dbf.persistCollection(detailList);
                            dbf.getEntityManager().flush();
                        }else{
                            logger.debug("未知回调！！！！！！！！");
                        }

                    }
                    return null;
                });

        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }


}
