package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.NeedReplyMessage;

/**
 * Create by DCY on 2018/5/9
 */
public class ListTETraceMsg extends NeedReplyMessage {

    private String tunnelUuid;

    private TETraceType traceType;

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public TETraceType getTraceType() {
        return traceType;
    }

    public void setTraceType(TETraceType traceType) {
        this.traceType = traceType;
    }
}
