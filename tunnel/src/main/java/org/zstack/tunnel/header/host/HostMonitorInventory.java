package org.zstack.tunnel.header.host;

import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by DCY on 2017-08-30
 */
@Inventory(mappingVOClass = HostMonitorVO.class)
public class HostMonitorInventory {

    private String uuid;
    private String hostUuid;
    private String switchPortUuid;
    private String interfaceName;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static HostMonitorInventory valueOf(HostMonitorVO vo){
        HostMonitorInventory inv = new HostMonitorInventory();
        inv.setUuid(vo.getUuid());
        inv.setHostUuid(vo.getHostUuid());
        inv.setSwitchPortUuid(vo.getSwitchPortUuid());
        inv.setInterfaceName(vo.getInterfaceName());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<HostMonitorInventory> valueOf(Collection<HostMonitorVO> vos) {
        List<HostMonitorInventory> lst = new ArrayList<HostMonitorInventory>(vos.size());
        for (HostMonitorVO vo : vos) {
            lst.add(HostMonitorInventory.valueOf(vo));
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

    public String getSwitchPortUuid() {
        return switchPortUuid;
    }

    public void setSwitchPortUuid(String switchPortUuid) {
        this.switchPortUuid = switchPortUuid;
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
