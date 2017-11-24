package com.syscxp.header.tunnel.solution;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APIDeleteSolutionTunnelEvent extends APIEvent {
    private SolutionInventory solutionInventory;
    public APIDeleteSolutionTunnelEvent(String apiId) {
        super(apiId);
    }

    public APIDeleteSolutionTunnelEvent() {
        super(null);
    }

    public SolutionInventory getSolutionInventory() {
        return solutionInventory;
    }

    public void setSolutionInventory(SolutionInventory solutionInventory) {
        this.solutionInventory = solutionInventory;
    }
}
