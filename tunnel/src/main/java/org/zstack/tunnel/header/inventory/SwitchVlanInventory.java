package org.zstack.tunnel.header.inventory;

import org.zstack.header.query.ExpandedQueries;
import org.zstack.header.query.ExpandedQuery;
import org.zstack.header.search.Inventory;
import org.zstack.tunnel.header.vo.SwitchVlanVO;

import java.sql.Timestamp;

@Inventory(mappingVOClass = SwitchVlanVO.class)
@ExpandedQueries({
        @ExpandedQuery(expandedField = "switch", inventoryClass = SwitchInventory.class,
                foreignKey = "uuid", expandedInventoryKey = "switchUuid", hidden = true)
})
public class SwitchVlanInventory {

    private String uuid;
    private String switchtUuid;
    private Integer startVlan;
    private Integer endVlan;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static SwitchVlanInventory valueOf(SwitchVlanVO vo) {
        SwitchVlanInventory inv = new SwitchVlanInventory();
        inv.setUuid(vo.getUuid());
        inv.setSwitchtUuid(vo.getSwitchtUuid());
        inv.setStartVlan(vo.getStartVlan());
        inv.setEndVlan(vo.getEndVlan());
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

    public Integer getStartVlan() {
        return startVlan;
    }

    public void setStartVlan(Integer startVlan) {
        this.startVlan = startVlan;
    }

    public Integer getEndVlan() {
        return endVlan;
    }

    public void setEndVlan(Integer endVlan) {
        this.endVlan = endVlan;
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
