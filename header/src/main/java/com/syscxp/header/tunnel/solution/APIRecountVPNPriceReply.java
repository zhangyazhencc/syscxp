package com.syscxp.header.tunnel.solution;

import com.syscxp.header.query.APIQueryReply;

public class APIRecountVPNPriceReply extends APIQueryReply  {
 private SolutionVpnInventory vpnInventory;
 private SolutionInventory solutionInventory;

    public SolutionVpnInventory getVpnInventory() {
        return vpnInventory;
    }

    public void setVpnInventory(SolutionVpnInventory vpnInventory) {
        this.vpnInventory = vpnInventory;
    }

    public SolutionInventory getSolutionInventory() {
        return solutionInventory;
    }

    public void setSolutionInventory(SolutionInventory solutionInventory) {
        this.solutionInventory = solutionInventory;
    }
}
