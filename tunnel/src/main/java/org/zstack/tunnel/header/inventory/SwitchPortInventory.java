package org.zstack.tunnel.header.inventory;

import org.zstack.header.query.ExpandedQueries;
import org.zstack.header.query.ExpandedQuery;
import org.zstack.header.search.Inventory;
import org.zstack.tunnel.header.vo.SwitchPortVO;

import java.sql.Timestamp;

@Inventory(mappingVOClass = SwitchPortVO.class)
@ExpandedQueries({
        @ExpandedQuery(expandedField = "switch", inventoryClass = SwitchInventory.class,
                foreignKey = "uuid", expandedInventoryKey = "switchUuid", hidden = true)
})
public class SwitchPortInventory {

    private String uuid;
    private String switchtUuid;
    private Integer portNum;
    private String portName;
    private String label;
    private Integer vlan;
    private Integer endVlan;
    private Integer reuse;
    private Integer autoAlloc;
    private Integer enabled;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static SwitchPortInventory valueOf(SwitchPortVO vo) {
        SwitchPortInventory inv = new SwitchPortInventory();
        inv.setUuid(vo.getUuid());
        inv.setSwitchtUuid(vo.getSwitchtUuid());
        inv.setPortNum(vo.getPortNum());
        inv.setPortName(vo.getPortName());
        inv.setLabel(vo.getLabel());
        inv.setVlan(vo.getVlan());
        inv.setEndVlan(vo.getEndVlan());
        inv.setReuse(vo.getReuse());
        inv.setAutoAlloc(vo.getAutoAlloc());
        inv.setEnabled(vo.getEnabled());
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

    public String getSwitchtUuid() {
        return switchtUuid;
    }

    public void setSwitchtUuid(String switchtUuid) {
        this.switchtUuid = switchtUuid;
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getVlan() {
        return vlan;
    }

    public void setVlan(Integer vlan) {
        this.vlan = vlan;
    }

    public Integer getEndVlan() {
        return endVlan;
    }

    public void setEndVlan(Integer endVlan) {
        this.endVlan = endVlan;
    }

    public Integer getReuse() {
        return reuse;
    }

    public void setReuse(Integer reuse) {
        this.reuse = reuse;
    }

    public Integer getAutoAlloc() {
        return autoAlloc;
    }

    public void setAutoAlloc(Integer autoAlloc) {
        this.autoAlloc = autoAlloc;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
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
