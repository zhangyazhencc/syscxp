package com.syscxp.header.tunnel.network;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create by DCY on 2018/3/8
 */
@Inventory(mappingVOClass = L3RtVO.class)
public class L3RtInventory {

    private String uuid;
    private String l3EndpointUuid;
    private String impor;
    private String export;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static L3RtInventory valueOf(L3RtVO vo){
        L3RtInventory inv = new L3RtInventory();

        inv.setUuid(vo.getUuid());
        inv.setL3EndpointUuid(vo.getL3EndpointUuid());
        inv.setImpor(vo.getImpor());
        inv.setExport(vo.getExport());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());

        return inv;
    }

    public static List<L3RtInventory> valueOf(Collection<L3RtVO> vos){
        List<L3RtInventory> invs = new ArrayList<>(vos.size());
        for (L3RtVO vo : vos) {
            invs.add(L3RtInventory.valueOf(vo));
        }
        return invs;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getL3EndpointUuid() {
        return l3EndpointUuid;
    }

    public void setL3EndpointUuid(String l3EndpointUuid) {
        this.l3EndpointUuid = l3EndpointUuid;
    }

    public String getImpor() {
        return impor;
    }

    public void setImpor(String impor) {
        this.impor = impor;
    }

    public String getExport() {
        return export;
    }

    public void setExport(String export) {
        this.export = export;
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
