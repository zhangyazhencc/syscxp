package com.syscxp.tunnel.header.aliEdgeRouter;

import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

public class APIGetAliEdgeRouterMsg extends APISyncCallMessage {
    @APIParam(emptyString = false,resourceType=AliEdgeRouterVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
