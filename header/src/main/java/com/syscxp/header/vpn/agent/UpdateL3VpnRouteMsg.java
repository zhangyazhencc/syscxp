package com.syscxp.header.vpn.agent;

import java.util.Map;

public class UpdateL3VpnRouteMsg extends VpnMessage {

    Map<String, Object> route;

    public Map<String, Object> getRoute() { return route; }

    public void setRoute(Map<String, Object> route) { this.route = route; }
}
