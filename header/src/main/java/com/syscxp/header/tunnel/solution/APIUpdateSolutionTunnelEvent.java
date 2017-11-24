package com.syscxp.header.tunnel.solution;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APIUpdateSolutionTunnelEvent extends APIEvent {
    private SolutionTunnelInventory TunnelInventory;
    private SolutionInventory solutionInventory;

    public APIUpdateSolutionTunnelEvent(String apiId) {
        super(apiId);
    }

    public APIUpdateSolutionTunnelEvent() {
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
