package com.syscxp.header.tunnel.host;

import com.syscxp.header.host.HostInventory;
import com.syscxp.header.search.Inventory;
import com.syscxp.header.search.Parent;
import com.syscxp.header.tunnel.node.NodeInventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Inventory(mappingVOClass = MonitorHostVO.class, collectionValueOfMethod = "valueOf1",
        parent = {@Parent(inventoryClass = HostInventory.class, type = MonitorConstant.HOST_TYPE)})
public class MonitorHostInventory extends HostInventory {

    private String username;
    private String password;
    private Integer sshPort;
    private MonitorType monitorType;

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
        this.setPassword(vo.getPassword());
        this.setSshPort(vo.getSshPort());
        this.setMonitorType(vo.getMonitorType());
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getSshPort() {
        return sshPort;
    }

    public void setSshPort(Integer sshPort) {
        this.sshPort = sshPort;
    }

    public MonitorType getMonitorType() {
        return monitorType;
    }

    public void setMonitorType(MonitorType monitorType) {
        this.monitorType = monitorType;
    }
}
