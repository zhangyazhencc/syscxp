package org.zstack.tunnel.header.inventory;

import org.zstack.header.query.ExpandedQueries;
import org.zstack.header.query.ExpandedQuery;
import org.zstack.header.search.Inventory;
import org.zstack.tunnel.header.vo.TunnelVO;

import java.sql.Timestamp;

@Inventory(mappingVOClass = TunnelVO.class)
@ExpandedQueries({
        @ExpandedQuery(expandedField = "endpoint", inventoryClass = EndpointInventory.class,
                foreignKey = "uuid", expandedInventoryKey = "endpointA"),
        @ExpandedQuery(expandedField = "endpoint", inventoryClass = EndpointInventory.class,
                foreignKey = "uuid", expandedInventoryKey = "endpointB"),
        @ExpandedQuery(expandedField = "endpoint", inventoryClass = EndpointInventory.class,
                foreignKey = "uuid", expandedInventoryKey = "networkTypetUuid"),
})
public class TunnelInventory {

    private String uuid;
    private String networkTypetUuid;
    private String endpointA;
    private String endpointB;
    private String endpointAType;
    private String endpointBType;
    private String projectUuid;
    private String code;
    private String name;
    private Integer bandwidth;
    private Integer vni;
    private double distance;
    private String state;
    private String status;
    private String priExclusive;
    private Integer alarmed;
    private Integer deleted;
    private Timestamp billingDate;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static TunnelInventory valueOf(TunnelVO vo) {
        TunnelInventory inv = new TunnelInventory();
        inv.setUuid(vo.getUuid());
        inv.setNetworkTypetUuid(vo.getNetworkTypetUuid());
        inv.setEndpointA(vo.getEndpointA());
        inv.setEndpointB(vo.getEndpointB());
        inv.setEndpointAType(vo.getEndpointAType());
        inv.setEndpointBType(vo.getEndpointBType());
        inv.setProjectUuid(vo.getProjectUuid());
        inv.setCode(vo.getCode());
        inv.setName(vo.getName());
        inv.setBandwidth(vo.getBandwidth());
        inv.setDistance(vo.getDistance());
        inv.setState(vo.getState());
        inv.setStatus(vo.getStatus());
        inv.setPriExclusive(vo.getPriExclusive());
        inv.setDeleted(vo.getDeleted());
        inv.setBillingDate(vo.getBillingDate());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        return inv;
    }

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
