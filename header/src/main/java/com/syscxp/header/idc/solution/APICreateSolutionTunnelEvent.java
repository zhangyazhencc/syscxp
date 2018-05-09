package com.syscxp.header.idc.solution;

import com.syscxp.header.message.APIEvent;

public class APICreateSolutionTunnelEvent extends APIEvent {
    private SolutionTunnelInventory tunnelInventory;

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


}
