package com.syscxp.kvm;

public class KVMHostInventory {

    private String zoneUuid;
    private String zoneName;
    private String name;
    private String uuid;
    private String clusterUuid;
    private String managementIp;
    private String hypervisorType;
    private String state;
    private String status;
    private Long totalMemoryCapacity;
    private String publicPhysicalInterface;
    private String privatePhysicalInterface;
    private String tunnelPhysicalInterface;
    private String username;
    private Integer sshPort;
    private String hostType;
    private String clusterType;

    public String getZoneUuid() {
        return zoneUuid;
    }

    public void setZoneUuid(String zoneUuid) {
        this.zoneUuid = zoneUuid;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getClusterUuid() {
        return clusterUuid;
    }

    public void setClusterUuid(String clusterUuid) {
        this.clusterUuid = clusterUuid;
    }

    public String getManagementIp() {
        return managementIp;
    }

    public void setManagementIp(String managementIp) {
        this.managementIp = managementIp;
    }

    public String getHypervisorType() {
        return hypervisorType;
    }

    public void setHypervisorType(String hypervisorType) {
        this.hypervisorType = hypervisorType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTotalMemoryCapacity() {
        return totalMemoryCapacity;
    }

    public void setTotalMemoryCapacity(Long totalMemoryCapacity) {
        this.totalMemoryCapacity = totalMemoryCapacity;
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

    public String getHostType() {
        return hostType;
    }

    public void setHostType(String hostType) {
        this.hostType = hostType;
    }

    public String getClusterType() {
        return clusterType;
    }

    public void setClusterType(String clusterType) {
        this.clusterType = clusterType;
    }
}
