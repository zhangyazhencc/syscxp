package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.message.APIEvent;

/**
 * Create by DCY on 2017/11/6
 */
public class APIUpdateForciblyTunnelVlanEvent extends APIEvent {
    private TunnelInventory inventory;

    public APIUpdateForciblyTunnelVlanEvent(){}

    public APIUpdateForciblyTunnelVlanEvent(String apiId){super(apiId);}

    public TunnelInventory getInventory() {
        return inventory;
    }

    public void setInventory(TunnelInventory inventory) {
        this.inventory = inventory;
    }
}
