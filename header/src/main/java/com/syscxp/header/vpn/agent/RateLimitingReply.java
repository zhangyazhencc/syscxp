package com.syscxp.header.vpn.agent;

import com.syscxp.header.message.MessageReply;

public class RateLimitingReply extends MessageReply {
    private String vpnLimit;

    public String getVpnLimit() {
        return vpnLimit;
    }

    public void setVpnLimit(String vpnLimit) {
        this.vpnLimit = vpnLimit;
    }
}
