package org.zstack.tunnel.header.inventory;

import org.zstack.header.query.ExpandedQueries;
import org.zstack.header.query.ExpandedQuery;
import org.zstack.header.search.Inventory;
import org.zstack.tunnel.header.vo.TunnelPointVO;

import java.sql.Timestamp;

@Inventory(mappingVOClass = TunnelPointVO.class)
@ExpandedQueries({
        @ExpandedQuery(expandedField = "tunnel", inventoryClass = TunnelInventory.class,
                foreignKey = "uuid", expandedInventoryKey = "tunnelUuid"),
        @ExpandedQuery(expandedField = "agent", inventoryClass = AgentInventory.class,
                foreignKey = "uuid", expandedInventoryKey = "agentUuid"),
        @ExpandedQuery(expandedField = "switch", inventoryClass = SwitchInventory.class,
                foreignKey = "uuid", expandedInventoryKey = "switchUuid", hidden = true)
})
public class TunnelPointInventory {

    private String uuid;
    private String tunnelUuid;
    private String agentUuid;
    private String switchUuid;
    private String hostingUuid;
    private String bridgeName;
    private Integer meter;
    private Integer priority;
    private String role;
    private String hostingType;
    private Integer isAttached;
    private Integer deleted;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static TunnelPointInventory valueOf(TunnelPointVO vo) {
        TunnelPointInventory inv = new TunnelPointInventory();
        inv.setUuid(vo.getUuid());
        inv.setSwitchUuid(vo.getSwitchUuid());
        inv.setTunnelUuid(vo.getTunnelUuid());
        inv.setAgentUuid(vo.getAgentUuid());
        inv.setHostingUuid(vo.getHostingUuid());
        inv.setBridgeName(vo.getBridgeName());
        inv.setMeter(vo.getMeter());
        inv.setPriority(vo.getPriority());
        inv.setRole(vo.getRole());
        inv.setHostingType(vo.getHostingType());
        inv.setIsAttached(vo.getIsAttached());
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

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public String getAgentUuid() {
        return agentUuid;
    }

    public void setAgentUuid(String agentUuid) {
        this.agentUuid = agentUuid;
    }

    public String getSwitchUuid() {
        return switchUuid;
    }

    public void setSwitchUuid(String switchUuid) {
        this.switchUuid = switchUuid;
    }

    public String getHostingUuid() {
        return hostingUuid;
    }

    public void setHostingUuid(String hostingUuid) {
        this.hostingUuid = hostingUuid;
    }

    public String getBridgeName() {
        return bridgeName;
    }

    public void setBridgeName(String bridgeName) {
        this.bridgeName = bridgeName;
    }

    public Integer getMeter() {
        return meter;
    }

    public void setMeter(Integer meter) {
        this.meter = meter;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getHostingType() {
        return hostingType;
    }

    public void setHostingType(String hostingType) {
        this.hostingType = hostingType;
    }

    public Integer getIsAttached() {
        return isAttached;
    }

    public void setIsAttached(Integer isAttached) {
        this.isAttached = isAttached;
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
