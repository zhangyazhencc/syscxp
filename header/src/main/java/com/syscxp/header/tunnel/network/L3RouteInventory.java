package com.syscxp.header.tunnel.network;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = L3RouteVO.class)
public class L3RouteInventory {

    private String uuid;
    private String l3EndpointUuid;
    private String cidr;
    private String truthCidr;
    private String nextIp;
    private Integer indexNum;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static L3RouteInventory valueOf(L3RouteVO vo){
        L3RouteInventory inv = new L3RouteInventory();
        inv.setUuid(vo.getUuid());
        inv.setCidr(vo.getCidr());
        inv.setTruthCidr(vo.getTruthCidr());
        inv.setL3EndpointUuid(vo.getL3EndpointUuid());
        inv.setNextIp(vo.getNextIp());
        inv.setIndexNum(vo.getIndexNum());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<L3RouteInventory> valueOf(Collection<L3RouteVO> vos) {
        List<L3RouteInventory> invs = new ArrayList<>(vos.size());
        for (L3RouteVO vo : vos) {
            invs.add(L3RouteInventory.valueOf(vo));
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

    public String getCidr() {
        return cidr;
    }

    public void setCidr(String cidr) {
        this.cidr = cidr;
    }

    public String getNextIp() {
        return nextIp;
    }

    public void setNextIp(String nextIp) {
        this.nextIp = nextIp;
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

    public Integer getIndexNum() {
        return indexNum;
    }

    public void setIndexNum(Integer indexNum) {
        this.indexNum = indexNum;
    }

    public String getTruthCidr() {
        return truthCidr;
    }

    public void setTruthCidr(String truthCidr) {
        this.truthCidr = truthCidr;
    }
}
