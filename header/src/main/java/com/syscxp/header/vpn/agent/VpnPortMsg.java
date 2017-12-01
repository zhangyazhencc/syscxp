package com.syscxp.header.vpn.agent;

public class VpnPortMsg extends VpnMessage {
    private String vpnVlan;
    private String interfaceName;
    private String vpnPort;
    private String command;

    public String getVpnVlan() {
        return vpnVlan;
    }

    public void setVpnVlan(String vpnVlan) {
        this.vpnVlan = vpnVlan;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
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
