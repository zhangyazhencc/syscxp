package com.syscxp.header.account;

import com.syscxp.header.message.APIReply;

public class APILogInBySecretIdReply extends APIReply {
    private String sessionUuid;

    public String getSessionUuid() {
        return sessionUuid;
    }

    public void setSessionUuid(String sessionUuid) {
        this.sessionUuid = sessionUuid;
    }
}
