package com.syscxp.header.search;

import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APIMessage;

@Deprecated
public abstract class APIGetMessage extends APIMessage {
    @APIParam
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
