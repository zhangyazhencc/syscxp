package com.syscxp.header.vpn.agent;

import java.util.Map;

public class UpdateL3RouteMsg extends VpnMessage {

    private Map route;

    public Map getRoute() {
        return route;
    }

    public void setRoute(Map route) {
        this.route = route;
    }
}
