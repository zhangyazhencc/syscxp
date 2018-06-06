package com.syscxp.kvm;

import com.syscxp.header.message.APISyncCallMessage;

public class APIGetKVMHostMsg extends APISyncCallMessage {
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
