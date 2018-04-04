package com.syscxp.idc.idc;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.header.billing.*;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.tunnel.tunnel.ResourceOrderEffectiveVO;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.sql.Timestamp;

@Configurable(preConstruction = true, dependencyCheck = true, autowire = Autowire.BY_TYPE)
public class IdcBase {

    private static final CLogger logger = Utils.getLogger(IdcBase.class);
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private RESTFacade restf;

    public Timestamp getExpireDate(Timestamp oldTime, ProductChargeModel chargeModel, int duration) {
        Timestamp newTime = oldTime;
        if (chargeModel == ProductChargeModel.BY_YEAR) {
            newTime = Timestamp.valueOf(oldTime.toLocalDateTime().plusYears(duration));
        } else if (chargeModel == ProductChargeModel.BY_MONTH) {
            newTime = Timestamp.valueOf(oldTime.toLocalDateTime().plusMonths(duration));
        } else if (chargeModel == ProductChargeModel.BY_WEEK) {
            newTime = Timestamp.valueOf(oldTime.toLocalDateTime().plusWeeks(duration));
        } else if (chargeModel == ProductChargeModel.BY_DAY) {
            newTime = Timestamp.valueOf(oldTime.toLocalDateTime().plusDays(duration));
        }
        return newTime;
    }


    public void saveResourceOrderEffective(String orderUuid, String resourceUuid, String resourceType) {
        ResourceOrderEffectiveVO resourceOrderEffectiveVO = new ResourceOrderEffectiveVO();
        resourceOrderEffectiveVO.setUuid(Platform.getUuid());
        resourceOrderEffectiveVO.setResourceType(resourceType);
        resourceOrderEffectiveVO.setResourceUuid(resourceUuid);
        resourceOrderEffectiveVO.setOrderUuid(orderUuid);
        dbf.persistAndRefresh(resourceOrderEffectiveVO);
    }

    public OrderInventory createOrder(APICreateOrderMsg orderMsg) {
        try {
            APICreateOrderReply reply = new BillingRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(orderMsg);
            if (!reply.isOrderSuccess()) {

            }else{
                return reply.getInventory();
            }
        } catch (Exception e) {
            logger.error(String.format("无法创建订单, %s", e.getMessage()), e);
        }
        return null;
    }

    public OrderInventory createModifyOrder(APICreateIDCModifyOrderMsg orderMsg) {
        try {
            APICreateIDCModifyOrderReply reply = new BillingRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(orderMsg);
            if (reply.isSuccess()) {
                return reply.getInventory();
            }
        } catch (Exception e) {
            logger.error(String.format("无法创建修改价格订单, %s", e.getMessage()), e);
        }
        return null;
    }



    public OrderInventory createOrderForIDCTrustee(APICreateBuyIDCOrderMsg orderMsg) {
        try {
            APICreateBuyIDCOrderReply reply = new BillingRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(orderMsg);

            if (reply.isSuccess()) {
                return reply.getInventory();
            }
        } catch (Exception e) {
            logger.error(String.format("无法创建购买订单, %s", e.getMessage()), e);
        }
        return null;
    }

}
