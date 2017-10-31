package com.syscxp.header.billing;

import com.syscxp.header.message.APIReply;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APICreateOrderReply extends APIReply {
    private  OrderInventory inventory;

    private boolean isSuccess = true;

    public APICreateOrderReply(){}

    public OrderInventory getInventory() {
        return inventory;
    }

    public void setInventory(OrderInventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public boolean isSuccess() {
        return isSuccess;
    }

    @Override
    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
