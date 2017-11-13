package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.billing.OrderType;
import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

public class APIUpdateExpireDateMsg extends APISyncCallMessage {
    @APIParam(emptyString = false, resourceType = InterfaceVO.class, checkAccount = true)
    private String uuid;
    @APIParam
    private Integer duration;
    @APIParam(validValues = {"BY_MONTH", "BY_YEAR", "BY_DAY"})
    private ProductChargeModel productChargeModel;
    @APIParam
    private OrderType type;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public ProductChargeModel getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(ProductChargeModel productChargeModel) {
        this.productChargeModel = productChargeModel;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

}
