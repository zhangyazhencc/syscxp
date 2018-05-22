package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.NeedReplyMessage;

/**
 * Create by DCY on 2017/10/26
 */
public class EnabledTunnelMsg extends NeedReplyMessage implements LocalTunnelControlMessage {
    private String tunnelUuid;

    private String taskUuid;

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public String getTaskUuid() {
        return taskUuid;
    }

    public void setTaskUuid(String taskUuid) {
        this.taskUuid = taskUuid;
    }
}
