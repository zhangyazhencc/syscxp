package com.syscxp.tunnel.solution.header;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APICreateSolutionEvent extends APIEvent {
    private SolutionInventory inventory;

    public APICreateSolutionEvent(String apiId) {
        super(apiId);
    }

    public APICreateSolutionEvent() {
        super(null);
    }

    public SolutionInventory getInventory() {
        return inventory;
    }

    public void setInventory(SolutionInventory inventory) {
        this.inventory = inventory;
    }
}
