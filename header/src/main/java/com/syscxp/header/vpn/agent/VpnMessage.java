package com.syscxp.header.vpn.agent;

import com.syscxp.header.message.NeedReplyMessage;

public class VpnMessage extends NeedReplyMessage {
    private String vpnUuid;

    public void setVpnUuid(String vpnUuid) {
        this.vpnUuid = vpnUuid;
    }

    public String getVpnUuid() {
        return vpnUuid;
    }
}
