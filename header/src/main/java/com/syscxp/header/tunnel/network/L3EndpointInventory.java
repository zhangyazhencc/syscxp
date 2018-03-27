package com.syscxp.header.tunnel.network;

import com.syscxp.header.query.ExpandedQueries;
import com.syscxp.header.query.ExpandedQuery;
import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = L3EndpointVO.class)
@ExpandedQueries({
        @ExpandedQuery(expandedField = "l3Network", inventoryClass = L3NetworkInventory.class,
                foreignKey = "l3NetworkUuid", expandedInventoryKey = "uuid")})
public class L3EndpointInventory {

    private String uuid;
    private String l3NetworkUuid;
    private String l3NetworkName;
    private String endpointUuid;
    private String endpointName;
    private String bandwidthOffering;
    private Long bandwidth;
    private String routeType;
    private String state;
    private String status;
    private Integer maxRouteNum;
    private String localIP;
    private String remoteIp;
    private String monitorIp;
    private String netmask;
    private String ipCidr;
    private String interfaceUuid;
    private String switchPortUuid;
    private String physicalSwitchUuid;
    private Integer vlan;
    private String rd;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static L3EndpointInventory valueOf(L3EndpointVO vo){
        L3EndpointInventory inv = new L3EndpointInventory();
        inv.setUuid(vo.getUuid());
        inv.setL3NetworkUuid(vo.getL3NetworkUuid());
        inv.setEndpointUuid(vo.getEndpointUuid());
        inv.setBandwidthOffering(vo.getBandwidthOffering());
        inv.setBandwidth(vo.getBandwidth());
        inv.setRouteType(vo.getRouteType());
        inv.setState(vo.getState().toString());
        inv.setStatus(vo.getStatus().toString());
        inv.setMaxRouteNum(vo.getMaxRouteNum());
        inv.setLocalIP(vo.getLocalIP());
        inv.setRemoteIp(vo.getRemoteIp());
        inv.setMonitorIp(vo.getMonitorIp());
        inv.setNetmask(vo.getNetmask());
        inv.setIpCidr(vo.getIpCidr());
        inv.setInterfaceUuid(vo.getInterfaceUuid());
        inv.setSwitchPortUuid(vo.getSwitchPortUuid());
        inv.setPhysicalSwitchUuid(vo.getPhysicalSwitchUuid());
        inv.setVlan(vo.getVlan());
        inv.setRd(vo.getRd());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        inv.setL3NetworkName(vo.getL3NetworkVO().getName());
        inv.setEndpointName(vo.getEndpointEO().getName());

        return inv;
    }

    public static List<L3EndpointInventory> valueOf(Collection<L3EndpointVO> vos) {
        List<L3EndpointInventory> invs = new ArrayList<>(vos.size());
        for (L3EndpointVO vo : vos) {
            invs.add(L3EndpointInventory.valueOf(vo));
        }
        return invs;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public String getEndpointName() {
        return endpointName;
    }

    public void setEndpointName(String endpointName) {
        this.endpointName = endpointName;
    }

    public String getL3NetworkName() {
        return l3NetworkName;
    }

    public void setL3NetworkName(String l3NetworkName) {
        this.l3NetworkName = l3NetworkName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getIpCidr() {
        return ipCidr;
    }

    public void setIpCidr(String ipCidr) {
        this.ipCidr = ipCidr;
    }

    public String getMonitorIp() {
        return monitorIp;
    }

    public void setMonitorIp(String monitorIp) {
        this.monitorIp = monitorIp;
    }
}