package com.syscxp.header.tunnel.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-11-03.
 * @Description: 网络工具返回集合.
 */
public class L3NetworkMonitorDetailInventory {
    private String monitorUuid;
    private String l3NetworkUuid;
    private String l3NetworkName;
    private String ownerAccountUuid;
    private String interfaceNameA;
    private String interfaceNameZ;
    private String endpointAIp;
    private String endpointZIp;
    private Integer endpointAVid;
    private Integer endpointZVid;
    private long endpointABandwidth;
    private long endpointZBandwidth;

    public static L3NetworkMonitorDetailInventory valueOf(AlarmCommands.L3NetworkMonitorCommand vo) {
        L3NetworkMonitorDetailInventory inventory = new L3NetworkMonitorDetailInventory();

        inventory.setMonitorUuid(vo.getMonitorUuid());
        inventory.setL3NetworkUuid(vo.getL3NetworkUuid());
        inventory.setL3NetworkName(vo.getL3NetworkName());
        inventory.setOwnerAccountUuid(vo.getOwnerAccountUuid());
        inventory.setInterfaceNameA(vo.getInterfaceNameA());
        inventory.setInterfaceNameZ(vo.getInterfaceNameZ());
        inventory.setEndpointAIp(vo.getEndpointAIp());
        inventory.setEndpointZIp(vo.getEndpointZIp());
        inventory.setEndpointAVid(vo.getEndpointAVid());
        inventory.setEndpointZVid(vo.getEndpointZVid());
        inventory.setEndpointABandwidth(vo.getEndpointABandwidth());
        inventory.setEndpointZBandwidth(vo.getEndpointZBandwidth());

        return inventory;
    }

    public static List<L3NetworkMonitorDetailInventory> valueOf(Collection<AlarmCommands.L3NetworkMonitorCommand> vos) {
        List<L3NetworkMonitorDetailInventory> lst = new ArrayList<L3NetworkMonitorDetailInventory>(vos.size());
        for (AlarmCommands.L3NetworkMonitorCommand vo : vos) {
            lst.add(L3NetworkMonitorDetailInventory.valueOf(vo));
        }
        return lst;
    }

    public String getL3NetworkUuid() {
        return l3NetworkUuid;
    }

    public void setL3NetworkUuid(String l3NetworkUuid) {
        this.l3NetworkUuid = l3NetworkUuid;
    }

    public String getL3NetworkName() {
        return l3NetworkName;
    }

    public void setL3NetworkName(String l3NetworkName) {
        this.l3NetworkName = l3NetworkName;
    }

    public void setEndpointAVid(Integer endpointAVid) {
        this.endpointAVid = endpointAVid;
    }

    public void setEndpointZVid(Integer endpointZVid) {
        this.endpointZVid = endpointZVid;
    }

    public long getEndpointABandwidth() {
        return endpointABandwidth;
    }

    public void setEndpointABandwidth(long endpointABandwidth) {
        this.endpointABandwidth = endpointABandwidth;
    }

    public long getEndpointZBandwidth() {
        return endpointZBandwidth;
    }

    public void setEndpointZBandwidth(long endpointZBandwidth) {
        this.endpointZBandwidth = endpointZBandwidth;
    }

    public String getOwnerAccountUuid() {
        return ownerAccountUuid;
    }

    public void setOwnerAccountUuid(String ownerAccountUuid) {
        this.ownerAccountUuid = ownerAccountUuid;
    }

    public String getInterfaceNameA() {
        return interfaceNameA;
    }

    public void setInterfaceNameA(String interfaceNameA) {
        this.interfaceNameA = interfaceNameA;
    }

    public String getInterfaceNameZ() {
        return interfaceNameZ;
    }

    public void setInterfaceNameZ(String interfaceNameZ) {
        this.interfaceNameZ = interfaceNameZ;
    }

    public String getEndpointAIp() {
        return endpointAIp;
    }

    public void setEndpointAIp(String endpointAIp) {
        this.endpointAIp = endpointAIp;
    }

    public String getEndpointZIp() {
        return endpointZIp;
    }

    public void setEndpointZIp(String endpointZIp) {
        this.endpointZIp = endpointZIp;
    }

    public Integer getEndpointAVid() {
        return endpointAVid;
    }

    public Integer getEndpointZVid() {
        return endpointZVid;
    }

    public String getMonitorUuid() {
        return monitorUuid;
    }

    public void setMonitorUuid(String monitorUuid) {
        this.monitorUuid = monitorUuid;
    }
}
