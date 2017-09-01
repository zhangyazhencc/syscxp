package org.zstack.tunnel.header.host;

import org.zstack.header.search.Inventory;
import org.zstack.tunnel.header.switchs.SwitchPortInventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by DCY on 2017-09-01
 */
@Inventory(mappingVOClass = HostSwitchMonitorVO.class)
public class HostSwitchPortInventory {

    private String uuid;
    private String hostUuid;
    private HostInventory host;
    private String switchPortUuid;
    private SwitchPortInventory switchPort;
    private String interfaceName;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static HostSwitchPortInventory valueOf(HostSwitchMonitorVO vo){
        HostSwitchPortInventory inv = new HostSwitchPortInventory();
        inv.setUuid(vo.getUuid());
        inv.setHostUuid(vo.getHostUuid());
        inv.setHost(HostInventory.valueOf(vo.getHost()));
        inv.setSwitchPortUuid(vo.getSwitchPortUuid());
        inv.setSwitchPort(SwitchPortInventory.valueOf(vo.getSwitchPort()));
        inv.setInterfaceName(vo.getInterfaceName());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<HostSwitchPortInventory> valueOf(Collection<HostSwitchMonitorVO> vos) {
        List<HostSwitchPortInventory> lst = new ArrayList<HostSwitchPortInventory>(vos.size());
        for (HostSwitchMonitorVO vo : vos) {
            lst.add(HostSwitchPortInventory.valueOf(vo));
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

    public HostInventory getHost() {
        return host;
    }

    public void setHost(HostInventory host) {
        this.host = host;
    }

    public String getSwitchPortUuid() {
        return switchPortUuid;
    }

    public void setSwitchPortUuid(String switchPortUuid) {
        this.switchPortUuid = switchPortUuid;
    }

    public SwitchPortInventory getSwitchPort() {
        return switchPort;
    }

    public void setSwitchPort(SwitchPortInventory switchPort) {
        this.switchPort = switchPort;
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
