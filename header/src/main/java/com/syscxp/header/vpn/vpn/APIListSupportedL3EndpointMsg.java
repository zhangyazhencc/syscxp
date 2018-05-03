package com.syscxp.header.vpn.vpn;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

@Action(services = {VpnConstant.ACTION_SERVICE}, category = VpnConstant.ACTION_CATEGORY_VPN, names = {"read"})
public class APIListSupportedL3EndpointMsg extends APISyncCallMessage {
    @APIParam(emptyString = false)
    private String l3NetworkUuid;

    public String getL3NetworkUUuid() {
        return l3NetworkUuid;
    }

    public void setL3NetworkUUuid(String l3NetworkUuid) {
        this.l3NetworkUuid = l3NetworkUuid;
    }
}
