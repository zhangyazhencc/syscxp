package com.syscxp.header.vpn.vpn;

import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

@InnerCredentialCheck
public class APICheckVpnForTunnelMsg extends APISyncCallMessage {
    @APIParam
    private String tunnelUuid;

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }
}
