package com.syscxp.header.vpn.agent;


public class ChangeVpnStatusMsg extends VpnMessage {
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
