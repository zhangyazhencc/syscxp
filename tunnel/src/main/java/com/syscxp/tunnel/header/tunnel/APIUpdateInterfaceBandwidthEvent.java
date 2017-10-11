package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

/**
 * Create by DCY on 2017/9/28
 */
@RestResponse(allTo = "inventory")
public class APIUpdateInterfaceBandwidthEvent extends APIEvent {
    private InterfaceInventory inventory;

    public APIUpdateInterfaceBandwidthEvent(){}

    public APIUpdateInterfaceBandwidthEvent(String apiId){super(apiId);}

    public InterfaceInventory getInventory() {
        return inventory;
    }

    public void setInventory(InterfaceInventory inventory) {
        this.inventory = inventory;
    }
}