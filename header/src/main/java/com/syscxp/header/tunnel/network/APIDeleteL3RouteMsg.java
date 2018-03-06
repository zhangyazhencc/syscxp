package com.syscxp.header.tunnel.network;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

public class APIDeleteL3RouteMsg extends APIMessage{

    @APIParam
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

}
