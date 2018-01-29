package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.NeedReplyMessage;

/**
 * Create by DCY on 2018/1/24
 */
public class RollBackCreateTunnelMsg extends NeedReplyMessage {

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
