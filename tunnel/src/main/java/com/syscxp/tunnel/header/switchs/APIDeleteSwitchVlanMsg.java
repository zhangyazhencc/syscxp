package com.syscxp.tunnel.header.switchs;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

/**
 * Created by DCY on 2017-09-13
 */
public class APIDeleteSwitchVlanMsg extends APIMessage {
    @APIParam(emptyString = false, resourceType = SwitchVlanVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
