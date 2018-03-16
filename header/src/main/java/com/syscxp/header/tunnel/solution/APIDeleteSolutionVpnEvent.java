package com.syscxp.header.tunnel.solution;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

public class APIDeleteSolutionVpnEvent extends APIEvent {
    private SolutionInventory solutionInventory;
    public APIDeleteSolutionVpnEvent(String apiId) {
        super(apiId);
    }

    public APIDeleteSolutionVpnEvent() {
        super(null);
    }

    public SolutionInventory getSolutionInventory() {
        return solutionInventory;
    }

    public void setSolutionInventory(SolutionInventory solutionInventory) {
        this.solutionInventory = solutionInventory;
    }
}
