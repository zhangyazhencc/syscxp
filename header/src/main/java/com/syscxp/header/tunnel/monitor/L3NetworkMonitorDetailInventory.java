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
    private String l3NetworkUuid;
    private String name;
    private String ownerAccountUuid;
    private String interfaceNameA;
    private String interfaceNameZ;
    private String endpointAIp;
    private String endpointZIp;
    private String endpointAVid;
    private String endpointZVid;
    private String monitorDetailUuid;

    public static L3NetworkMonitorDetailInventory valueOf(AlarmCommands.L3NetworkMonitorDetail vo) {
        L3NetworkMonitorDetailInventory inventory = new L3NetworkMonitorDetailInventory();
        inventory.setL3NetworkUuid(vo.getL3NetworkUuid());
        inventory.setName(vo.getName());
        inventory.setOwnerAccountUuid(vo.getOwnerAccountUuid());
        inventory.setInterfaceNameA(vo.getInterfaceNameA());
        inventory.setInterfaceNameZ(vo.getInterfaceNameZ());
        inventory.setEndpointAIp(vo.getEndpointAIp());
        inventory.setEndpointZIp(vo.getEndpointZIp());
        inventory.setEndpointAVid(vo.getEndpointAVid());
        inventory.setEndpointZVid(vo.getEndpointZVid());

        return inventory;
    }

    public static List<L3NetworkMonitorDetailInventory> valueOf(Collection<AlarmCommands.L3NetworkMonitorDetail> vos) {
        List<L3NetworkMonitorDetailInventory> lst = new ArrayList<L3NetworkMonitorDetailInventory>(vos.size());
        for (AlarmCommands.L3NetworkMonitorDetail vo : vos) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getEndpointAVid() {
        return endpointAVid;
    }

    public void setEndpointAVid(String endpointAVid) {
        this.endpointAVid = endpointAVid;
    }

    public String getEndpointZVid() {
        return endpointZVid;
    }

    public void setEndpointZVid(String endpointZVid) {
        this.endpointZVid = endpointZVid;
    }

    public String getMonitorDetailUuid() {
        return monitorDetailUuid;
    }

    public void setMonitorDetailUuid(String monitorDetailUuid) {
        this.monitorDetailUuid = monitorDetailUuid;
    }
}
