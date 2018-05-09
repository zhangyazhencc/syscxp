package com.syscxp.header.vpn.agent;


import java.util.Map;

public class InitVpnMsg extends VpnMessage {
    DhcpPools dhcpPools;
    Map<String, Object> route;

    public DhcpPools getDhcpPools() {
        return dhcpPools;
    }

    public void setDhcpPools(DhcpPools dhcpPools) {
        this.dhcpPools = dhcpPools;
    }

    public Map<String, Object> getRoute() {
        return route;
    }

    public void setRoute(Map<String, Object> route) {
        this.route = route;
    }

}
