package com.syscxp.header.tunnel.solution;

import com.syscxp.header.message.APIEvent;

import java.util.List;

public class APIGetShareSolutionEvent extends APIEvent {
    private List<SolutionInventory> solutionInventories;

    public APIGetShareSolutionEvent() {
    }

    public APIGetShareSolutionEvent(String apiId) {
        super(apiId);
    }

    public List<SolutionInventory> getSolutionInventories() {
        return solutionInventories;
    }

    public void setSolutionInventories(List<SolutionInventory> solutionInventories) {
        this.solutionInventories = solutionInventories;
    }
}
