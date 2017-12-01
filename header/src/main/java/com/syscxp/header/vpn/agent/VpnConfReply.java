package com.syscxp.header.vpn.agent;

import com.syscxp.header.message.MessageReply;

public class VpnConfReply extends MessageReply {
    private String vpnSucc;

    public String getVpnSucc() {
        return vpnSucc;
    }

    public void setVpnSucc(String vpnSucc) {
        this.vpnSucc = vpnSucc;
    }
}
