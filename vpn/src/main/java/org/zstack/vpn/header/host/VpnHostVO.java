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
    private String endpointUuid;
    @Column
    private String description;
    @Column
    private String publicInterface;
    @Column
    private String publicIp;
    @Column
    private String tunnelInterface;
    @Column
    private String manageIp;
    @Column
    private String sshPort;
    @Column
    @Enumerated(EnumType.STRING)
    private VpnState state;
    @Column
    @Enumerated(EnumType.STRING)
    private VpnStatus status;
    @Column
    private String username;
    @Column
    private String password;
    @Column
    private Timestamp lastOpDate;
    @Column
    private Timestamp createDate;

    public String getPublicIp() {
        return publicIp;
    }

    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
    }


    public VpnState getState() {
        return state;
    }

    public void setState(VpnState state) {
        this.state = state;
    }

    public VpnStatus getStatus() {
        return status;
    }

    public void setStatus(VpnStatus status) {
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

    public String getPublicInterface() {
        return publicInterface;
    }

    public void setPublicInterface(String publicInterface) {
        this.publicInterface = publicInterface;
    }

    public String getTunnelInterface() {
        return tunnelInterface;
    }

    public void setTunnelInterface(String tunnelInterface) {
        this.tunnelInterface = tunnelInterface;
    }

    public String getManageIp() {
        return manageIp;
    }

    public void setManageIp(String manageIp) {
        this.manageIp = manageIp;
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