package com.syscxp.header.host;

import com.syscxp.header.message.NeedReplyMessage;

public class DeleteHostMsg extends NeedReplyMessage implements HostMessage {
    private String uuid;

    public DeleteHostMsg() {
    }

    public DeleteHostMsg(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getHostUuid() {
        return getUuid();
    }
}
