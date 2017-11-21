package com.syscxp.header.tunnel.solution;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APICreateSolutionVpnEvent extends APIEvent {
    private SolutionVpnInventory inventory;

    public APICreateSolutionVpnEvent(String apiId) {
        super(apiId);
    }

    public APICreateSolutionVpnEvent() {
        super(null);
    }

    public SolutionVpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(SolutionVpnInventory inventory) {
        this.inventory = inventory;
    }
}
