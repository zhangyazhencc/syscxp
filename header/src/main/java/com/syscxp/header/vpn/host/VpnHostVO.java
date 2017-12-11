package com.syscxp.header.vpn.host;


import com.syscxp.header.host.HostVO;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
@PrimaryKeyJoinColumn(name = "uuid", referencedColumnName = "uuid")
public class VpnHostVO extends HostVO {
    @Column
    private String publicIp;
    @Column
    private Integer sshPort;
    @Column
    private String username;
    @Column
    private String password;
    @Column
    private String nodeUuid;
    @Column
    private Integer startPort;
    @Column
    private Integer endPort;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "hostUuid", insertable = false, updatable = false)
    private List<HostInterfaceVO> hostInterfaces;

    public VpnHostVO() {
    }

    public VpnHostVO(HostVO vo) {
        super(vo);
    }

    public String getNodeUuid() {
        return nodeUuid;
    }

    public void setNodeUuid(String nodeUuid) {
        this.nodeUuid = nodeUuid;
    }

    public Integer getEndPort() {
        return endPort;
    }

    public void setEndPort(Integer endPort) {
        this.endPort = endPort;
    }

    public Integer getStartPort() {
        return startPort;
    }

    public void setStartPort(Integer startPort) {
        this.startPort = startPort;
    }

    public List<HostInterfaceVO> getHostInterfaces() {
        return hostInterfaces;
    }

    public void setHostInterfaces(List<HostInterfaceVO> hostInterfaces) {
        this.hostInterfaces = hostInterfaces;
    }

    public String getPublicIp() {
        return publicIp;
    }

    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getSshPort() {
        return sshPort;
    }

    public void setSshPort(Integer sshPort) {
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

}
