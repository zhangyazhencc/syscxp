package com.syscxp.header.tunnel.solution;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APICreateSolutionTunnelEvent extends APIEvent {
    private SolutionTunnelInventory inventory;

    public APICreateSolutionTunnelEvent(String apiId) {
        super(apiId);
    }

    public APICreateSolutionTunnelEvent() {
        super(null);
    }

    public SolutionTunnelInventory getInventory() {
        return inventory;
    }

    public void setInventory(SolutionTunnelInventory inventory) {
        this.inventory = inventory;
    }
}
