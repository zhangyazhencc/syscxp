package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;


public class APIDeleteResourcePolicyRefMsg extends APIMessage{

    @APIParam
    private String tunnelUuid;

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }
}
