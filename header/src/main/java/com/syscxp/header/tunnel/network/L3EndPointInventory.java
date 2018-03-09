package com.syscxp.header.tunnel.network;

import com.syscxp.header.search.Inventory;
import com.syscxp.header.tunnel.endpoint.EndpointInventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = L3EndPointVO.class)
public class L3EndPointInventory {

    private String uuid;
    private String l3NetworkUuid;
    private String endpointUuid;
    private String bandwidthOffering;
    private Long bandwidth;
    private String routeType;
    private String status;
    private Integer maxRouteNum;
    private String localIP;
    private String remoteIp;
    private String netmask;
    private String interfaceUuid;
    private String switchPortUuid;
    private String physicalSwitchUuid;
    private Integer vlan;
    private String rd;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    private EndpointInventory endpoint;
    private List<L3RouteInventory> l3Routes = new ArrayList<L3RouteInventory>();
    private List<L3RtInventory> l3Rts = new ArrayList<L3RtInventory>();

    public static L3EndPointInventory valueOf(L3EndPointVO vo){
        L3EndPointInventory inv = new L3EndPointInventory();
        inv.setUuid(vo.getUuid());
        inv.setL3NetworkUuid(vo.getL3NetworkUuid());
        inv.setEndpointUuid(vo.getEndpointUuid());
        inv.setBandwidthOffering(vo.getBandwidthOffering());
        inv.setBandwidth(vo.getBandwidth());
        inv.setRouteType(vo.getRouteType());
        inv.setStatus(vo.getStatus().toString());
        inv.setMaxRouteNum(vo.getMaxRouteNum());
        inv.setLocalIP(vo.getLocalIP());
        inv.setRemoteIp(vo.getRemoteIp());
        inv.setNetmask(vo.getNetmask());
        inv.setInterfaceUuid(vo.getInterfaceUuid());
        inv.setSwitchPortUuid(vo.getSwitchPortUuid());
        inv.setPhysicalSwitchUuid(vo.getPhysicalSwitchUuid());
        inv.setVlan(vo.getVlan());
        inv.setRd(vo.getRd());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());

        inv.setEndpoint(EndpointInventory.valueOf(vo.getEndpointVO()));
        inv.setL3Routes(L3RouteInventory.valueOf(vo.getL3RouteVOS()));
        inv.setL3Rts(L3RtInventory.valueOf(vo.getL3RtVOS()));

        return inv;
    }

    public static List<L3EndPointInventory> valueOf(Collection<L3EndPointVO> vos) {
        List<L3EndPointInventory> invs = new ArrayList<>(vos.size());
        for (L3EndPointVO vo : vos) {
            invs.add(L3EndPointInventory.valueOf(vo));
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

    public EndpointInventory getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(EndpointInventory endpoint) {
        this.endpoint = endpoint;
    }

    public List<L3RouteInventory> getL3Routes() {
        return l3Routes;
    }

    public void setL3Routes(List<L3RouteInventory> l3Routes) {
        this.l3Routes = l3Routes;
    }

    public List<L3RtInventory> getL3Rts() {
        return l3Rts;
    }

    public void setL3Rts(List<L3RtInventory> l3Rts) {
        this.l3Rts = l3Rts;
    }
}
