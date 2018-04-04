package com.syscxp.header.idc.solution;

import com.syscxp.header.message.APIEvent;

public class APIUpdateSolutionTunnelEvent extends APIEvent {
    private SolutionTunnelInventory tunnelInventory;

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


}
