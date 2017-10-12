package org.zstack.tunnel.header.router;

import org.zstack.header.message.APIParam;
import org.zstack.header.message.APISyncCallMessage;

public class APIGetRouterMsg extends APISyncCallMessage {

    @APIParam(emptyString = false,resourceType = RouterVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
