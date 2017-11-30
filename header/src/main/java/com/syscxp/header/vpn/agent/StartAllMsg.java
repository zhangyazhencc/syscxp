package com.syscxp.header.vpn.agent;

public class StartAllMsg extends VpnMessage {
    private String vpnVlan;
    private String vpnPort;
    private String interfaceName;
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

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }
}
