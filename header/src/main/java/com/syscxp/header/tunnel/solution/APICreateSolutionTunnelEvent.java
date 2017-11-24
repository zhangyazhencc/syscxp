package com.syscxp.header.tunnel.solution;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APICreateSolutionTunnelEvent extends APIEvent {
    private SolutionTunnelInventory TunnelInventory;
    private SolutionInventory solutionInventory;

    public APICreateSolutionTunnelEvent(String apiId) {
        super(apiId);
    }

    public APICreateSolutionTunnelEvent() {
        super(null);
    }

    public SolutionTunnelInventory getTunnelInventory() {
        return TunnelInventory;
    }

    public void setTunnelInventory(SolutionTunnelInventory tunnelInventory) {
        TunnelInventory = tunnelInventory;
    }

    public SolutionInventory getSolutionInventory() {
        return solutionInventory;
    }

    public void setSolutionInventory(SolutionInventory solutionInventory) {
        this.solutionInventory = solutionInventory;
    }
}
