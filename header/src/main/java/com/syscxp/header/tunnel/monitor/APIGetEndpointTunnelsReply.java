package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.billing.APIGetProductPriceReply;

import java.util.List;

/**
 * Create by DCY on 2017/11/1
 */
public class APIGetEndpointTunnelsReply extends APIGetProductPriceReply {
    private List<EndpointTunnelsInventory> inventories;
    public APIGetEndpointTunnelsReply() {
    }

    public APIGetEndpointTunnelsReply(APIGetProductPriceReply reply) {
        super(reply);
    }

    public List<EndpointTunnelsInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<EndpointTunnelsInventory> inventories) {
        this.inventories = inventories;
    }
}
