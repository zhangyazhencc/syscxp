package org.zstack.header.vpn;

public class VpnAgentCommand {
    private String host_ip;
    private String public_ip;
    private String uuid;
    private Integer port;

    public String getPublicIp() {
        return public_ip;
    }

    public void setPublicIp(String publicIp) {
        this.public_ip = publicIp;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

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
