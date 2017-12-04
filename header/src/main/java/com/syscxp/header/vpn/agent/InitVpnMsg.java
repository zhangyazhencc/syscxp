package com.syscxp.header.vpn.agent;


import com.syscxp.header.vpn.vpn.VpnVO;

public class InitVpnMsg extends VpnMessage {
    public String hostIp;
    public String vpnVlan;
    public String vpnPort;
    public String interfaceName;
    public String speed;
    public CertInfo certInfo;

    public static InitVpnMsg valueOf(VpnVO vo) {
        InitVpnMsg msg = new InitVpnMsg();
        msg.setVpnUuid(vo.getUuid());
        msg.setHostIp(vo.getVpnHost().getHostIp());
        msg.setVpnPort(vo.getPort().toString());
        msg.setVpnVlan(vo.getVlan().toString());
        return msg;
    }

    public CertInfo getCertInfo() {
        return certInfo;
    }

    public void setCertInfo(CertInfo certInfo) {
        this.certInfo = certInfo;
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

}
