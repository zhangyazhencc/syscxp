package com.syscxp.header.tunnel.solution;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APIUpdateSolutionVpnEvent extends APIEvent {
    private SolutionVpnInventory vpnInventory;
    private SolutionInventory solutionInventory;

    public APIUpdateSolutionVpnEvent(String apiId) {
        super(apiId);
    }

    public APIUpdateSolutionVpnEvent() {
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
