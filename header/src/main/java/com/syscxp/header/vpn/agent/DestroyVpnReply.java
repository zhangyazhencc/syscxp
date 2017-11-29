package com.syscxp.header.vpn.agent;

import com.syscxp.header.message.MessageReply;

public class DestroyVpnReply extends MessageReply {
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
