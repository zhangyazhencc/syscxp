package com.syscxp.account.header.account;


import com.syscxp.header.message.APIReply;

public class APIGetSecretKeyReply extends APIReply {

    private String secretKey;

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
