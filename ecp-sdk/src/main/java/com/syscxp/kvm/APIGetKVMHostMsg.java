package com.syscxp.kvm;

import com.syscxp.header.message.APISyncCallMessage;

public class APIGetKVMHostMsg extends APISyncCallMessage {
    private String uuid;

    private String accountUuid;

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
