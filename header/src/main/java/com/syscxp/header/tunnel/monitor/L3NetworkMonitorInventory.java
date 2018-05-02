package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.query.ExpandedQueries;
import com.syscxp.header.query.ExpandedQuery;
import com.syscxp.header.search.Inventory;
import com.syscxp.header.tunnel.network.L3EndpointInventory;
import com.syscxp.header.tunnel.network.L3NetworkInventory;
import com.syscxp.header.tunnel.network.L3NetworkVO;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-03-22.
 * @Description: .
 */

@Inventory(mappingVOClass = L3NetworkMonitorVO.class)
@ExpandedQueries({
        @ExpandedQuery(expandedField = "l3NetworkVO", inventoryClass = L3NetworkInventory.class,
                foreignKey = "l3NetworkUuid", expandedInventoryKey = "uuid"),
})
public class L3NetworkMonitorInventory {


    private String l3NetworkUuid;
    private String name;
    private String ownerAccountUuid;
    private String srcL3EndpointUuid;
    private String srcL3EndpointName;
    private String dstL3EndpointUuid;
    private String dstL3EndpointName;
    private String monitorDetailUuid;


    public static L3NetworkMonitorInventory valueOf(AlarmCommands.L3NetworkMonitors vo) {
        L3NetworkMonitorInventory inventory = new L3NetworkMonitorInventory();
        inventory.setL3NetworkUuid(vo.getL3NetworkUuid());
        inventory.setName(vo.getName());
        inventory.setSrcL3EndpointUuid(vo.getSrcL3EndpointUuid());
        inventory.setSrcL3EndpointUuid(vo.getSrcL3EndpointName());
        inventory.setDstL3EndpointUuid(vo.getDstL3EndpointUuid());
        inventory.setDstL3EndpointUuid(vo.getDstL3EndpointName());
        inventory.setMonitorDetailUuid(vo.getMonitorDetailUuid());
        return inventory;
    }

    public static List<L3NetworkMonitorInventory> valueOf(Collection<AlarmCommands.L3NetworkMonitors> vos) {
        List<L3NetworkMonitorInventory> lst = new ArrayList<L3NetworkMonitorInventory>(vos.size());
        for (AlarmCommands.L3NetworkMonitors vo : vos) {
            lst.add(L3NetworkMonitorInventory.valueOf(vo));
        }

        return lst;
    }

    public String getL3NetworkUuid() {
        return l3NetworkUuid;
    }

    public void setL3NetworkUuid(String l3NetworkUuid) {
        this.l3NetworkUuid = l3NetworkUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerAccountUuid() {
        return ownerAccountUuid;
    }

    public void setOwnerAccountUuid(String ownerAccountUuid) {
        this.ownerAccountUuid = ownerAccountUuid;
    }

    public String getSrcL3EndpointUuid() {
        return srcL3EndpointUuid;
    }

    public void setSrcL3EndpointUuid(String srcL3EndpointUuid) {
        this.srcL3EndpointUuid = srcL3EndpointUuid;
    }

    public String getSrcL3EndpointName() {
        return srcL3EndpointName;
    }

    public void setSrcL3EndpointName(String srcL3EndpointName) {
        this.srcL3EndpointName = srcL3EndpointName;
    }

    public String getDstL3EndpointUuid() {
        return dstL3EndpointUuid;
    }

    public void setDstL3EndpointUuid(String dstL3EndpointUuid) {
        this.dstL3EndpointUuid = dstL3EndpointUuid;
    }

    public String getDstL3EndpointName() {
        return dstL3EndpointName;
    }

    public void setDstL3EndpointName(String dstL3EndpointName) {
        this.dstL3EndpointName = dstL3EndpointName;
    }

    public String getMonitorDetailUuid() {
        return monitorDetailUuid;
    }

    public void setMonitorDetailUuid(String monitorDetailUuid) {
        this.monitorDetailUuid = monitorDetailUuid;
    }
}

