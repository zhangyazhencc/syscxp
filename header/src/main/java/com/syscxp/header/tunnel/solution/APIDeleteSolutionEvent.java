package com.syscxp.header.tunnel.solution;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

public class APIDeleteSolutionEvent extends APIEvent {
    private SolutionInventory solutionInventory;
    public APIDeleteSolutionEvent(String apiId) {
        super(apiId);
    }

    public APIDeleteSolutionEvent() {
        super(null);
    }

    public SolutionInventory getSolutionInventory() {
        return solutionInventory;
    }

    public void setSolutionInventory(SolutionInventory solutionInventory) {
        this.solutionInventory = solutionInventory;
    }
}
