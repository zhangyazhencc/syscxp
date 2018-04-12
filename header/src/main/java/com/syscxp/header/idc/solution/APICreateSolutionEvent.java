package com.syscxp.header.idc.solution;

import com.syscxp.header.message.APIEvent;

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