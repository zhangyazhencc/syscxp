package com.syscxp.header.host;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APISyncCallMessage;

@Action(category = HostConstant.ACTION_CATEGORY, adminOnly = true)
public class APIGetHostTypesMsg extends APISyncCallMessage {
 
    public static APIGetHostTypesMsg __example__() {
        APIGetHostTypesMsg msg = new APIGetHostTypesMsg();


        return msg;
    }

}
