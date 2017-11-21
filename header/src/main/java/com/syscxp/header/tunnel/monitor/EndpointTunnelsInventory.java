package com.syscxp.header.tunnel.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-11-03.
 * @Description: 网络工具返回集合.
 */
public class EndpointTunnelsInventory {
    private String tunnelUuid;
    private String tunnelName;
    private String nodeA;
    private String nodeZ;
    private long bandwidth;

    public static EndpointTunnelsInventory valueOf(MonitorAgentCommands.EndpointTunnel vo){
        EndpointTunnelsInventory inventory = new EndpointTunnelsInventory();
        inventory.setTunnelUuid(vo.getTunnelUuid());
        inventory.setTunnelName(vo.getTunnelName());
        inventory.setNodeA(vo.getNodeA());
        inventory.setNodeZ(vo.getNodeZ());
        inventory.setBandwidth(vo.getBandwidth());

        return  inventory;
    }

    public static List<EndpointTunnelsInventory> valueOf(Collection<MonitorAgentCommands.EndpointTunnel> vos) {
        List<EndpointTunnelsInventory> lst = new ArrayList<EndpointTunnelsInventory>(vos.size());
        for (MonitorAgentCommands.EndpointTunnel vo : vos) {
            lst.add(EndpointTunnelsInventory.valueOf(vo));
        }
        return lst;
    }

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public String getTunnelName() {
        return tunnelName;
    }

    public void setTunnelName(String tunnelName) {
        this.tunnelName = tunnelName;
    }

    public String getNodeA() {
        return nodeA;
    }

    public void setNodeA(String nodeA) {
        this.nodeA = nodeA;
    }

    public String getNodeZ() {
        return nodeZ;
    }

    public void setNodeZ(String nodeZ) {
        this.nodeZ = nodeZ;
    }

    public long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(long bandwidth) {
        this.bandwidth = bandwidth;
    }
}
