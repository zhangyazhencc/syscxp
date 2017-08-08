package org.zstack.tunnel.header.inventory;

import java.sql.Timestamp;
import org.zstack.header.query.ExpandedQueries;
import org.zstack.header.query.ExpandedQuery;
import org.zstack.header.search.Inventory;
import org.zstack.tunnel.header.vo.HostSwitchMonitorVO;


@Inventory(mappingVOClass = HostSwitchMonitorVO.class)
@ExpandedQueries({
        @ExpandedQuery(expandedField = "switch", inventoryClass = SwitchInventory.class,
                foreignKey = "uuid", expandedInventoryKey = "switchUuid"),
        @ExpandedQuery(expandedField = "host", inventoryClass = HostInventory.class,
                foreignKey = "uuid", expandedInventoryKey = "hostUuid", hidden = true)
})
public class HostSwitchMonitorInventory {

    private String uuid;
    private String switchUuid;
    private String hostUuid;
    private String interfaceName;
    private Integer deleted;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static HostSwitchMonitorInventory valueOf(HostSwitchMonitorVO vo){
        HostSwitchMonitorInventory inv = new HostSwitchMonitorInventory();
        inv.setUuid(vo.getUuid());
        inv.setSwitchUuid(vo.getSwitchUuid());
        inv.setHostUuid(vo.getHostUuid());
        inv.setInterfaceName(vo.getInterfaceName());
        inv.setDeleted(vo.getDeleted());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSwitchUuid() {
        return switchUuid;
    }

    public void setSwitchUuid(String switchUuid) {
        this.switchUuid = switchUuid;
    }

    public String getHostUuid() {
        return hostUuid;
    }

    public void setHostUuid(String hostUuid) {
        this.hostUuid = hostUuid;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
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
