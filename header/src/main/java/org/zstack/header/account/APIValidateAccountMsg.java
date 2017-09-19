package org.zstack.header.account;

import org.zstack.header.identity.InnerCredentialCheck;
import org.zstack.header.message.APIParam;
import org.zstack.header.message.APISyncCallMessage;

@InnerCredentialCheck
public class APIValidateAccountMsg extends APISyncCallMessage {

    @APIParam(emptyString = false)
    private String uuid;

    private String name;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
