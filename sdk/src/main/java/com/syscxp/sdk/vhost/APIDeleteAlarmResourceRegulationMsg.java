package com.syscxp.sdk.vhost;


import com.syscxp.header.message.APISyncCallMessage;

public class APIDeleteAlarmResourceRegulationMsg  extends APISyncCallMessage {

    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
