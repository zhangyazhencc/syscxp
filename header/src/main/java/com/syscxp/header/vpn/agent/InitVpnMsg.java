package com.syscxp.header.vpn.agent;


import com.syscxp.header.vpn.vpn.VpnVO;

public class InitVpnMsg extends VpnMessage {
    public String hostIp;
    public String vpnVlan;
    public String vpnPort;
    public String interfaceName;
    public String speed;
    public String username;
    public String passwd;

    public static InitVpnMsg valueOf(VpnVO vo) {
        InitVpnMsg msg = new InitVpnMsg();
        msg.setVpnUuid(vo.getUuid());
        msg.setHostIp(vo.getVpnHost().getHostIp());
        msg.setUsername(vo.getAccountUuid());
        msg.setPasswd(vo.getCertKey());
        msg.setVpnPort(vo.getPort().toString());
        msg.setVpnVlan(vo.getVlan().toString());
        return msg;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}
