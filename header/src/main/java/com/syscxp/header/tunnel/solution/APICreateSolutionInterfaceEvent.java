package com.syscxp.header.tunnel.solution;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APICreateSolutionInterfaceEvent extends APIEvent {
    private SolutionInterfaceInventory inventory;

    public APICreateSolutionInterfaceEvent(String apiId) {
        super(apiId);
    }

    public APICreateSolutionInterfaceEvent() {
        super(null);
    }

    public SolutionInterfaceInventory getInventory() {
        return inventory;
    }

    public void setInventory(SolutionInterfaceInventory inventory) {
        this.inventory = inventory;
    }
}
