package com.syscxp.tunnel.header.monitor;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-11.
 * @Description: 删除监控主机与物理交换机关联.
 */
public class APIDeleteHostSwitchMonitorMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = HostSwitchMonitorVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
