package com.syscxp.header.vpn.vpn;

import com.syscxp.header.message.APIReply;

public class APICheckVpnForTunnelReply extends APIReply {
    private boolean used;

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
