package com.syscxp.header.vpn.agent;

public class VpnConfMsg extends VpnMessage {
    private String hostIp;
    private String vpnPort;

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public String getVpnPort() {
        return vpnPort;
    }

    public void setVpnPort(String vpnPort) {
        this.vpnPort = vpnPort;
    }
}
