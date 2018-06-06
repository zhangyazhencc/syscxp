package com.syscxp.kvm;

public class KVMHostInventory {

    private String zoneUuid;
    private String uuid;
    private String managementIp;
    private String status;
    private String publicPhysicalInterface;
    private String privatePhysicalInterface;
    private String tunnelPhysicalInterface;
    private String username;
    private Integer sshPort;

    public String getZoneUuid() {
        return zoneUuid;
    }

    public void setZoneUuid(String zoneUuid) {
        this.zoneUuid = zoneUuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getManagementIp() {
        return managementIp;
    }

    public void setManagementIp(String managementIp) {
        this.managementIp = managementIp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPublicPhysicalInterface() {
        return publicPhysicalInterface;
    }

    public void setPublicPhysicalInterface(String publicPhysicalInterface) {
        this.publicPhysicalInterface = publicPhysicalInterface;
    }

    public String getPrivatePhysicalInterface() {
        return privatePhysicalInterface;
    }

    public void setPrivatePhysicalInterface(String privatePhysicalInterface) {
        this.privatePhysicalInterface = privatePhysicalInterface;
    }

    public String getTunnelPhysicalInterface() {
        return tunnelPhysicalInterface;
    }

    public void setTunnelPhysicalInterface(String tunnelPhysicalInterface) {
        this.tunnelPhysicalInterface = tunnelPhysicalInterface;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getSshPort() {
        return sshPort;
    }

    public void setSshPort(Integer sshPort) {
        this.sshPort = sshPort;
    }
}
