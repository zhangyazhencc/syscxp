package com.syscxp.header.vpn.host;


import com.syscxp.header.core.encrypt.DECRYPT;
import com.syscxp.header.core.encrypt.ENCRYPTParam;
import com.syscxp.header.host.HostEO;
import com.syscxp.header.host.HostVO;
import com.syscxp.header.vo.EO;

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
    @ENCRYPTParam
    private String password;
    @Column
    private Integer startPort;
    @Column
    private Integer endPort;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "zoneUuid", insertable = false, updatable = false)
    private ZoneVO zone;

    @Column
    private String zoneUuid;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "hostUuid", insertable = false, updatable = false)
    private List<HostInterfaceVO> hostInterfaces;

    public VpnHostVO() {
    }

    public VpnHostVO(HostVO vo) {
        super(vo);
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

    public String getZoneUuid() {
        return zoneUuid;
    }

    public void setZoneUuid(String zoneUuid) {
        this.zoneUuid = zoneUuid;
    }

    public ZoneVO getZone() {
        return zone;
    }

    public void setZone(ZoneVO zone) {
        this.zone = zone;
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

    @DECRYPT
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
