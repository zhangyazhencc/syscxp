package com.syscxp.header.tunnel.network;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create by DCY on 2018/4/19
 */
@Inventory(mappingVOClass = L3SlaveRouteVO.class)
public class L3SlaveRouteInventory {

    private String uuid;
    private String l3RouteUuid;
    private String routeIp;
    private Integer preference;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static L3SlaveRouteInventory valueOf(L3SlaveRouteVO vo){
        L3SlaveRouteInventory inv = new L3SlaveRouteInventory();
        inv.setUuid(vo.getUuid());
        inv.setL3RouteUuid(vo.getL3RouteUuid());
        inv.setRouteIp(vo.getRouteIp());
        inv.setPreference(vo.getPreference());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<L3SlaveRouteInventory> valueOf(Collection<L3SlaveRouteVO> vos) {
        List<L3SlaveRouteInventory> invs = new ArrayList<>(vos.size());
        for (L3SlaveRouteVO vo : vos) {
            invs.add(L3SlaveRouteInventory.valueOf(vo));
        }
        return invs;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getL3RouteUuid() {
        return l3RouteUuid;
    }

    public void setL3RouteUuid(String l3RouteUuid) {
        this.l3RouteUuid = l3RouteUuid;
    }

    public String getRouteIp() {
        return routeIp;
    }

    public void setRouteIp(String routeIp) {
        this.routeIp = routeIp;
    }

    public Integer getPreference() {
        return preference;
    }

    public void setPreference(Integer preference) {
        this.preference = preference;
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
