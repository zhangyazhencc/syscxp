package com.syscxp.header.tunnel.network;

import com.syscxp.header.tunnel.endpoint.EndpointVO;
import com.syscxp.header.vo.ForeignKey;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
public class L3EndPointVO {

    @Id
    @Column
    private String uuid;

    @Column
    @ForeignKey(parentEntityClass = L3NetworkEO.class, onDeleteAction = ForeignKey.ReferenceOption.SET_NULL)
    private String l3NetworkUuid;

    @Column
    private String endpointUuid;

    @Column
    private String bandwidthOffering;

    @Column
    private Long bandwidth;

    @Column
    private String routeType;

    @Column
    @Enumerated(EnumType.STRING)
    private L3EndpointState state;

    @Column
    @Enumerated(EnumType.STRING)
    private L3EndpointStatus status;

    @Column
    private Integer maxRouteNum;

    @Column
    private String localIP;

    @Column
    private String remoteIp;

    @Column
    private String netmask;

    @Column
    private String ipCidr;

    @Column
    private String interfaceUuid;

    @Column
    private String switchPortUuid;

    @Column
    private String physicalSwitchUuid;

    @Column
    private Integer vlan;

    @Column
    private String rd;

    @Column
    private Timestamp lastOpDate;

    @Column
    private Timestamp createDate;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="endpointUuid", insertable=false, updatable=false)
    private EndpointVO endpointVO;

    @OneToMany(fetch= FetchType.EAGER)
    @JoinColumn(name = "l3EndPointUuid", insertable = false, updatable = false)
    private List<L3RtVO> l3RtVOS = new ArrayList<L3RtVO>();

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

    public String getBandwidthOffering() {
        return bandwidthOffering;
    }

    public void setBandwidthOffering(String bandwidthOffering) {
        this.bandwidthOffering = bandwidthOffering;
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

    public L3EndpointStatus getStatus() {
        return status;
    }

    public void setStatus(L3EndpointStatus status) {
        this.status = status;
    }

    public Integer getMaxRouteNum() {
        return maxRouteNum;
    }

    public void setMaxRouteNum(Integer maxRouteNum) {
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

    public String getPhysicalSwitchUuid() {
        return physicalSwitchUuid;
    }

    public void setPhysicalSwitchUuid(String physicalSwitchUuid) {
        this.physicalSwitchUuid = physicalSwitchUuid;
    }

    public Integer getVlan() {
        return vlan;
    }

    public void setVlan(Integer vlan) {
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

    public EndpointVO getEndpointVO() {
        return endpointVO;
    }

    public void setEndpointVO(EndpointVO endpointVO) {
        this.endpointVO = endpointVO;
    }

    public List<L3RtVO> getL3RtVOS() {
        return l3RtVOS;
    }

    public void setL3RtVOS(List<L3RtVO> l3RtVOS) {
        this.l3RtVOS = l3RtVOS;
    }

    public L3EndpointState getState() {
        return state;
    }

    public void setState(L3EndpointState state) {
        this.state = state;
    }

    public String getIpCidr() {
        return ipCidr;
    }

    public void setIpCidr(String ipCidr) {
        this.ipCidr = ipCidr;
    }
}
