package com.syscxp.account.header.account;

import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

@InnerCredentialCheck
public class APIGetSecretKeyMsg extends APISyncCallMessage implements  AccountMessage {

    @APIParam(maxLength = 32)
    private String secretId;

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    @Override
    public String getAccountUuid() {
        return null;
    }
}
