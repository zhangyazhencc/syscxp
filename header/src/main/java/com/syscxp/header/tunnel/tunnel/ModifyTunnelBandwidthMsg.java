package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.NeedReplyMessage;

/**
 * Create by DCY on 2017/10/26
 */
public class ModifyTunnelBandwidthMsg extends NeedReplyMessage {
    private String tunnelUuid;

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }
}
