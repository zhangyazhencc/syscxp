package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIReply;

/**
 * Create by DCY on 2018/5/28
 */
public class APIGetTunnelTypeReply extends APIReply {

    private String tunnelType;

    public String getTunnelType() {
        return tunnelType;
    }

    public void setTunnelType(String tunnelType) {
        this.tunnelType = tunnelType;
    }
}
