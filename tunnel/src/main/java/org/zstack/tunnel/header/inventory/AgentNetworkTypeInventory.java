package org.zstack.tunnel.header.inventory;

import java.sql.Timestamp;
import org.zstack.header.query.ExpandedQueries;
import org.zstack.header.query.ExpandedQuery;
import org.zstack.header.search.Inventory;
import org.zstack.tunnel.header.vo.AgentNetworkTypeVO;

@Inventory(mappingVOClass = AgentNetworkTypeVO.class)
@ExpandedQueries({
        @ExpandedQuery(expandedField = "agent", inventoryClass = AgentInventory.class,
                foreignKey = "uuid", expandedInventoryKey = "agentUuid"),
        @ExpandedQuery(expandedField = "networkType", inventoryClass = NetworkTypeInventory.class,
                foreignKey = "uuid", expandedInventoryKey = "networkTypeUuid"),
})
public class AgentNetworkTypeInventory {

    private String uuid;
    private String networkTypeUuid;
    private String agentUuid;
    private String status;
    private String ip;
    private String vxlanPort;
    private String gatewayIp;
    private String gatewayMac;
    private String physicalPort;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static AgentNetworkTypeInventory valueOf(AgentNetworkTypeVO vo) {
        AgentNetworkTypeInventory inv = new AgentNetworkTypeInventory();
        inv.setUuid(vo.getUuid());
        inv.setNetworkTypeUuid(vo.getNetworkTypeUuid());
        inv.setAgentUuid(vo.getAgentUuid());
        inv.setStatus(vo.getStatus());
        inv.setIp(vo.getIp());
        inv.setVxlanPort(vo.getVxlanPort());
        inv.setGatewayIp(vo.getGatewayIp());
        inv.setGatewayMac(vo.getGatewayMac());
        inv.setPhysicalPort(vo.getPhysicalPort());
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

    public String getNetworkTypeUuid() {
        return networkTypeUuid;
    }

    public void setNetworkTypeUuid(String networkTypeUuid) {
        this.networkTypeUuid = networkTypeUuid;
    }

    public String getAgentUuid() {
        return agentUuid;
    }

    public void setAgentUuid(String agentUuid) {
        this.agentUuid = agentUuid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getVxlanPort() {
        return vxlanPort;
    }

    public void setVxlanPort(String vxlanPort) {
        this.vxlanPort = vxlanPort;
    }

    public String getGatewayIp() {
        return gatewayIp;
    }

    public void setGatewayIp(String gatewayIp) {
        this.gatewayIp = gatewayIp;
    }

    public String getGatewayMac() {
        return gatewayMac;
    }

    public void setGatewayMac(String gatewayMac) {
        this.gatewayMac = gatewayMac;
    }

    public String getPhysicalPort() {
        return physicalPort;
    }

    public void setPhysicalPort(String physicalPort) {
        this.physicalPort = physicalPort;
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
