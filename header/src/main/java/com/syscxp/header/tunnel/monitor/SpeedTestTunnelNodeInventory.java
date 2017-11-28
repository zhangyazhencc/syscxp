package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.search.Inventory;
import com.syscxp.header.tunnel.tunnel.TunnelMonitorState;
import com.syscxp.header.tunnel.tunnel.TunnelState;
import com.syscxp.header.tunnel.tunnel.TunnelStatus;
import com.syscxp.header.tunnel.tunnel.TunnelSwitchPortVO;

import java.sql.Timestamp;
import java.util.*;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-11-14.
 * @Description: .
 */
@Inventory(mappingVOClass = SpeedTestTunnelVO.class)
public class SpeedTestTunnelNodeInventory {
    private String uuid;
    private String tunnelUuid;
    private String tunnelName;
    private List<Map<String, String>> nodes;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static SpeedTestTunnelNodeInventory valueOf(SpeedTestTunnelVO vo) {
        SpeedTestTunnelNodeInventory inventory = new SpeedTestTunnelNodeInventory();
        List<Map<String, String>> nodes = new ArrayList<>();


        inventory.setUuid(vo.getUuid());
        inventory.setTunnelUuid(vo.getTunnelUuid());
        inventory.setTunnelName(vo.getTunnelVO().getName());
        inventory.setLastOpDate(vo.getLastOpDate());
        inventory.setCreateDate(vo.getCreateDate());

        for (TunnelSwitchPortVO tunnelSwitchPortVO : vo.getTunnelVO().getTunnelSwitchPortVOS()) {
            Map<String, String> node = new HashMap<>();
            node.put("nodeUuid", tunnelSwitchPortVO.getEndpointVO().getNodeVO().getUuid());
            node.put("nodeName", tunnelSwitchPortVO.getEndpointVO().getNodeVO().getName());
            node.put("sortType", tunnelSwitchPortVO.getSortTag());

            nodes.add(node);
        }

        inventory.setNodes(nodes);

        return inventory;
    }

    public static List<SpeedTestTunnelNodeInventory> valueOf(Collection<SpeedTestTunnelVO> vos) {
        List<SpeedTestTunnelNodeInventory> lst = new ArrayList<SpeedTestTunnelNodeInventory>(vos.size());
        for (SpeedTestTunnelVO vo : vos) {
            lst.add(SpeedTestTunnelNodeInventory.valueOf(vo));
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

    public List<Map<String, String>> getNodes() {
        return nodes;
    }

    public void setNodes(List<Map<String, String>> nodes) {
        this.nodes = nodes;
    }
}
