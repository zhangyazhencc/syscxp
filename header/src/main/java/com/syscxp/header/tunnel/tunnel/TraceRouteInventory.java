package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create by DCY on 2017/11/28
 */
@Inventory(mappingVOClass = TraceRouteVO.class)
public class TraceRouteInventory {

    private String uuid;
    private String tunnelUuid;
    private Integer traceSort;
    private String routeIP;
    private String timesFirst;
    private String timesSecond;
    private String timesThird;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static TraceRouteInventory valueOf(TraceRouteVO vo){
        TraceRouteInventory inv = new TraceRouteInventory();
        inv.setUuid(vo.getUuid());
        inv.setTunnelUuid(vo.getTunnelUuid());
        inv.setTraceSort(vo.getTraceSort());
        inv.setRouteIP(vo.getRouteIP());
        inv.setTimesFirst(vo.getTimesFirst());
        inv.setTimesSecond(vo.getTimesSecond());
        inv.setTimesThird(vo.getTimesThird());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<TraceRouteInventory> valueOf(Collection<TraceRouteVO> vos) {
        List<TraceRouteInventory> lst = new ArrayList<TraceRouteInventory>(vos.size());
        for (TraceRouteVO vo : vos) {
            lst.add(TraceRouteInventory.valueOf(vo));
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

    public Integer getTraceSort() {
        return traceSort;
    }

    public void setTraceSort(Integer traceSort) {
        this.traceSort = traceSort;
    }

    public String getRouteIP() {
        return routeIP;
    }

    public void setRouteIP(String routeIP) {
        this.routeIP = routeIP;
    }

    public String getTimesFirst() {
        return timesFirst;
    }

    public void setTimesFirst(String timesFirst) {
        this.timesFirst = timesFirst;
    }

    public String getTimesSecond() {
        return timesSecond;
    }

    public void setTimesSecond(String timesSecond) {
        this.timesSecond = timesSecond;
    }

    public String getTimesThird() {
        return timesThird;
    }

    public void setTimesThird(String timesThird) {
        this.timesThird = timesThird;
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
