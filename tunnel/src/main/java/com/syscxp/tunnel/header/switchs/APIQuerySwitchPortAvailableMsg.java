package com.syscxp.tunnel.header.switchs;

import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.query.AutoQuery;

@AutoQuery(replyClass = APIQuerySwitchPortAvailableReply.class, inventoryClass = SwitchPortAvailableInventory.class)
public class APIQuerySwitchPortAvailableMsg extends APISyncCallMessage {
    @APIParam
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
