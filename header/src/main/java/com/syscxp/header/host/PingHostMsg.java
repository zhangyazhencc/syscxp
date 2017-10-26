package com.syscxp.header.host;

import com.syscxp.header.message.NeedReplyMessage;

/**
 */
public class PingHostMsg extends NeedReplyMessage implements HostMessage {
    private String hostUuid;

    public String getHostUuid() {
        return hostUuid;
    }

    public void setHostUuid(String hostUuid) {
        this.hostUuid = hostUuid;
    }
}
