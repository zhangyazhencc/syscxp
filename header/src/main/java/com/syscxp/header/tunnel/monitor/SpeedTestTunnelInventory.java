package com.syscxp.header.tunnel.monitor;

import com.sun.xml.internal.messaging.saaj.packaging.mime.util.LineInputStream;
import com.syscxp.header.search.Inventory;
import com.syscxp.header.tunnel.node.NodeInventory;
import com.syscxp.header.tunnel.node.NodeVO;
import com.syscxp.header.tunnel.tunnel.TunnelSwitchPortVO;
import com.syscxp.header.tunnel.tunnel.TunnelVO;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-11-14.
 * @Description: .
 */
@Inventory(mappingVOClass = SpeedTestTunnelVO.class)
public class SpeedTestTunnelInventory {
    private String uuid;
    private String tunnelUuid;
    private String tunnelName;
    private List<NodeInventory> nodeInventories;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static SpeedTestTunnelInventory valueOf(SpeedTestTunnelVO vo){
        SpeedTestTunnelInventory inventory = new SpeedTestTunnelInventory();
        inventory.setUuid(vo.getUuid());
        inventory.setTunnelUuid(vo.getTunnelUuid());
        inventory.setTunnelName(vo.getTunnelVO().getName());

        List<NodeVO> nodeVOS = new ArrayList<>();
        for(TunnelSwitchPortVO tunnelSwitchPortVO :vo.getTunnelVO().getTunnelSwitchPortVOS()){
            nodeVOS.add(tunnelSwitchPortVO.getEndpointVO().getNodeVO());
        }
        inventory.setNodeInventories(NodeInventory.valueOf(nodeVOS));

        inventory.setLastOpDate(vo.getLastOpDate());
        inventory.setCreateDate(vo.getCreateDate());

        return  inventory;
    }

    public static List<SpeedTestTunnelInventory> valueOf(Collection<SpeedTestTunnelVO> vos) {
        List<SpeedTestTunnelInventory> lst = new ArrayList<SpeedTestTunnelInventory>(vos.size());
        for (SpeedTestTunnelVO vo : vos) {
            lst.add(SpeedTestTunnelInventory.valueOf(vo));
        }
        return lst;
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

    public String getTunnelName() {
        return tunnelName;
    }

    public void setTunnelName(String tunnelName) {
        this.tunnelName = tunnelName;
    }

    public List<NodeInventory> getNodeInventories() {
        return nodeInventories;
    }

    public void setNodeInventories(List<NodeInventory> nodeInventories) {
        this.nodeInventories = nodeInventories;
    }
}
