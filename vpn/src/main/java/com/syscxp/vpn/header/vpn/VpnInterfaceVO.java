package com.syscxp.vpn.header.vpn;

import com.syscxp.header.vo.ForeignKey;
import com.syscxp.header.vo.ForeignKey.ReferenceOption;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
public class VpnInterfaceVO {
    @Id
    @Column
    private String uuid;
    @Column
    @ForeignKey(parentEntityClass = VpnVO.class, parentKey = "uuid", onDeleteAction = ReferenceOption.CASCADE)
    private String vpnUuid;
    @Column
    private String hostUuid;
    @Column
    private String name;
    @Column
    private String networkUuid;
    @Column
    private String localIp;
    @Column
    private String netmask;
    @Column
    private String vlan;
    @Column
    private Timestamp lastOpDate;
    @Column
    private Timestamp createDate;

    public String getHostUuid() {
        return hostUuid;
    }

    public void setHostUuid(String hostUuid) {
        this.hostUuid = hostUuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getVlan() {
        return vlan;
    }

    public void setVlan(String vlan) {
        this.vlan = vlan;
    }

    public String getVpnUuid() {
        return vpnUuid;
    }

    public void setVpnUuid(String vpnUuid) {
        this.vpnUuid = vpnUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNetworkUuid() {
        return networkUuid;
    }

    public void setNetworkUuid(String networkUuid) {
        this.networkUuid = networkUuid;
    }

    public String getLocalIp() {
        return localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    public String getNetmask() {
        return netmask;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
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
