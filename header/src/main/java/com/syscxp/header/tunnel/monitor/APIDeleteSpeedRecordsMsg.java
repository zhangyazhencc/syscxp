package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-11-14.
 * @Description: 删除测速专线.
 */
public class APIDeleteSpeedRecordsMsg extends APIMessage {
    @APIParam(emptyString = false,resourceType = SpeedRecordsVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
