package com.syscxp.tunnel.header.switchs;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

/**
 * Created by DCY on 2017-09-13
 */
public class APIDeletePhysicalSwitchMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = PhysicalSwitchVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}