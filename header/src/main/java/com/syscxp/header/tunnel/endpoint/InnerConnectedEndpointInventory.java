package com.syscxp.header.tunnel.endpoint;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create by DCY on 2017/11/1
 */
@Inventory(mappingVOClass = InnerConnectedEndpointVO.class)
public class InnerConnectedEndpointInventory {

    private String uuid;
    private String endpointUuid;
    private String connectedEndpointUuid;
    private String name;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static InnerConnectedEndpointInventory valueOf(InnerConnectedEndpointVO vo){
        InnerConnectedEndpointInventory inv = new InnerConnectedEndpointInventory();

        inv.setUuid(vo.getUuid());
        inv.setEndpointUuid(vo.getEndpointUuid());
        inv.setConnectedEndpointUuid(vo.getConnectedEndpointUuid());
        inv.setName(vo.getName());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());

        return inv;
    }

    public static List<InnerConnectedEndpointInventory> valueOf(Collection<InnerConnectedEndpointVO> vos) {
        List<InnerConnectedEndpointInventory> lst = new ArrayList<InnerConnectedEndpointInventory>(vos.size());
        for (InnerConnectedEndpointVO vo : vos) {
            lst.add(InnerConnectedEndpointInventory.valueOf(vo));
        }
        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
    }

    public String getConnectedEndpointUuid() {
        return connectedEndpointUuid;
    }

    public void setConnectedEndpointUuid(String connectedEndpointUuid) {
        this.connectedEndpointUuid = connectedEndpointUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
