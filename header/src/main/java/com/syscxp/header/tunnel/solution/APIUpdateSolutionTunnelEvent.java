package com.syscxp.header.tunnel.solution;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(fieldsTo ={"tunnelInventory","solutionInventory"})
public class APIUpdateSolutionTunnelEvent extends APIEvent {
    private SolutionTunnelInventory tunnelInventory;
    private SolutionInventory solutionInventory;

    public APIUpdateSolutionTunnelEvent(String apiId) {
        super(apiId);
    }

    public APIUpdateSolutionTunnelEvent() {
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
