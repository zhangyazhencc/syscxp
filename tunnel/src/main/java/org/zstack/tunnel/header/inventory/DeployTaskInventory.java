package org.zstack.tunnel.header.inventory;

import org.zstack.header.query.ExpandedQueries;
import org.zstack.header.query.ExpandedQuery;
import org.zstack.header.search.Inventory;
import org.zstack.tunnel.header.vo.DeployTaskVO;

import java.sql.Timestamp;

@Inventory(mappingVOClass = DeployTaskVO.class)
@ExpandedQueries({
        @ExpandedQuery(expandedField = "tunnelPoint", inventoryClass = TunnelPointInventory.class,
                foreignKey = "uuid", expandedInventoryKey = "tunnelPointUuid"),
})
public class DeployTaskInventory {

    private String uuid;
    private String tunnelPointUuid;
    private String state;
    private String type;
    private String description;
    private String comment;
    private String finshBy;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static DeployTaskInventory valueOf(DeployTaskVO vo) {
        DeployTaskInventory inv = new DeployTaskInventory();
        inv.setTunnelPointUuid(vo.getTunnelPointUuid());
        inv.setState(vo.getState());
        inv.setType(vo.getType());
        inv.setDescription(vo.getDescription());
        inv.setComment(vo.getComment());
        inv.setFinshBy(vo.getFinshBy());
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

    public String getTunnelPointUuid() {
        return tunnelPointUuid;
    }

    public void setTunnelPointUuid(String tunnelPointUuid) {
        this.tunnelPointUuid = tunnelPointUuid;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFinshBy() {
        return finshBy;
    }

    public void setFinshBy(String finshBy) {
        this.finshBy = finshBy;
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
