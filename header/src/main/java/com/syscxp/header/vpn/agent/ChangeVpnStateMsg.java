package com.syscxp.header.vpn.agent;


import com.syscxp.header.vpn.vpn.VpnState;

public class ChangeVpnStateMsg extends VpnMessage {
    private VpnState currentState;
    private VpnState state;

    public VpnState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(VpnState currentState) {
        this.currentState = currentState;
    }

    public VpnState getState() {
        return state;
    }

    public void setState(VpnState state) {
        this.state = state;
    }
}
