package com.syscxp.header.tunnel.network;

import com.syscxp.header.vo.ForeignKey;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table
public class L3EndPointVO {

    @Id
    @Column
    private String uuid;
    @Column
    @ForeignKey(parentEntityClass = L3NetworkVO.class, parentKey = "uuid", onDeleteAction = ForeignKey.ReferenceOption.CASCADE)
    private String l3NetworkUuid;
    @Column
    private String endpointUuid;
    @Column
    private Long bandwidth;
    @Column
    private String routeType;
    @Column
    private String status;
    @Column
    private Long maxRouteNum;
    @Column
    private String localIP;
    @Column
    private String remoteIp;
    @Column
    private String netmask;
    @Column
    private String interfaceUuid;
    @Column
    private String switchPortUuid;
    @Column
    private Long vlan;
    @Column
    private String rd;
    @Column
    private Timestamp lastOpDate;
    @Column
    private Timestamp createDate;

    @OneToMany(targetEntity = L3RouteVO.class,orphanRemoval=true)
    private Set<L3RouteVO> l3RouteVOs;

    @OneToMany(targetEntity = L3RtVO.class,orphanRemoval=true)
    private Set<L3RtVO> l3RtVOs;

    @PreUpdate
    private void preUpdate() {
        lastOpDate = null;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getL3NetworkUuid() {
        return l3NetworkUuid;
    }

    public void setL3NetworkUuid(String l3NetworkUuid) {
        this.l3NetworkUuid = l3NetworkUuid;
    }

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
    }

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
    }

    public String getRouteType() {
        return routeType;
    }

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getMaxRouteNum() {
        return maxRouteNum;
    }

    public void setMaxRouteNum(Long maxRouteNum) {
        this.maxRouteNum = maxRouteNum;
    }

    public String getLocalIP() {
        return localIP;
    }

    public void setLocalIP(String localIP) {
        this.localIP = localIP;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public String getNetmask() {
        return netmask;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    public String getInterfaceUuid() {
        return interfaceUuid;
    }

    public void setInterfaceUuid(String interfaceUuid) {
        this.interfaceUuid = interfaceUuid;
    }

    public String getSwitchPortUuid() {
        return switchPortUuid;
    }

    public void setSwitchPortUuid(String switchPortUuid) {
        this.switchPortUuid = switchPortUuid;
    }

    public Long getVlan() {
        return vlan;
    }

    public void setVlan(Long vlan) {
        this.vlan = vlan;
    }

    public String getRd() {
        return rd;
    }

    public void setRd(String rd) {
        this.rd = rd;
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

    public Set<L3RouteVO> getL3RouteVOs() {
        return l3RouteVOs;
    }

    public void setL3RouteVOs(Set<L3RouteVO> l3RouteVOs) {
        this.l3RouteVOs = l3RouteVOs;
    }

    public Set<L3RtVO> getL3RtVOs() {
        return l3RtVOs;
    }

    public void setL3RtVOs(Set<L3RtVO> l3RtVOs) {
        this.l3RtVOs = l3RtVOs;
    }
}
