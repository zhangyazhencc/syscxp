package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.tunnel.TunnelVO;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-11.
 * @Description: 创建监控通道.
 */
public class APIStopTunnelMonitorMsg extends APIMessage {
    @APIParam(emptyString = false,resourceType = TunnelVO.class,maxLength = 32)
    private String tunnelUuid;

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }
}