package com.syscxp.header.account;

import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

@InnerCredentialCheck
public class APIGetSecretKeyMsg extends APISyncCallMessage {

    @APIParam(maxLength = 32)
    private String secretId;

    @APIParam
    private String IP;

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }
}
