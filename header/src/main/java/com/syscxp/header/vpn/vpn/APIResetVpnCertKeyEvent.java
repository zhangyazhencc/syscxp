package com.syscxp.header.vpn.vpn;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APIResetVpnCertKeyEvent extends APIEvent {


    public APIResetVpnCertKeyEvent() {
    }

    public APIResetVpnCertKeyEvent(String apiId) {
        super(apiId);
    }

    private VpnInventory inventory;

    public VpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnInventory inventory) {
        this.inventory = inventory;
    }
}
