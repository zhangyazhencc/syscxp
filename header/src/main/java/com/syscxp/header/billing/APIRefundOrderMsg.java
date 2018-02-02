package com.syscxp.header.billing;

import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

@InnerCredentialCheck
public class APIRefundOrderMsg extends APISyncCallMessage{

    @APIParam(emptyString = false,resourceType = OrderVO.class)
    private String orderUuid;

    public String getOrderUuid() {
        return orderUuid;
    }

    public void setOrderUuid(String orderUuid) {
        this.orderUuid = orderUuid;
    }
}
