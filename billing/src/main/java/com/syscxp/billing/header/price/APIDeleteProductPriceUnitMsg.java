package com.syscxp.billing.header.price;

import com.syscxp.header.billing.ProductPriceUnitVO;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

public class APIDeleteProductPriceUnitMsg extends APIMessage{
    @APIParam(required = false, resourceType = ProductPriceUnitVO.class)
    private String uuid;

    @APIParam(required = false, maxLength = 256)
    private String lineName;

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
