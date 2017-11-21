package com.syscxp.header.tunnel.solution;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APIUpdateSolutionTunnelEvent extends APIEvent {
    private SolutionTunnelInventory inventory;

    public APIUpdateSolutionTunnelEvent(String apiId) {
        super(apiId);
    }

    public APIUpdateSolutionTunnelEvent() {
        super(null);
    }

    public SolutionTunnelInventory getInventory() {
        return inventory;
    }

    public void setInventory(SolutionTunnelInventory inventory) {
        this.inventory = inventory;
    }
}
