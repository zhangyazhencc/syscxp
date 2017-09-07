package org.zstack.header.message;

import org.zstack.utils.gson.JSONObjectUtil;

import java.sql.Timestamp;

public abstract class InnerAPIMessage extends APIMessage {

    private String signature;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
