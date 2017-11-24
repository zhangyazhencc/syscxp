package com.syscxp.header.tunnel.solution;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APICreateSolutionInterfaceEvent extends APIEvent {
    private SolutionInterfaceInventory InterfaceInventory;
    private SolutionInventory solutionInventory;

    public APICreateSolutionInterfaceEvent(String apiId) {
        super(apiId);
    }

    public APICreateSolutionInterfaceEvent() {
        super(null);
    }

    public SolutionInterfaceInventory getInterfaceInventory() {
        return InterfaceInventory;
    }

    public void setInterfaceInventory(SolutionInterfaceInventory interfaceInventory) {
        InterfaceInventory = interfaceInventory;
    }

    public SolutionInventory getSolutionInventory() {
        return solutionInventory;
    }

    public void setSolutionInventory(SolutionInventory solutionInventory) {
        this.solutionInventory = solutionInventory;
    }
}
