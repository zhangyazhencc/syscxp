package com.syscxp.header.tunnel.solution;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "solutionInventory")
public class APIDeleteSolutionInterfaceEvent extends APIEvent {
    private SolutionInventory solutionInventory;
    public APIDeleteSolutionInterfaceEvent(String apiId) {
        super(apiId);
    }

    public APIDeleteSolutionInterfaceEvent() {
        super(null);
    }

    public SolutionInventory getSolutionInventory() {
        return solutionInventory;
    }

    public void setSolutionInventory(SolutionInventory solutionInventory) {
        this.solutionInventory = solutionInventory;
    }
}
