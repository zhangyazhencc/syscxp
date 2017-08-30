package org.zstack.tunnel.header.host;

import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by DCY on 2017-08-30
 */
@Inventory(mappingVOClass = HostSwitchMonitorVO.class)
public class HostSwitchMonitorInventory {

    private String uuid;
    private String hostUuid;
    private String switchUuid;
    private String interfaceName;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static HostSwitchMonitorInventory valueOf(HostSwitchMonitorVO vo){
        HostSwitchMonitorInventory inv = new HostSwitchMonitorInventory();
        inv.setUuid(vo.getUuid());
        inv.setHostUuid(vo.getHostUuid());
        inv.setSwitchUuid(vo.getSwitchUuid());
        inv.setInterfaceName(vo.getInterfaceName());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<HostSwitchMonitorInventory> valueOf(Collection<HostSwitchMonitorVO> vos) {
        List<HostSwitchMonitorInventory> lst = new ArrayList<HostSwitchMonitorInventory>(vos.size());
        for (HostSwitchMonitorVO vo : vos) {
            lst.add(HostSwitchMonitorInventory.valueOf(vo));
        }
        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getHostUuid() {
        return hostUuid;
    }

    public void setHostUuid(String hostUuid) {
        this.hostUuid = hostUuid;
    }

    public String getSwitchUuid() {
        return switchUuid;
    }

    public void setSwitchUuid(String switchUuid) {
        this.switchUuid = switchUuid;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
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
