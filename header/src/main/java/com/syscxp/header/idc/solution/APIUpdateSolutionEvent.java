package com.syscxp.header.idc.solution;

import com.syscxp.header.message.APIEvent;

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
