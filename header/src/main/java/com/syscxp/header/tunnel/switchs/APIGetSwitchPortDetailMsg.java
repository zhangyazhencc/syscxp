package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

/**
 * Create by DCY on 2018/2/24
 */
public class APIGetSwitchPortDetailMsg extends APISyncCallMessage {

    @APIParam(emptyString = false,resourceType = SwitchPortVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
