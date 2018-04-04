package com.syscxp.header.idc.solution;

import com.syscxp.header.message.APIEvent;

public class APICreateSolutionVpnEvent extends APIEvent {
    private SolutionVpnInventory vpnInventory;

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
}
