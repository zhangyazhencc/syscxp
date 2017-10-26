package com.syscxp.header.host;

import com.syscxp.header.message.Message;

public class RemovePingTaskMsg extends Message {
    private String hostUuid;

    public String getHostUuid() {
        return hostUuid;
    }

    public void setHostUuid(String hostUuid) {
        this.hostUuid = hostUuid;
    }
}
