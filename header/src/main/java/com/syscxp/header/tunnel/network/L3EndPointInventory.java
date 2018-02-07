package com.syscxp.header.tunnel.network;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = L3EndPointVO.class)
public class L3EndPointInventory {

    private String uuid;
    private String l3NetworkUuid;
    private String endpointUuid;
    private Long bandwidth;
    private String routeType;
    private String status;
    private Long maxRouteNum;
    private String localIP;
    private String remoteIp;
    private String netmask;
    private String interfaceUuid;
    private String switchPortUuid;
    private Long vlan;
    private String rd;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static L3EndPointInventory valueOf(L3EndPointVO vo){
        L3EndPointInventory inv = new L3EndPointInventory();
        inv.setUuid(vo.getUuid());

        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());

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
}
