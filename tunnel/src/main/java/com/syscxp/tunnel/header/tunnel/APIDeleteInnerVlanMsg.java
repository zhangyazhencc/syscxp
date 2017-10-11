package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

/**
 * Create by DCY on 2017/10/11
 */
public class APIDeleteInnerVlanMsg extends APIMessage {
    @APIParam(emptyString = false, resourceType = QinqVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}