package com.syscxp.header.tunnel.solution;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APICreateSolutionInterfaceEvent extends APIEvent {
    private SolutionInterfaceInventory interfaceInventory;
    private SolutionInventory solutionInventory;

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

    public SolutionInventory getSolutionInventory() {
        return solutionInventory;
    }

    public void setSolutionInventory(SolutionInventory solutionInventory) {
        this.solutionInventory = solutionInventory;
    }
}
