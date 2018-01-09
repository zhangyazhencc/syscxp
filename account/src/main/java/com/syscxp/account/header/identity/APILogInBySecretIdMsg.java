package com.syscxp.account.header.identity;

import com.syscxp.header.identity.APISessionMessage;
import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.APIParam;

@SuppressCredentialCheck
public class APILogInBySecretIdMsg extends APISessionMessage {

    @APIParam
    private String secretId;

    @APIParam
    private String secretKey;

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
