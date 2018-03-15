package com.syscxp.header.tunnel.network;

import com.syscxp.header.message.NeedReplyMessage;

/**
 * Create by DCY on 2018/3/12
 */
public class DeleteL3EndPointMsg extends NeedReplyMessage {
    private String l3EndpointUuid;

    private String taskUuid;

    public String getL3EndpointUuid() {
        return l3EndpointUuid;
    }

    public void setL3EndpointUuid(String l3EndpointUuid) {
        this.l3EndpointUuid = l3EndpointUuid;
    }

    public String getTaskUuid() {
        return taskUuid;
    }

    public void setTaskUuid(String taskUuid) {
        this.taskUuid = taskUuid;
    }
}
