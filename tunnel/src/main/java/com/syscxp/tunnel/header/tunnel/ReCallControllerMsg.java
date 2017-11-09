package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.message.NeedReplyMessage;

/**
 * Create by DCY on 2017/11/9
 */
public class ReCallControllerMsg extends NeedReplyMessage {
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
