package com.syscxp.header.tunnel.solution;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APIUpdateSolutionEvent extends APIEvent {
    private SolutionInventory inventory;

    public APIUpdateSolutionEvent(String apiId) {
        super(apiId);
    }

    public APIUpdateSolutionEvent() {
        super(null);
    }

    public SolutionInventory getInventory() {
        return inventory;
    }

    public void setInventory(SolutionInventory inventory) {
        this.inventory = inventory;
    }
}
