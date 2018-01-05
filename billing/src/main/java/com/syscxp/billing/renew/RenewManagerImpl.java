package com.syscxp.billing.renew;

import com.syscxp.billing.header.renew.APIUpdateRenewEvent;
import com.syscxp.billing.header.renew.APIUpdateRenewMsg;
import com.syscxp.billing.header.renew.RenewInventory;
import com.syscxp.billing.header.renew.RenewVO;
import com.syscxp.core.Platform;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.db.UpdateQuery;
import com.syscxp.header.billing.APICreateOrderMsg;
import com.syscxp.header.billing.APIDeleteExpiredRenewEvent;
import com.syscxp.header.billing.APIDeleteExpiredRenewMsg;
import com.syscxp.header.billing.BillingConstant;
import org.springframework.beans.factory.annotation.Autowired;
import com.syscxp.billing.header.renew.*;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.DbEntityLister;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;

import java.math.BigDecimal;

public class RenewManagerImpl  extends AbstractService implements  ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(RenewManagerImpl.class);

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
        if (msg instanceof APIUpdateRenewMsg) {
            handle((APIUpdateRenewMsg) msg);
        } else if (msg instanceof APIUpdateRenewPriceMsg) {
            handle((APIUpdateRenewPriceMsg) msg);
        }  else if (msg instanceof APIDeleteExpiredRenewMsg) {
            handle((APIDeleteExpiredRenewMsg) msg);
        }  else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIDeleteExpiredRenewMsg msg) {
        UpdateQuery q = UpdateQuery.New(RenewVO.class);
        q.condAnd(RenewVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        q.condAnd(RenewVO_.productUuid, SimpleQuery.Op.EQ, msg.getProductUuid());
        q.delete();
        APIDeleteExpiredRenewEvent event = new APIDeleteExpiredRenewEvent(msg.getId());
        event.setInventory(true);
        bus.publish(event);
    }

    private void handle(APIUpdateRenewPriceMsg msg) {
        if (msg.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(" input valuable vlue");
        }
        RenewVO vo = dbf.findByUuid(msg.getUuid(), RenewVO.class);
        vo.setPriceDiscount(msg.getPrice());
        dbf.updateAndRefresh(vo);
        saveRenewPriceLog(vo.getAccountUuid(), msg.getSession().getAccountUuid(), vo.getProductUuid(), msg.getSession().getUserUuid(), vo.getPriceDiscount(), msg.getPrice());
        RenewInventory ri = RenewInventory.valueOf(vo);
        APIUpdateRenewPriceEvent evt = new APIUpdateRenewPriceEvent(msg.getId());
        evt.setInventory(ri);
        bus.publish(evt);
    }

    private void saveRenewPriceLog(String accountUuid,String opAccountUuid,String productUuid,String opUserUuid,BigDecimal originPrice,BigDecimal nowPrice) {
        RenewPriceLogVO vo = new RenewPriceLogVO();
        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(accountUuid);
        vo.setOpAccountUuid(opAccountUuid);
        vo.setProductUuid(productUuid);
        vo.setOpUserUuid(opUserUuid);
        vo.setOriginPrice(originPrice);
        vo.setNowPrice(nowPrice);
        dbf.persistAndRefresh(vo);
    }

    private void handle(APIUpdateRenewMsg msg) {
        RenewVO vo = dbf.findByUuid(msg.getUuid(), RenewVO.class);
        if (vo.isRenewAuto() != msg.isRenewAuto()) {
            vo.setRenewAuto(msg.isRenewAuto());
        }
        dbf.updateAndRefresh(vo);
        RenewInventory ri = RenewInventory.valueOf(vo);
        APIUpdateRenewEvent evt = new APIUpdateRenewEvent(msg.getId());
        evt.setInventory(ri);
        bus.publish(evt);

    }


    @Override
    public String getId() {
        return bus.makeLocalServiceId(BillingConstant.SERVICE_ID_RENEW);
    }

    @Override
    public boolean start() {
        try {

        } catch (Exception e) {
            throw new CloudRuntimeException(e);
        }
        return true;
    }


    @Override
    public boolean stop() {
        return true;
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {

        if (msg instanceof APICreateOrderMsg) {
            validate((APICreateOrderMsg) msg);
        }
        return msg;
    }

    private void validate(APICreateOrderMsg msg) {

    }

}
