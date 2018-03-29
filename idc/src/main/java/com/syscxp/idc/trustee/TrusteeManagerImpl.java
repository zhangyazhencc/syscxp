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
import com.syscxp.header.billing.APICreateUnsubcribeOrderMsg;
import com.syscxp.header.billing.OrderInventory;
import com.syscxp.header.billing.ProductType;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.tunnel.billingCallBack.*;
import com.syscxp.header.tunnel.tunnel.ResourceOrderEffectiveVO_;
import com.syscxp.idc.header.trustee.*;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
        /*
        * 续费
        *
        * */
        TrusteeVO vo = dbf.findByUuid(msg.getUuid(), TrusteeVO.class);
        vOAddAllOfMsg.addAll(msg, vo);
        vo.setExpireDate(trusteeBase.getExpireDate(vo.getExpireDate(),msg.getProductChargeModel(),msg.getDuration()));
        vo.setTotalCost(vo.getTotalCost().add(msg.getCost()));
        event.setInventory(TrusteeInventory.valueOf(dbf.updateAndRefresh(vo)));
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

    @Transactional
    private void handle(APICreateTrusteeMsg msg) {

        APICreateTrusteeEvent event = new APICreateTrusteeEvent(msg.getId());
        /*
        * 扣费
        *
        * */

        TrusteeVO vo = new TrusteeVO();
        vOAddAllOfMsg.addAll(msg,vo);
        vo.setUuid(Platform.getUuid());
        vo.setExpireDate(trusteeBase.getExpireDate(dbf.getCurrentSqlTime(),msg.getProductChargeModel(),msg.getDuration()));
        dbf.getEntityManager().persist(vo);

        if(msg.getTrusteeDetails() != null && msg.getTrusteeDetails().size()>0){
            List<TrustDetailVO> detailList = new ArrayList<>();
            msg.getTrusteeDetails().forEach((K,V)->{
                TrustDetailVO detailVO = new TrustDetailVO();
                detailVO.setUuid(Platform.getUuid());
                detailVO.setCost(V);
                detailVO.setName(K);
                detailVO.setCreateDate(dbf.getCurrentSqlTime());
                detailVO.setLastOpDate(dbf.getCurrentSqlTime());
                detailList.add(detailVO);
            });

            dbf.persistCollection(detailList);
//            detailList.stream().forEach((itVO)->{dbf.getEntityManager().persist(itVO);});
        }

        dbf.getEntityManager().flush();
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
                            trusteeBase.saveResourceOrderEffective(cmd.getOrderUuid(),
                                    cmd.getPorductUuid(), cmd.getProductType().toString());
                            UpdateQuery.New(TrustDetailVO.class).condAnd(TrustDetailVO_.trusteeUuid,
                                    SimpleQuery.Op.EQ,cmd.getPorductUuid()).delete();
                            UpdateQuery.New(TrusteeVO.class).condAnd(TrusteeVO_.uuid,
                                    SimpleQuery.Op.EQ,cmd.getPorductUuid()).delete();
                        }

                    }


                    else {
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


}
