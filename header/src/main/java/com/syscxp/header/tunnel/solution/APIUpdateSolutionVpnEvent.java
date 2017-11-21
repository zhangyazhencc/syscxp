package com.syscxp.header.tunnel.solution;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APIUpdateSolutionVpnEvent extends APIEvent {
    private SolutionVpnInventory inventory;

    public APIUpdateSolutionVpnEvent(String apiId) {
        super(apiId);
    }

    public APIUpdateSolutionVpnEvent() {
        super(null);
    }

    public SolutionVpnInventory getInventory() {
        return inventory;
    }

    public void setInventory(SolutionVpnInventory inventory) {
        this.inventory = inventory;
    }
}
