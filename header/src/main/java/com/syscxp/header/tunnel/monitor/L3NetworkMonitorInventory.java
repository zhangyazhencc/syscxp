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
        @ExpandedQuery(expandedField = "l3NetworkVO",inventoryClass = L3NetworkInventory.class,
                foreignKey = "l3NetworkUuid",expandedInventoryKey = "uuid"),
})
public class L3NetworkMonitorInventory {

    private String uuid;

    private String l3NetworkUuid;

    private String srcL3EndpointUuid;

    private String dstL3EndpointUuid;

    private Timestamp lastOpDate;

    private Timestamp createDate;

    public static L3NetworkMonitorInventory valueOf(L3NetworkMonitorVO vo){
        L3NetworkMonitorInventory inventory = new L3NetworkMonitorInventory();
        inventory.setUuid(vo.getUuid());
        inventory.setL3NetworkUuid(vo.getL3NetworkUuid());
        inventory.setSrcL3EndpointUuid(vo.getSrcL3EndpointUuid());
        inventory.setDstL3EndpointUuid(vo.getDstL3EndpointUuid());
        inventory.setLastOpDate(vo.getLastOpDate());
        inventory.setCreateDate(vo.getCreateDate());
        return inventory;
    }

    public static List<L3NetworkMonitorInventory> valueOf(Collection<L3NetworkMonitorVO> vos){
        List<L3NetworkMonitorInventory> lst = new ArrayList<L3NetworkMonitorInventory>(vos.size());
        for(L3NetworkMonitorVO vo:vos){
            lst.add(L3NetworkMonitorInventory.valueOf(vo));
        }

        return lst;
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

    public String getSrcL3EndpointUuid() {
        return srcL3EndpointUuid;
    }

    public void setSrcL3EndpointUuid(String srcL3EndpointUuid) {
        this.srcL3EndpointUuid = srcL3EndpointUuid;
    }

    public String getDstL3EndpointUuid() {
        return dstL3EndpointUuid;
    }

    public void setDstL3EndpointUuid(String dstL3EndpointUuid) {
        this.dstL3EndpointUuid = dstL3EndpointUuid;
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

