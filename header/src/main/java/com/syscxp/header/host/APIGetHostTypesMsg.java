package com.syscxp.header.host;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APISyncCallMessage;

@Action(services = {"tunnel", "vpn"}, category = HostConstant.ACTION_CATEGORY, names = {"read"}, adminOnly = true)
public class APIGetHostTypesMsg extends APISyncCallMessage {
 
    public static APIGetHostTypesMsg __example__() {
        APIGetHostTypesMsg msg = new APIGetHostTypesMsg();


        return msg;
    }

}
