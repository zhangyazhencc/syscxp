package com.syscxp.account.header.identity;

import com.syscxp.header.identity.APISessionMessage;
import com.syscxp.header.identity.SuppressCredentialCheck;

@SuppressCredentialCheck
public class APILogOutMsg extends APISessionMessage {
    private String sessionUuid;

    public String getSessionUuid() {
        return sessionUuid;
    }

    public void setSessionUuid(String sessionUuid) {
        this.sessionUuid = sessionUuid;
    }
}
