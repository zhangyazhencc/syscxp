package com.syscxp.header.tunnel.solution;

import com.syscxp.header.query.APIQueryReply;

public class APIRecountTunnelPriceReply extends APIQueryReply {
    private SolutionTunnelInventory tunnelInventory;
    private SolutionInventory solutionInventory;

    public SolutionTunnelInventory getTunnelInventory() {
        return tunnelInventory;
    }

    public void setTunnelInventory(SolutionTunnelInventory tunnelInventory) {
        this.tunnelInventory = tunnelInventory;
    }

    public SolutionInventory getSolutionInventory() {
        return solutionInventory;
    }

    public void setSolutionInventory(SolutionInventory solutionInventory) {
        this.solutionInventory = solutionInventory;
    }
}
