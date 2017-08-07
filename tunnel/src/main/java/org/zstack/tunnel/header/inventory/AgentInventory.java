package org.zstack.tunnel.header.inventory;

import org.zstack.header.query.ExpandedQueries;
import org.zstack.header.query.ExpandedQuery;
import org.zstack.header.search.Inventory;
import org.zstack.tunnel.header.vo.AgentVO;

import java.sql.Timestamp;

@Inventory(mappingVOClass = AgentVO.class)
@ExpandedQueries({
        @ExpandedQuery(expandedField = "endpoint", inventoryClass = EndpointInventory.class,
                foreignKey = "uuid", expandedInventoryKey = "endpointUuid"),
        @ExpandedQuery(expandedField = "switch", inventoryClass = SwitchInventory.class,
                foreignKey = "uuid", expandedInventoryKey = "switchUuid", hidden = true)
})
public class AgentInventory {

    private String uuid;
    private String code;
    private String ip;
    private String status;
    private Integer enabled;
    private String endpointUuid;
    private String switchUuid;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static AgentInventory valueOf(AgentVO vo) {
        AgentInventory inv = new AgentInventory();
        inv.setUuid(vo.getUuid());
        inv.setEndpointUuid(vo.getEndpointUuid());
        inv.setSwitchUuid(vo.getSwitchUuid());
        inv.setIp(vo.getIp());
        inv.setCode(vo.getCode());
        inv.setStatus(vo.getStatus());
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
    }

    public String getSwitchUuid() {
        return switchUuid;
    }

    public void setSwitchUuid(String switchUuid) {
        this.switchUuid = switchUuid;
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
