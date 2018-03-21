package com.syscxp.header.tunnel.network;

import com.syscxp.header.message.NeedReplyMessage;

/**
 * Create by DCY on 2018/3/12
 */
public class DeleteL3RouteMsg extends NeedReplyMessage {
    private String l3RouteUuid;

    private String taskUuid;

    public String getL3RouteUuid() {
        return l3RouteUuid;
    }

    public void setL3RouteUuid(String l3RouteUuid) {
        this.l3RouteUuid = l3RouteUuid;
    }

    public String getTaskUuid() {
        return taskUuid;
    }

    public void setTaskUuid(String taskUuid) {
        this.taskUuid = taskUuid;
    }
}
