package com.syscxp.billing.header.price;

import com.syscxp.header.billing.ProductPriceUnitVO;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

public class APIUpdateProductPriceUnitMsg extends APIMessage{
    @APIParam(resourceType = ProductPriceUnitVO.class)
    private String uuid;

    private Integer unitPrice;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Integer unitPrice) {
        this.unitPrice = unitPrice;
    }
}
