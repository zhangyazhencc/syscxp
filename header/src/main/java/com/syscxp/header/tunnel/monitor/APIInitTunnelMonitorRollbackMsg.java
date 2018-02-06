package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-02-01.
 * @Description: 初始化监控数据回滚.
 */
public class APIInitTunnelMonitorRollbackMsg extends APIMessage {
    @APIParam(required = false)
    private String tunnelUuid;

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }
}
