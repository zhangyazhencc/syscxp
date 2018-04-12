package com.syscxp.header.idc.solution;

import com.syscxp.header.message.APIEvent;

/**
 * Create by DCY on 2018/4/12
 */
public class APIUpdateSolutionInterfaceEvent extends APIEvent {

    private SolutionInterfaceInventory inventory;

    public APIUpdateSolutionInterfaceEvent(String apiId) {
        super(apiId);
    }

    public APIUpdateSolutionInterfaceEvent() {
        super(null);
    }

    public SolutionInterfaceInventory getInventory() {
        return inventory;
    }

    public void setInventory(SolutionInterfaceInventory inventory) {
        this.inventory = inventory;
    }
}
