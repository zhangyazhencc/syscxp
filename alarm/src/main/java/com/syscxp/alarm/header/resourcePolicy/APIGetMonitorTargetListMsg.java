package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.billing.ProductType;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

public class APIGetMonitorTargetListMsg extends APISyncCallMessage{

    @APIParam
    private ProductType type;

    public ProductType getType() {
        return type;
    }

    public void setType(ProductType type) {
        this.type = type;
    }
}
