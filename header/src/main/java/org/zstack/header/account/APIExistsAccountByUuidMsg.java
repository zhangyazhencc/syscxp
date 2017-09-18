package org.zstack.header.account;

import org.zstack.header.message.APIParam;
import org.zstack.header.message.APISyncCallMessage;

public class APIExistsAccountByUuidMsg  extends APISyncCallMessage {

    @APIParam(emptyString = false)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
