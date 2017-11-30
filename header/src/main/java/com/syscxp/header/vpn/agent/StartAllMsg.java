package com.syscxp.header.vpn.agent;

public class StartAllMsg extends VpnMessage {
    private String vpnVlan;
    private String vpnPort;
    private String tunnelInterface;
    private String speed;

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

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }
}
