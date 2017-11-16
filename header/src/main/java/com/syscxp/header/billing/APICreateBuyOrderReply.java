package com.syscxp.header.billing;

import com.syscxp.header.message.APIReply;

import java.util.List;

public class APICreateBuyOrderReply extends APIReply {

    private List<OrderInventory> inventories;
    private boolean orderSuccess = true;

    public boolean isOrderSuccess() {
        return orderSuccess;
    }

    public void setOrderSuccess(boolean orderSuccess) {
        this.orderSuccess = orderSuccess;
    }

    public List<OrderInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<OrderInventory> inventories) {
        this.inventories = inventories;
    }
}
