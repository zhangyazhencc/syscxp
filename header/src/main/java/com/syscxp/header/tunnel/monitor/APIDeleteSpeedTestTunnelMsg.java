package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.MonitorConstant;
import com.syscxp.header.tunnel.tunnel.TunnelVO;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-11-14.
 * @Description: 删除测速专线.
 */

@Action(services = {"tunnel"}, category = MonitorConstant.ACTION_CATEGORY)
public class APIDeleteSpeedTestTunnelMsg extends APIMessage {
    @APIParam(emptyString = false,resourceType = SpeedTestTunnelVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
