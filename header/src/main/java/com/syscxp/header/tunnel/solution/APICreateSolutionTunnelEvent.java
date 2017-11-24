package com.syscxp.header.tunnel.solution;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APICreateSolutionTunnelEvent extends APIEvent {
    private SolutionTunnelInventory tunnelInventory;
    private SolutionInventory solutionInventory;

    public APICreateSolutionTunnelEvent(String apiId) {
        super(apiId);
    }

    public APICreateSolutionTunnelEvent() {
        super(null);
    }

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
