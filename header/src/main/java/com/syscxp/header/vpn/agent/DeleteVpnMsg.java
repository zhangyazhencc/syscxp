package com.syscxp.header.vpn.agent;


import com.syscxp.header.message.NeedReplyMessage;

public class DeleteVpnMsg extends NeedReplyMessage {
    private boolean expired;
    private String vpnUuid;

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public String getVpnUuid() {
        return vpnUuid;
    }

    public void setVpnUuid(String vpnUuid) {
        this.vpnUuid = vpnUuid;
    }
}
