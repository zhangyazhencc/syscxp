package com.syscxp.header.vpn.vpn;

import com.syscxp.header.message.APIReply;
import com.syscxp.header.vpn.host.VpnHostInventory;

import java.util.List;
import java.util.Map;

public class APIListVpnHostReply extends APIReply {
    private List<String> endpointUuids;

    public List<String> getEndpointUuids() {
        return endpointUuids;
    }

    public void setEndpointUuids(List<String> endpointUuids) {
        this.endpointUuids = endpointUuids;
    }
}

