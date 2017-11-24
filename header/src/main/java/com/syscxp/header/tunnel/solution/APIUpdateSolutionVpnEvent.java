package com.syscxp.header.tunnel.solution;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APIUpdateSolutionVpnEvent extends APIEvent {
    private SolutionVpnInventory VPNInventory;
    private SolutionInventory solutionInventory;

    public APIUpdateSolutionVpnEvent(String apiId) {
        super(apiId);
    }

    public APIUpdateSolutionVpnEvent() {
        super(null);
    }

    public SolutionVpnInventory getVPNInventory() {
        return VPNInventory;
    }

    public void setVPNInventory(SolutionVpnInventory VPNInventory) {
        this.VPNInventory = VPNInventory;
    }

    public SolutionInventory getSolutionInventory() {
        return solutionInventory;
    }

    public void setSolutionInventory(SolutionInventory solutionInventory) {
        this.solutionInventory = solutionInventory;
    }
}
