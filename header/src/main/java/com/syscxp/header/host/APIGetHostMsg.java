package com.syscxp.header.host;

import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

public class APIGetHostMsg extends APISyncCallMessage {

    @APIParam
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
 
    public static APIGetHostMsg __example__() {
        APIGetHostMsg msg = new APIGetHostMsg();


        return msg;
    }

}
