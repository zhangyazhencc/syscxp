package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.NeedReplyMessage;

/**
 * Create by DCY on 2017/11/28
 */
public class ListTunnelTraceMsg extends NeedReplyMessage {

    private String vsiId;

    private String sideA;

    private String sideZ;

    private String tunnelUuid;

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public String getVsiId() {
        return vsiId;
    }

    public void setVsiId(String vsiId) {
        this.vsiId = vsiId;
    }

    public String getSideA() {
        return sideA;
    }

    public void setSideA(String sideA) {
        this.sideA = sideA;
    }

    public String getSideZ() {
        return sideZ;
    }

    public void setSideZ(String sideZ) {
        this.sideZ = sideZ;
    }
}
