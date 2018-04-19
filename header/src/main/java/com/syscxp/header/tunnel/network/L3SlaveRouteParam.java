package com.syscxp.header.tunnel.network;

/**
 * Create by DCY on 2018/4/19
 */
public class L3SlaveRouteParam {

    private String routeIp;

    private Integer preference;

    public String getRouteIp() {
        return routeIp;
    }

    public void setRouteIp(String routeIp) {
        this.routeIp = routeIp;
    }

    public Integer getPreference() {
        return preference;
    }

    public void setPreference(Integer preference) {
        this.preference = preference;
    }
}
