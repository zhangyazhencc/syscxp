package com.syscxp.header.account;

import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

@InnerCredentialCheck
public class APIValidateAccountMsg extends APISyncCallMessage {

    @APIParam(emptyString = false)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
