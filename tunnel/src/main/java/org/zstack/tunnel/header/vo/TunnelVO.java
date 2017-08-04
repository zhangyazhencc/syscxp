package org.zstack.tunnel.header.vo;

import org.zstack.header.search.SqlTrigger;
import org.zstack.header.search.TriggerIndex;
import org.zstack.header.vo.ForeignKey;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
@Inheritance(strategy= InheritanceType.JOINED)
@TriggerIndex
@SqlTrigger
public class TunnelVO {

    @Id
    @Column
    private String uuid;

    @Column
    @org.zstack.header.vo.ForeignKey(parentEntityClass = NetworkTypeVO.class, parentKey = "uuid", onDeleteAction = ForeignKey.ReferenceOption.CASCADE)
    private String networkTypetUuid;

    @Column
    @org.zstack.header.vo.ForeignKey(parentEntityClass = EndpointVO.class, parentKey = "uuid", onDeleteAction = ForeignKey.ReferenceOption.CASCADE)
    private String endpointA;

    @Column
    @org.zstack.header.vo.ForeignKey(parentEntityClass = EndpointVO.class, parentKey = "uuid", onDeleteAction = ForeignKey.ReferenceOption.CASCADE)
    private String endpointB;

    @Column
    private String endpointAType;

    @Column
    private String endpointBType;

    @Column
    private String projectUuid;

    @Column
    private String code;

    @Column
    private String name;

    @Column
    private Integer bandwidth;

    @Column
    private Integer vni;

    @Column
    private double distance;

    @Column
    private String state;

    @Column
    private String status;

    @Column
    private String priExclusive;

    @Column
    private Integer alarmed;

    @Column
    private Integer deleted;

    @Column
    private Timestamp billingDate;

    @Column
    private Timestamp lastOpDate;

    @Column
    private Timestamp createDate;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNetworkTypetUuid() {
        return networkTypetUuid;
    }

    public void setNetworkTypetUuid(String networkTypetUuid) {
        this.networkTypetUuid = networkTypetUuid;
    }

    public String getEndpointA() {
        return endpointA;
    }

    public void setEndpointA(String endpointA) {
        this.endpointA = endpointA;
    }

    public String getEndpointB() {
        return endpointB;
    }

    public void setEndpointB(String endpointB) {
        this.endpointB = endpointB;
    }

    public String getEndpointAType() {
        return endpointAType;
    }

    public void setEndpointAType(String endpointAType) {
        this.endpointAType = endpointAType;
    }

    public String getEndpointBType() {
        return endpointBType;
    }

    public void setEndpointBType(String endpointBType) {
        this.endpointBType = endpointBType;
    }

    public String getProjectUuid() {
        return projectUuid;
    }

    public void setProjectUuid(String projectUuid) {
        this.projectUuid = projectUuid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Integer bandwidth) {
        this.bandwidth = bandwidth;
    }

    public Integer getVni() {
        return vni;
    }

    public void setVni(Integer vni) {
        this.vni = vni;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
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

    public String getPriExclusive() {
        return priExclusive;
    }

    public void setPriExclusive(String priExclusive) {
        this.priExclusive = priExclusive;
    }

    public Integer getAlarmed() {
        return alarmed;
    }

    public void setAlarmed(Integer alarmed) {
        this.alarmed = alarmed;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public Timestamp getBillingDate() {
        return billingDate;
    }

    public void setBillingDate(Timestamp billingDate) {
        this.billingDate = billingDate;
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
