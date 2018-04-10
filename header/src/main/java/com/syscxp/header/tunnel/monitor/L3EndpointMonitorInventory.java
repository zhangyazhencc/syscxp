package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.tunnel.network.L3EndpointInventory;
import com.syscxp.header.tunnel.network.L3EndpointVO;

import java.util.*;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-4-8.
 * @Description: .
 */
public class L3EndpointMonitorInventory {

    private L3EndpointInventory l3Endpoint;
    private Map<String, String> dstL3Endpoints;
    private Set<String> monitors;

    public static L3EndpointMonitorInventory valueOf(L3EndpointVO l3Endpoint, List<L3EndpointVO> dstL3Endpoints, List<L3NetworkMonitorVO> monitors) {
        L3EndpointMonitorInventory inventory = new L3EndpointMonitorInventory();

        inventory.setL3Endpoint(L3EndpointInventory.valueOf(l3Endpoint));
        Map<String, String> map = new HashMap<>();
        for (L3EndpointVO dstL3Endpoint : dstL3Endpoints)
            map.put(dstL3Endpoint.getUuid(), dstL3Endpoint.getEndpointEO().getName());
        inventory.setDstL3Endpoints(map);

        Set<String> set = new HashSet<>();
        for (L3NetworkMonitorVO monitor : monitors)
            set.add(monitor.getDstL3EndpointUuid());
        inventory.setMonitors(set);

        return inventory;
    }

    public L3EndpointInventory getL3Endpoint() {
        return l3Endpoint;
    }

    public void setL3Endpoint(L3EndpointInventory l3Endpoint) {
        this.l3Endpoint = l3Endpoint;
    }

    public Map<String, String> getDstL3Endpoints() {
        return dstL3Endpoints;
    }

    public void setDstL3Endpoints(Map<String, String> dstL3Endpoints) {
        this.dstL3Endpoints = dstL3Endpoints;
    }

    public Set<String> getMonitors() {
        return monitors;
    }

    public void setMonitors(Set<String> monitors) {
        this.monitors = monitors;
    }
}
