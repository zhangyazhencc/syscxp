package com.syscxp.header.tunnel.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-11-03.
 * @Description: falcon查询共点专线返回集合.
 */
public class EndpointTunnelsInventory {
    private String tunnelUuid;
    private String tunnelName;
    private String endpointAMip;
    private String nodeA;
    private String endpointZMip;
    private String nodeZ;
    private long bandwidth;
    private String accountUuid;
    private Integer endpointAVlan;
    private Integer endpointZVlan;

    public static EndpointTunnelsInventory valueOf(MonitorAgentCommands.EndpointTunnel vo){
        EndpointTunnelsInventory inventory = new EndpointTunnelsInventory();
        inventory.setTunnelUuid(vo.getTunnelUuid());
        inventory.setTunnelName(vo.getTunnelName());
        inventory.setNodeA(vo.getNodeA());
        inventory.setNodeZ(vo.getNodeZ());
        inventory.setBandwidth(vo.getBandwidth());
        inventory.setEndpointAMip(vo.getEndpoingAMip());
        inventory.setEndpointZMip(vo.getEndpoingZMip());
        inventory.setAccountUuid(vo.getAccountUuid());
        inventory.setEndpointAVlan(vo.getEndpointAVlan());
        inventory.setEndpointZVlan(vo.getEndpointZVlan());

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

    public String getEndpointAMip() {
        return endpointAMip;
    }

    public void setEndpointAMip(String endpointAMip) {
        this.endpointAMip = endpointAMip;
    }

    public String getEndpointZMip() {
        return endpointZMip;
    }

    public void setEndpointZMip(String endpointZMip) {
        this.endpointZMip = endpointZMip;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public Integer getEndpointAVlan() {
        return endpointAVlan;
    }

    public void setEndpointAVlan(Integer endpointAVlan) {
        this.endpointAVlan = endpointAVlan;
    }

    public Integer getEndpointZVlan() {
        return endpointZVlan;
    }

    public void setEndpointZVlan(Integer endpointZVlan) {
        this.endpointZVlan = endpointZVlan;
    }
}
