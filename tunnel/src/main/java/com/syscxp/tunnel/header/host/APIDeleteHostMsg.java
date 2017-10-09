package com.syscxp.tunnel.header.host;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-14.
 * @Description: .
 */
public class APIDeleteHostMsg extends APIMessage {
    @APIParam(emptyString = false,checkAccount = true,resourceType = HostEO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
