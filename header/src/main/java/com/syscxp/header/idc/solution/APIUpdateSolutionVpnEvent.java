package com.syscxp.header.idc.solution;

import com.syscxp.header.message.APIEvent;

public class APIUpdateSolutionVpnEvent extends APIEvent {
    private SolutionVpnInventory vpnInventory;

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
}
