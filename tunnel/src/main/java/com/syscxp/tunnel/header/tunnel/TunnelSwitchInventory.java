package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.search.Inventory;
import com.syscxp.tunnel.header.endpoint.EndpointInventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by DCY on 2017-11-01
 */
@Inventory(mappingVOClass = TunnelSwitchVO.class)
public class TunnelSwitchInventory {

    private String uuid;
    private String tunnelUuid;
    private String endpointUuid;
    private EndpointInventory endpoint;
    private String switchPortUuid;
    private NetworkType type;
    private Integer vlan;
    private String sortTag;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static TunnelSwitchInventory valueOf(TunnelSwitchVO vo){
        TunnelSwitchInventory inv = new TunnelSwitchInventory();
        inv.setUuid(vo.getUuid());
        inv.setTunnelUuid(vo.getTunnelUuid());
        inv.setEndpointUuid(vo.getEndpointUuid());
        inv.setEndpoint(EndpointInventory.valueOf(vo.getEndpointVO()));
        inv.setSwitchPortUuid(vo.getSwitchPortUuid());
        inv.setType(vo.getType());
        inv.setVlan(vo.getVlan());
        inv.setSortTag(vo.getSortTag());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<TunnelSwitchInventory> valueOf(Collection<TunnelSwitchVO> vos) {
        List<TunnelSwitchInventory> lst = new ArrayList<TunnelSwitchInventory>(vos.size());
        for (TunnelSwitchVO vo : vos) {
            lst.add(TunnelSwitchInventory.valueOf(vo));
        }
        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
    }

    public EndpointInventory getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(EndpointInventory endpoint) {
        this.endpoint = endpoint;
    }

    public String getSwitchPortUuid() {
        return switchPortUuid;
    }

    public void setSwitchPortUuid(String switchPortUuid) {
        this.switchPortUuid = switchPortUuid;
    }

    public NetworkType getType() {
        return type;
    }

    public void setType(NetworkType type) {
        this.type = type;
    }

    public Integer getVlan() {
        return vlan;
    }

    public void setVlan(Integer vlan) {
        this.vlan = vlan;
    }

    public String getSortTag() {
        return sortTag;
    }

    public void setSortTag(String sortTag) {
        this.sortTag = sortTag;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }
}
