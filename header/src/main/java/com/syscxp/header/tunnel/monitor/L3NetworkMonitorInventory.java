package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.query.ExpandedQueries;
import com.syscxp.header.query.ExpandedQuery;
import com.syscxp.header.search.Inventory;
import com.syscxp.header.tunnel.network.L3EndpointInventory;
import com.syscxp.header.tunnel.network.L3EndpointVO;
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
    private String l3NetworkName;
    private String ownerAccountUuid;
    private String srcEndpointName;
    private String dstEndpointName;
    private String monitorDetailUuid;

    public static L3NetworkMonitorInventory valueOf(L3NetworkMonitorVO vo) {
        L3NetworkMonitorInventory inventory = new L3NetworkMonitorInventory();
        inventory.setMonitorDetailUuid(vo.getUuid());
        inventory.setL3NetworkUuid(vo.getL3NetworkUuid());
        inventory.setL3NetworkName(vo.getL3NetworkVO().getName());
        inventory.setOwnerAccountUuid(vo.getL3NetworkVO().getOwnerAccountUuid());
        inventory.setSrcEndpointName(vo.getSrcEndpointVO().getEndpointEO().getName());
        inventory.setDstEndpointName(vo.getDstEndpointVO().getEndpointEO().getName());
        return inventory;
    }

    public static List<L3NetworkMonitorInventory> valueOf(Collection<L3NetworkMonitorVO> vos) {
        List<L3NetworkMonitorInventory> lst = new ArrayList<L3NetworkMonitorInventory>(vos.size());
        for (L3NetworkMonitorVO vo : vos) {
            lst.add(L3NetworkMonitorInventory.valueOf(vo));
        }

        return lst;
    }

    public String getMonitorDetailUuid() {
        return monitorDetailUuid;
    }

    public void setMonitorDetailUuid(String monitorDetailUuid) {
        this.monitorDetailUuid = monitorDetailUuid;
    }

    public String getL3NetworkUuid() {
        return l3NetworkUuid;
    }

    public void setL3NetworkUuid(String l3NetworkUuid) {
        this.l3NetworkUuid = l3NetworkUuid;
    }

    public String getL3NetworkName() {
        return l3NetworkName;
    }

    public void setL3NetworkName(String l3NetworkName) {
        this.l3NetworkName = l3NetworkName;
    }

    public String getSrcEndpointName() {
        return srcEndpointName;
    }

    public void setSrcEndpointName(String srcEndpointName) {
        this.srcEndpointName = srcEndpointName;
    }

    public String getDstEndpointName() {
        return dstEndpointName;
    }

    public void setDstEndpointName(String dstEndpointName) {
        this.dstEndpointName = dstEndpointName;
    }

    public String getOwnerAccountUuid() {
        return ownerAccountUuid;
    }

    public void setOwnerAccountUuid(String ownerAccountUuid) {
        this.ownerAccountUuid = ownerAccountUuid;
    }
}

