package com.syscxp.header.vpn.agent;

import com.syscxp.header.vpn.vpn.VpnSystemVO;

import java.util.Map;

public class ClientInfo {
    private String id;
    private Map<String, Object> vpn;
    private Map<String, Object> tap;
    private Map<String, Object> system;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getVpn() {
        return vpn;
    }

    public void setVpn(Map<String, Object> vpn) {
        this.vpn = vpn;
    }

    public Map<String, Object> getTap() {
        return tap;
    }

    public void setTap(Map<String, Object> tap) {
        this.tap = tap;
    }

    public Map<String, Object> getSystem() {
        return system;
    }

    public void setSystem(Map<String, Object> system) {
        this.system = system;
    }
}
