package com.syscxp.header.network.l3;


import com.syscxp.header.message.APISyncCallMessage;

public class APIDeleteUsedIpMsg extends APISyncCallMessage {
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
