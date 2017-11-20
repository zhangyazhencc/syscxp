package com.syscxp.header.host;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

@Action(services = {"tunnel", "vpn"}, category = HostConstant.ACTION_CATEGORY, names = {"read"}, adminOnly = true)
public class APIGetHostMsg extends APISyncCallMessage {

    @APIParam(resourceType = HostVO.class)
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
