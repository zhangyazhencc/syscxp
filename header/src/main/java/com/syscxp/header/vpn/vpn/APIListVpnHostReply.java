package com.syscxp.header.vpn.vpn;

import com.syscxp.header.message.APIReply;
import com.syscxp.header.vpn.host.VpnHostInventory;

import java.util.List;
import java.util.Map;

public class APIListVpnHostReply extends APIReply {
    private Map<String, Boolean> map;

    public Map<String, Boolean> getMap() {
        return map;
    }

    public void setMap(Map<String, Boolean> map) {
        this.map = map;
    }
}

