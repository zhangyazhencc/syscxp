package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIEvent;

/**
 * Create by DCY on 2017/10/31
 */
public class APIOpenTunnelEvent extends APIEvent {
    private TunnelInventory inventory;

    public APIOpenTunnelEvent(){}

    public APIOpenTunnelEvent(String apiId){super(apiId);}

    public TunnelInventory getInventory() {
        return inventory;
    }

    public void setInventory(TunnelInventory inventory) {
        this.inventory = inventory;
    }
}
