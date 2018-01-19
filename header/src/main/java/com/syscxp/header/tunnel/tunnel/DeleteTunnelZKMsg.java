package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.NeedReplyMessage;

/**
 * Create by DCY on 2018/1/18
 */
public class DeleteTunnelZKMsg extends NeedReplyMessage {
    private String taskUuid;

    private String commands;

    public String getTaskUuid() {
        return taskUuid;
    }

    public void setTaskUuid(String taskUuid) {
        this.taskUuid = taskUuid;
    }

    public String getCommands() {
        return commands;
    }

    public void setCommands(String commands) {
        this.commands = commands;
    }
}
