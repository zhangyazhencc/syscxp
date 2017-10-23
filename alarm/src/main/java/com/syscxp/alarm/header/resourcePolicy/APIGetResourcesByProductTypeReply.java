package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.message.APIReply;
import com.syscxp.header.tunnel.TunnelForBillingInventory;

import java.util.List;

public class APIGetResourcesByProductTypeReply extends APIReply{
    List<TunnelForBillingInventory> inventories;

    public List<TunnelForBillingInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<TunnelForBillingInventory> inventories) {
        this.inventories = inventories;
    }
}
