package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create by DCY on 2018/5/14
 */
@Inventory(mappingVOClass = ExplicitPathVO.class)
public class ExplicitPathInventory {

    private String uuid;
    private Integer traceSort;
    private String switchName;
    private String switchIP;
    private String vsiTePathUuid;
    private String tunnelsName;
    private String explicitName;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static ExplicitPathInventory valueOf(ExplicitPathVO vo){
        ExplicitPathInventory inv = new ExplicitPathInventory();
        inv.setUuid(vo.getUuid());
        inv.setVsiTePathUuid(vo.getVsiTePathUuid());
        inv.setTraceSort(vo.getTraceSort());
        inv.setSwitchName(vo.getSwitchName());
        inv.setSwitchIP(vo.getSwitchIP());
        inv.setTunnelsName(vo.getTunnelsName());
        inv.setExplicitName(vo.getExplicitName());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<ExplicitPathInventory> valueOf(Collection<ExplicitPathVO> vos) {
        List<ExplicitPathInventory> lst = new ArrayList<ExplicitPathInventory>(vos.size());
        for (ExplicitPathVO vo : vos) {
            lst.add(ExplicitPathInventory.valueOf(vo));
        }
        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getTraceSort() {
        return traceSort;
    }

    public void setTraceSort(Integer traceSort) {
        this.traceSort = traceSort;
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

    public String getVsiTePathUuid() {
        return vsiTePathUuid;
    }

    public void setVsiTePathUuid(String vsiTePathUuid) {
        this.vsiTePathUuid = vsiTePathUuid;
    }

    public String getTunnelsName() {
        return tunnelsName;
    }

    public void setTunnelsName(String tunnelsName) {
        this.tunnelsName = tunnelsName;
    }

    public String getExplicitName() {
        return explicitName;
    }

    public void setExplicitName(String explicitName) {
        this.explicitName = explicitName;
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
