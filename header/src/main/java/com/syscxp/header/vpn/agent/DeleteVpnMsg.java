package com.syscxp.header.vpn.agent;


public class DeleteVpnMsg extends VpnMessage {
    private boolean expired;

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }
}
