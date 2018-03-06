package com.syscxp.header.tunnel.solution;


import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIListShareSolutionReply extends APIQueryReply {
    private List<SolutionInventory> solutionInventories;

    public List<SolutionInventory> getSolutionInventories() {
        return solutionInventories;
    }

    public void setSolutionInventories(List<SolutionInventory> solutionInventories) {
        this.solutionInventories = solutionInventories;
    }
}
