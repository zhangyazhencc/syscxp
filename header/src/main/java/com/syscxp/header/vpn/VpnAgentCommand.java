package com.syscxp.header.vpn;

public class VpnAgentCommand {
    private String host_ip;
    private String uuid;
    private Integer port;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getHostIp() {
        return host_ip;
    }

    public void setHostIp(String hostIp) {
        this.host_ip = hostIp;
    }

    public String getVpnUuid() {
        return uuid;
    }

    public void setVpnUuid(String vpnUuid) {
        this.uuid = vpnUuid;
    }
}
