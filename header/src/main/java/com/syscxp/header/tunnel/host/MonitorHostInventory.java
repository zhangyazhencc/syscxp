package com.syscxp.header.tunnel.host;

import com.syscxp.header.host.HostInventory;
import com.syscxp.header.query.ExpandedQueries;
import com.syscxp.header.query.ExpandedQuery;
import com.syscxp.header.search.Inventory;
import com.syscxp.header.search.Parent;
import com.syscxp.header.tunnel.endpoint.EndpointInventory;
import com.syscxp.header.tunnel.node.NodeInventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Inventory(mappingVOClass = MonitorHostVO.class, collectionValueOfMethod = "valueOf1",
        parent = {@Parent(inventoryClass = HostInventory.class, type = MonitorHostConstant.HOST_TYPE)})
@ExpandedQueries({
        @ExpandedQuery(expandedField = "node", inventoryClass = NodeInventory.class,
                foreignKey = "nodeUuid", expandedInventoryKey = "uuid"),
        @ExpandedQuery(expandedField = "switch", inventoryClass = HostSwitchMonitorVO.class,
                foreignKey = "uuid", expandedInventoryKey = "hostUuid")

})
public class MonitorHostInventory extends HostInventory {

    private String username;
    private Integer sshPort;
    private String monitorType;
    private String nodeUuid;

    private NodeInventory nodeInventory;

    public NodeInventory getNodeInventory() {
        return nodeInventory;
    }

    public void setNodeInventory(NodeInventory nodeInventory) {
        this.nodeInventory = nodeInventory;
    }

    protected MonitorHostInventory(MonitorHostVO vo) {
        super(vo);
        this.setUsername(vo.getUsername());
        this.setSshPort(vo.getSshPort());
        this.setMonitorType(vo.getMonitorType().toString());
        this.setNodeUuid(vo.getNodeUuid());
        if (vo.getNode() != null)
            this.setNodeInventory(NodeInventory.valueOf(vo.getNode()));
    }

    public static MonitorHostInventory valueOf(MonitorHostVO vo) {
        return new MonitorHostInventory(vo);
    }

    public static List<MonitorHostInventory> valueOf1(Collection<MonitorHostVO> vos) {
        List<MonitorHostInventory> invs = new ArrayList<>();
        for (MonitorHostVO vo : vos) {
            invs.add(valueOf(vo));
        }
        return invs;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getSshPort() {
        return sshPort;
    }

    public void setSshPort(Integer sshPort) {
        this.sshPort = sshPort;
    }

    public String getMonitorType() {
        return monitorType;
    }

    public void setMonitorType(String monitorType) {
        this.monitorType = monitorType;
    }

    public String getNodeUuid() {
        return nodeUuid;
    }

    public void setNodeUuid(String nodeUuid) {
        this.nodeUuid = nodeUuid;
    }
}
