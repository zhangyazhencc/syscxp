package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.tunnel.network.L3EndpointInventory;
import com.syscxp.header.tunnel.network.L3EndpointVO;

import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-03-22.
 * @Description: .
 */
public class L3EndpointMonitorInventory {
    L3EndpointInventory endpoint;
    List<L3NetworkMonitorInventory> monitors;

    public static L3EndpointMonitorInventory valueOf(L3EndpointVO endpointVO, List<L3NetworkMonitorVO> monitorVOS){
        L3EndpointMonitorInventory inventory = new L3EndpointMonitorInventory();
        inventory.setEndpoint(L3EndpointInventory.valueOf(endpointVO));
        inventory.setMonitors(L3NetworkMonitorInventory.valueOf(monitorVOS));
        return inventory;
    }

    public L3EndpointInventory getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(L3EndpointInventory endpoint) {
        this.endpoint = endpoint;
    }

    public List<L3NetworkMonitorInventory> getMonitors() {
        return monitors;
    }

    public void setMonitors(List<L3NetworkMonitorInventory> monitors) {
        this.monitors = monitors;
    }
}

