package com.syscxp.header.idc.solution;

import com.syscxp.header.message.APIEvent;

public class APICreateSolutionInterfaceEvent extends APIEvent {
    private SolutionInterfaceInventory interfaceInventory;

    public APICreateSolutionInterfaceEvent(String apiId) {
        super(apiId);
    }

    public APICreateSolutionInterfaceEvent() {
    }

    public SolutionInterfaceInventory getInterfaceInventory() {
        return interfaceInventory;
    }

    public void setInterfaceInventory(SolutionInterfaceInventory interfaceInventory) {
        this.interfaceInventory = interfaceInventory;
    }


}
