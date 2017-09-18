package org.zstack.vpn.header.host;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
public class VpnHostVO {
    @Id
    @Column
    private String uuid;
    @Column
    private String name;
    @Column
    private String endpoint;
    @Column
    private String description;
    @Column
    private String publicIface;
    @Column
    private String tunnelIface;
    @Column
    private String hostIp;
    @Column
    private String sshPort;
    @Column
    @Enumerated(EnumType.STRING)
    private HostState state;

    @Column
    @Enumerated(EnumType.STRING)
    private HostStatus status;
    @Column
    private String username;
    @Column
    private String password;
    @Column
    private Timestamp lastOpDate;
    @Column
    private Timestamp createDate;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }


    public HostState getState() {
        return state;
    }

    public void setState(HostState state) {
        this.state = state;
    }

    public HostStatus getStatus() {
        return status;
    }

    public void setStatus(HostStatus status) {
        this.status = status;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublicIface() {
        return publicIface;
    }

    public void setPublicIface(String publicIface) {
        this.publicIface = publicIface;
    }

    public String getTunnelIface() {
        return tunnelIface;
    }

    public void setTunnelIface(String tunnelIface) {
        this.tunnelIface = tunnelIface;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public String getSshPort() {
        return sshPort;
    }

    public void setSshPort(String sshPort) {
        this.sshPort = sshPort;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
}
