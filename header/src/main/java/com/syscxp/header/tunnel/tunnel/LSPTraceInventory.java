package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create by DCY on 2017/11/28
 */
@Inventory(mappingVOClass = LSPTraceVO.class)
public class LSPTraceInventory {

    private String uuid;
    private String tunnelUuid;
    private Integer traceSort;
    private String switchName;
    private String switchIP;
    private String source;
    private String destination;
    private String direction;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static LSPTraceInventory valueOf(LSPTraceVO vo){
        LSPTraceInventory inv = new LSPTraceInventory();
        inv.setUuid(vo.getUuid());
        inv.setTunnelUuid(vo.getTunnelUuid());
        inv.setTraceSort(vo.getTraceSort());
        inv.setSwitchName(vo.getSwitchName());
        inv.setSwitchIP(vo.getSwitchIP());
        inv.setSource(vo.getSource());
        inv.setDestination(vo.getDestination());
        inv.setDirection(vo.getDirection());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<LSPTraceInventory> valueOf(Collection<LSPTraceVO> vos) {
        List<LSPTraceInventory> lst = new ArrayList<LSPTraceInventory>(vos.size());
        for (LSPTraceVO vo : vos) {
            lst.add(LSPTraceInventory.valueOf(vo));
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

    public String getSwitchIP() {
        return switchIP;
    }

    public void setSwitchIP(String switchIP) {
        this.switchIP = switchIP;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
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

    public String getSwitchName() {
        return switchName;
    }

    public void setSwitchName(String switchName) {
        this.switchName = switchName;
    }
}
