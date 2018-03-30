package com.syscxp.header.account;

import com.syscxp.header.identity.APISessionMessage;
import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.identity.PasswordNoSee;
import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.APIParam;

@InnerCredentialCheck
public class APILogInBySecretIdMsg extends APISessionMessage {

    @APIParam
    private String secretId;

    @APIParam
    @PasswordNoSee
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
