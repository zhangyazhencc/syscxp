package org.zstack.tunnel.header.inventory;

import org.zstack.header.query.ExpandedQueries;
import org.zstack.header.query.ExpandedQuery;
import org.zstack.header.search.Inventory;
import org.zstack.tunnel.header.vo.TunnelPointSwitchPortVO;

import java.sql.Timestamp;

@Inventory(mappingVOClass = TunnelPointSwitchPortVO.class)
@ExpandedQueries({
        @ExpandedQuery(expandedField = "switchPortUuid", inventoryClass = SwitchPortInventory.class,
                foreignKey = "uuid", expandedInventoryKey = "switchPortUuid"),
        @ExpandedQuery(expandedField = "tunnelPointUuid", inventoryClass = TunnelPointInventory.class,
                foreignKey = "uuid", expandedInventoryKey = "tunnelPointUuid"),
        @ExpandedQuery(expandedField = "switch", inventoryClass = SwitchInventory.class,
                foreignKey = "uuid", expandedInventoryKey = "switchUuid", hidden = true)
})
public class TunnelPointSwitchPortInventory {

    private String uuid;
    private String switchUuid;
    private String switchPortUuid;
    private String tunnelPointUuid;
    private String groupUuid;
    private Integer portNum;
    private String portName;
    private String portType;
    private Integer vlan;
    private Integer innerVlan;
    private Integer deleted;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static TunnelPointSwitchPortInventory valueOf(TunnelPointSwitchPortVO vo) {
        TunnelPointSwitchPortInventory inv = new TunnelPointSwitchPortInventory();
        inv.setUuid(vo.getUuid());
        inv.setSwitchUuid(vo.getSwitchUuid());
        inv.setSwitchPortUuid(vo.getSwitchPortUuid());
        inv.setTunnelPointUuid(vo.getTunnelPointUuid());
        inv.setGroupUuid(vo.getGroupUuid());
        inv.setPortNum(vo.getPortNum());
        inv.setPortName(vo.getPortName());
        inv.setPortType(vo.getPortType());
        inv.setVlan(vo.getVlan());
        inv.setInnerVlan(vo.getInnerVlan());
        inv.setDeleted(vo.getDeleted());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
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

    public String getSwitchPortUuid() {
        return switchPortUuid;
    }

    public void setSwitchPortUuid(String switchPortUuid) {
        this.switchPortUuid = switchPortUuid;
    }

    public String getTunnelPointUuid() {
        return tunnelPointUuid;
    }

    public void setTunnelPointUuid(String tunnelPointUuid) {
        this.tunnelPointUuid = tunnelPointUuid;
    }

    public String getGroupUuid() {
        return groupUuid;
    }

    public void setGroupUuid(String groupUuid) {
        this.groupUuid = groupUuid;
    }

    public Integer getPortNum() {
        return portNum;
    }

    public void setPortNum(Integer portNum) {
        this.portNum = portNum;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public String getPortType() {
        return portType;
    }

    public void setPortType(String portType) {
        this.portType = portType;
    }

    public Integer getVlan() {
        return vlan;
    }

    public void setVlan(Integer vlan) {
        this.vlan = vlan;
    }

    public Integer getInnerVlan() {
        return innerVlan;
    }

    public void setInnerVlan(Integer innerVlan) {
        this.innerVlan = innerVlan;
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
