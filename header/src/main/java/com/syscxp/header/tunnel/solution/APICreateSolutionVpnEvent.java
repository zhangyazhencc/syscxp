package com.syscxp.header.tunnel.solution;

import com.syscxp.header.message.APIEvent;

public class APICreateSolutionVpnEvent extends APIEvent {
    private SolutionVpnInventory vpnInventory;
    private SolutionInventory solutionInventory;

    public APICreateSolutionVpnEvent(String apiId) {
        super(apiId);
    }

    public APICreateSolutionVpnEvent() {
        super(null);
    }

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
