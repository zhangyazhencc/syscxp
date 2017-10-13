package com.syscxp.tunnel.header.aliEdgeRouter;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

public class APIDeleteAliEdgeRouterConfigMsg extends APIMessage {
    @APIParam(checkAccount = true,resourceType = AliEdgeRouterConfigVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
