package com.syscxp.header.vpn.agent;


public class DestroyVpnMsg extends VpnMessage {
    private String vpnVlan;
    private String vpnPort;
    private String tunnelInterface;

    public String getVpnVlan() {
        return vpnVlan;
    }

    public void setVpnVlan(String vpnVlan) {
        this.vpnVlan = vpnVlan;
    }

    public String getVpnPort() {
        return vpnPort;
    }

    public void setVpnPort(String vpnPort) {
        this.vpnPort = vpnPort;
    }

    public String getTunnelInterface() {
        return tunnelInterface;
    }

    public void setTunnelInterface(String tunnelInterface) {
        this.tunnelInterface = tunnelInterface;
    }
}
