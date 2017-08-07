package org.zstack.tunnel.header.inventory;

import org.zstack.header.query.ExpandedQueries;
import org.zstack.header.query.ExpandedQuery;
import org.zstack.header.search.Inventory;
import org.zstack.tunnel.header.vo.EndpointVO;

import java.sql.Timestamp;

@Inventory(mappingVOClass = EndpointVO.class)
@ExpandedQueries({
        @ExpandedQuery(expandedField = "node", inventoryClass = NodeInventory.class,
                foreignKey = "uuid", expandedInventoryKey = "nodeUuid"),
})
public class EndpointInventory {

    private String uuid;
    private String nodeUuid;
    private String name;
    private String code;
    private Integer enabled;
    private String openToCustomers;
    private String status;
    private String subType;
    private String description;
    private Integer deleted;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static EndpointInventory valueOf(EndpointVO vo){
        EndpointInventory inv = new EndpointInventory();
        inv.setUuid(vo.getUuid());
        inv.setNodeUuid(vo.getNodeUuid());
        inv.setName(vo.getName());
        inv.setCode(vo.getCode());
        inv.setEnabled(vo.getEnabled());
        inv.setOpenToCustomers(vo.getOpenToCustomers());
        inv.setStatus(vo.getStatus());
        inv.setSubType(vo.getSubType());
        inv.setDescription(vo.getDescription());
        inv.setDeleted(vo.getDeleted());
        inv.setLastOpDate(vo.getLastOpDate());
        vo.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNodeUuid() {
        return nodeUuid;
    }

    public void setNodeUuid(String nodeUuid) {
        this.nodeUuid = nodeUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public String getOpenToCustomers() {
        return openToCustomers;
    }

    public void setOpenToCustomers(String openToCustomers) {
        this.openToCustomers = openToCustomers;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
