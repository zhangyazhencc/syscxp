package com.syscxp.header.vpn.agent;

public class VpnPortMsg extends VpnMessage {
    private String vpnVlan;
    private String tunnelInterface;
    private String vpnPort;
    private String command;

    public String getVpnVlan() {
        return vpnVlan;
    }

    public void setVpnVlan(String vpnVlan) {
        this.vpnVlan = vpnVlan;
    }

    public String getTunnelInterface() {
        return tunnelInterface;
    }

    public void setTunnelInterface(String tunnelInterface) {
        this.tunnelInterface = tunnelInterface;
    }

    public String getVpnPort() {
        return vpnPort;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setVpnPort(String vpnPort) {
        this.vpnPort = vpnPort;
    }
}
