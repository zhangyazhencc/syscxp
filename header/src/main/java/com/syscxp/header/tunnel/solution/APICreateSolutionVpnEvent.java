package com.syscxp.header.tunnel.solution;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APICreateSolutionVpnEvent extends APIEvent {
    private SolutionVpnInventory VPNInventory;
    private SolutionInventory solutionInventory;

    public APICreateSolutionVpnEvent(String apiId) {
        super(apiId);
    }

    public APICreateSolutionVpnEvent() {
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
