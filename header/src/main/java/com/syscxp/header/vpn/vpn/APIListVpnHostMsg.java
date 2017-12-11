package com.syscxp.header.vpn.vpn;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.vpn.VpnConstant;

import java.util.List;

@Action(services = {VpnConstant.ACTION_SERVICE}, category = VpnConstant.ACTION_CATEGORY_VPN, names = {"read"})
public class APIListVpnHostMsg extends APISyncCallMessage {
    @APIParam(nonempty = true, nullElements = true)
    private List<String> endpointUuids;

    public List<String> getEndpointUuids() {

        return endpointUuids;
    }

    public void setEndpointUuids(List<String> endpointUuids) {
        this.endpointUuids = endpointUuids;
    }
}
