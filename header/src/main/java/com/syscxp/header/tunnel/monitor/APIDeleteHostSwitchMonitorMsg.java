package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.MonitorConstant;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-11.
 * @Description: 删除监控主机与物理交换机关联.
 */

@Action(services = {"tunnel"}, category = MonitorConstant.ACTION_CATEGORY)
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
