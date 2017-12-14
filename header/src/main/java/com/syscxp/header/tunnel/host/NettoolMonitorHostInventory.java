package com.syscxp.header.tunnel.host;

import com.syscxp.header.host.HostInventory;
import com.syscxp.header.search.Inventory;
import com.syscxp.header.search.Parent;
import com.syscxp.header.tunnel.node.NodeInventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Inventory(mappingVOClass = MonitorHostVO.class)
public class NettoolMonitorHostInventory {

    private String monitorHostUuid;
    private String nodeName;

    public static NettoolMonitorHostInventory valueOf(MonitorHostVO vo) {
        NettoolMonitorHostInventory inventory = new NettoolMonitorHostInventory();
        inventory.setMonitorHostUuid(vo.getUuid());
        if (vo.getNode() != null)
            inventory.setNodeName(vo.getNode().getName());

        return inventory;
    }
    public static List<NettoolMonitorHostInventory> valueOf(Collection<MonitorHostVO> vos ) {
        List<NettoolMonitorHostInventory> lst = new ArrayList<>();
        for (MonitorHostVO vo : vos) {
            lst.add(NettoolMonitorHostInventory.valueOf(vo));
        }
        return lst;
    }

    public String getMonitorHostUuid() {
        return monitorHostUuid;
    }

    public void setMonitorHostUuid(String monitorHostUuid) {
        this.monitorHostUuid = monitorHostUuid;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }
}
