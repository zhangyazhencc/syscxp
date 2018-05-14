package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create by DCY on 2018/5/9
 */
@Inventory(mappingVOClass = TETraceVO.class)
public class TETraceInventory {

    private String uuid;
    private String teConfigUuid;
    private String switchName;
    private String switchIP;
    private Integer traceSort;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static TETraceInventory valueOf(TETraceVO vo){
        TETraceInventory inv = new TETraceInventory();
        inv.setUuid(vo.getUuid());
        inv.setTeConfigUuid(vo.getTeConfigUuid());
        inv.setSwitchName(vo.getSwitchName());
        inv.setSwitchIP(vo.getSwitchIP());
        inv.setTraceSort(vo.getTraceSort());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<TETraceInventory> valueOf(Collection<TETraceVO> vos) {
        List<TETraceInventory> lst = new ArrayList<TETraceInventory>(vos.size());
        for (TETraceVO vo : vos) {
            lst.add(TETraceInventory.valueOf(vo));
        }
        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTeConfigUuid() {
        return teConfigUuid;
    }

    public void setTeConfigUuid(String teConfigUuid) {
        this.teConfigUuid = teConfigUuid;
    }

    public String getSwitchName() {
        return switchName;
    }

    public void setSwitchName(String switchName) {
        this.switchName = switchName;
    }

    public String getSwitchIP() {
        return switchIP;
    }

    public void setSwitchIP(String switchIP) {
        this.switchIP = switchIP;
    }

    public Integer getTraceSort() {
        return traceSort;
    }

    public void setTraceSort(Integer traceSort) {
        this.traceSort = traceSort;
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
