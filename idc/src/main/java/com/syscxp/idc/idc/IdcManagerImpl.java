package com.syscxp.idc.idc;

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
import com.syscxp.header.idc.IdcConstant;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.tunnel.tunnel.ResourceOrderEffectiveVO_;
import com.syscxp.idc.header.*;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class IdcManagerImpl extends AbstractService implements IdcManager {

    @Autowired
    private CloudBus bus;

    @Autowired
    private DatabaseFacade dbf;

    @Autowired
    private ErrorFacade errf;

    @Autowired
    private RESTFacade restf;

    private static final CLogger logger = Utils.getLogger(IdcManagerImpl.class);

    private final VOAddAllOfMsg vOAddAllOfMsg = new VOAddAllOfMsg();

    private final IdcBase idcBase = new IdcBase();

    @Override
    public void handleMessage(Message msg) {

        if(msg instanceof APICreateIdcMsg){
            handle((APICreateIdcMsg)msg);
        } else if(msg instanceof APIDeleteIdcMsg){
            handle((APIDeleteIdcMsg)msg);
        } else if(msg instanceof APIUpdateIdcMsg){
            handle((APIUpdateIdcMsg)msg);
        } else if(msg instanceof APIIdcRenewalMsg){
            handle((APIIdcRenewalMsg)msg);
        } else if(msg instanceof APICreateIdcDetailMsg){
            handle((APICreateIdcDetailMsg)msg);
        } else if(msg instanceof APIDeleteIdcDetailMsg){
            handle((APIDeleteIdcDetailMsg)msg);
        } else if(msg instanceof APIModifyIdcTotalCostMsg){
            handle((APIModifyIdcTotalCostMsg)msg);
        } else{
            bus.dealWithUnknownMessage(msg);
        }

    }

    @Transactional
    private void handle(APIModifyIdcTotalCostMsg msg) {

        APIModifyIdcTotalCostEvent event = new APIModifyIdcTotalCostEvent(msg.getId());

        IdcVO vo = dbf.findByUuid(msg.getUuid(), IdcVO.class);

        APICreateIDCModifyOrderMsg orderMsg = new APICreateIDCModifyOrderMsg();
        orderMsg.setFixedCost(vo.getTotalCost().subtract(msg.getFixedCost()).intValue());
        orderMsg.setProductName("IDC托管-"+vo.getName());
        orderMsg.setProductUuid(vo.getUuid());
        orderMsg.setProductType(ProductType.IDCTrustee);
        orderMsg.setDescriptionData(msg.getFixedCost().toString());
        orderMsg.setAccountUuid(vo.getAccountUuid());
        orderMsg.setCallBackData(msg.getClass().getSimpleName());
        orderMsg.setNotifyUrl(restf.getSendCommandUrl());
        orderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
        orderMsg.setExpiredTime(vo.getExpireDate());

        OrderInventory orderInventory = idcBase.createModifyOrder(orderMsg);
        if (orderInventory == null) {
            event.setError(errf.stringToOperationError("修改价格失败"));
        }else {
            idcBase.saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());
            vo.setTotalCost(msg.getFixedCost());
            event.setInventory(IdcInventory.valueOf(dbf.updateAndRefresh(vo)));
        }

        bus.publish(event);
    }

    private void handle(APIDeleteIdcDetailMsg msg) {

        APIDeleteIdcDetailEvent event = new APIDeleteIdcDetailEvent(msg.getId());
        UpdateQuery.New(IdcDetailVO.class).condAnd(IdcDetailVO_.uuid, SimpleQuery.Op.EQ,msg.getUuid()).delete();
        bus.publish(event);
    }

    private void handle(APICreateIdcDetailMsg msg) {

        APICreateIdcDetailEvent event = new APICreateIdcDetailEvent(msg.getId());
        IdcDetailVO vo = new IdcDetailVO();
        vOAddAllOfMsg.addAll(msg,vo);
        vo.setUuid(Platform.getUuid());
        vo.setLastOpDate(dbf.getCurrentSqlTime());
        vo.setCreateDate(dbf.getCurrentSqlTime());
        event.setInventory(IdcDetailInventory.valueOf(dbf.persistAndRefresh(vo)));
        bus.publish(event);
    }

    private void handle(APIIdcRenewalMsg msg) {

        APIIdcRenewalEvent event = new APIIdcRenewalEvent(msg.getId());

        IdcVO vo = dbf.findByUuid(msg.getUuid(), IdcVO.class);

        APICreateOrderMsg orderMsg = new APICreateOrderMsg();
        orderMsg.setProductName(vo.getName());
        orderMsg.setProductUuid(vo.getUuid());
        orderMsg.setProductType(ProductType.IDCTrustee);
        orderMsg.setDescriptionData(msg.getCost().toString());
        orderMsg.setAccountUuid(msg.getSession().getAccountUuid());
        orderMsg.setCallBackData("APIIdcRenewalMsg");
        orderMsg.setNotifyUrl(restf.getSendCommandUrl());
        APICreateRenewOrderMsg RenewOrderMsg = new APICreateRenewOrderMsg(orderMsg);
        RenewOrderMsg.setDuration(msg.getDuration());
        RenewOrderMsg.setProductChargeModel(msg.getProductChargeModel());
        RenewOrderMsg.setOpAccountUuid(msg.getSession().getAccountUuid());
        RenewOrderMsg.setStartTime(dbf.getCurrentSqlTime());
        RenewOrderMsg.setExpiredTime(vo.getExpireDate());

        OrderInventory orderInventory = idcBase.createOrder(orderMsg);
        if (orderInventory != null) {
            idcBase.saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());
            vOAddAllOfMsg.addAll(msg, vo);
            vo.setExpireDate(idcBase.getExpireDate(vo.getExpireDate(),msg.getProductChargeModel(),msg.getDuration()));
            vo.setTotalCost(vo.getTotalCost().add(msg.getCost()));
            event.setInventory(IdcInventory.valueOf(dbf.updateAndRefresh(vo)));
        }

        bus.publish(event);

    }

    private void handle(APIUpdateIdcMsg msg) {

        APIUpdateIdcEvent event = new APIUpdateIdcEvent(msg.getId());
        IdcVO vo = dbf.findByUuid(msg.getUuid(), IdcVO.class);
        vOAddAllOfMsg.addAll(msg, vo);
        event.setInventory(IdcInventory.valueOf(dbf.updateAndRefresh(vo)));
        bus.publish(event);

    }

    private void handle(APIDeleteIdcMsg msg) {

        APIDeleteIdcEvent event = new APIDeleteIdcEvent(msg.getId());
        IdcVO vo = dbf.findByUuid(msg.getUuid(),IdcVO.class);

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

            OrderInventory orderInventory = idcBase.createOrder(orderMsg);
            if (orderInventory != null) {
                idcBase.saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());
                UpdateQuery.New(IdcDetailVO.class).condAnd(IdcDetailVO_.trusteeUuid, SimpleQuery.Op.EQ,msg.getUuid()).delete();
                UpdateQuery.New(IdcVO.class).condAnd(IdcVO_.uuid, SimpleQuery.Op.EQ,msg.getUuid()).delete();
            } else {
                event.setError(errf.stringToOperationError("退订失败"));
            }
        }

        bus.publish(event);
    }

    private CreateIdcCallback CreateTrustee(APICreateIdcMsg msg){
        IdcVO vo = new IdcVO();
        vOAddAllOfMsg.addAll(msg,vo);
        vo.setUuid(Platform.getUuid());
        vo.setExpireDate(idcBase.getExpireDate(dbf.getCurrentSqlTime(),msg.getProductChargeModel(),msg.getDuration()));

        List<IdcDetailVO> detailList = new ArrayList<>();
        if(msg.getTrusteeDetails() != null && msg.getTrusteeDetails().size()>0){
            msg.getTrusteeDetails().forEach((K,V)->{
                IdcDetailVO detailVO = new IdcDetailVO();
                detailVO.setUuid(Platform.getUuid());
                detailVO.setCost(V);
                detailVO.setName(K);
                detailVO.setCreateDate(dbf.getCurrentSqlTime());
                detailVO.setLastOpDate(dbf.getCurrentSqlTime());
                detailList.add(detailVO);
            });

        }

        CreateIdcCallback callBack = new CreateIdcCallback();
        callBack.setTrusteeVO(vo);
        callBack.setTrustDetailList(detailList);
        return callBack;
    }

    @Transactional
    private void handle(APICreateIdcMsg msg) {

        APICreateIdcEvent event = new APICreateIdcEvent(msg.getId());

        CreateIdcCallback callback = CreateTrustee(msg);
        IdcVO vo = callback.getTrusteeVO();
        List<IdcDetailVO> detailList = callback.getTrustDetailList();

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

        OrderInventory orderInventory = idcBase.createOrderForIDCTrustee(orderMsg);
        if (orderInventory != null) {
            idcBase.saveResourceOrderEffective(orderInventory.getUuid(), vo.getUuid(), vo.getClass().getSimpleName());
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
        return bus.makeLocalServiceId(IdcConstant.SERVICE_ID);
    }

    @Override
    public boolean start() {

        restf.registerSyncHttpCallHandler("billing", OrderCallbackCmd.class,
                cmd -> {
                    if (cmd.getCallBackData().equals("APIDeleteIdcMsg")) {
                        logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                        if (!orderIsExist(cmd.getOrderUuid())) {
                            idcBase.saveResourceOrderEffective(cmd.getOrderUuid(), cmd.getPorductUuid(), cmd.getProductType().toString());
                            UpdateQuery.New(IdcDetailVO.class).condAnd(IdcDetailVO_.trusteeUuid, SimpleQuery.Op.EQ,cmd.getPorductUuid()).delete();
                            UpdateQuery.New(IdcVO.class).condAnd(IdcVO_.uuid, SimpleQuery.Op.EQ,cmd.getPorductUuid()).delete();
                        }

                    } else if(cmd.getCallBackData().equals("APIIdcRenewalMsg")){
                        logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                        if (!orderIsExist(cmd.getOrderUuid())) {

                            idcBase.saveResourceOrderEffective(cmd.getOrderUuid(), cmd.getPorductUuid(), cmd.getProductType().toString());
                            IdcVO vo = dbf.findByUuid(cmd.getPorductUuid(), IdcVO.class);
                            vo.setExpireDate(idcBase.getExpireDate(vo.getExpireDate(),cmd.getProductChargeModel(),cmd.getDuration()));
                            vo.setTotalCost(vo.getTotalCost().add(new BigDecimal(cmd.getDescriptionData())));
                            dbf.updateAndRefresh(vo);

                        }
                    }else if(cmd.getCallBackData().equals("APIModifyIdcTotalCostMsg")){
                        logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                        if (!orderIsExist(cmd.getOrderUuid())) {
                            idcBase.saveResourceOrderEffective(cmd.getOrderUuid(), cmd.getPorductUuid(), cmd.getProductType().toString());
                            IdcVO vo = dbf.findByUuid(cmd.getPorductUuid(),IdcVO.class);
                            vo.setTotalCost(new BigDecimal(cmd.getDescriptionData()));
                            dbf.updateAndRefresh(vo);
                        }


                    }else {

                        Message message = RESTApiDecoder.loads(cmd.getCallBackData());
                        if(message instanceof CreateIdcCallback){
                            logger.debug(String.format("from %s call back. type: %s", CoreGlobalProperty.BILLING_SERVER_URL, cmd.getType()));
                            CreateIdcCallback callback = (CreateIdcCallback)message;
                            IdcVO vo = callback.getTrusteeVO();
                            List<IdcDetailVO> detailList = callback.getTrustDetailList();
                            idcBase.saveResourceOrderEffective(cmd.getOrderUuid(), vo.getUuid(), vo.getClass().getSimpleName());
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
