package org.zstack.tunnel.header.monitor;

import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create by DCY on 2017/9/26
 */
@Inventory(mappingVOClass = MonitorCidrVO.class)
public class MonitorCidrInventory {

    private String uuid;
    private String monitorCidr;
    private String startAddress;
    private String endAddress;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static MonitorCidrInventory valueOf(MonitorCidrVO vo){
        MonitorCidrInventory inv = new MonitorCidrInventory();
        inv.setUuid(vo.getUuid());
        inv.setMonitorCidr(vo.getMonitorCidr());
        inv.setStartAddress(vo.getStartAddress());
        inv.setEndAddress(vo.getEndAddress());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<MonitorCidrInventory> valueOf(Collection<MonitorCidrVO> vos) {
        List<MonitorCidrInventory> lst = new ArrayList<MonitorCidrInventory>(vos.size());
        for (MonitorCidrVO vo : vos) {
            lst.add(MonitorCidrInventory.valueOf(vo));
        }
        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMonitorCidr() {
        return monitorCidr;
    }

    public void setMonitorCidr(String monitorCidr) {
        this.monitorCidr = monitorCidr;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
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
