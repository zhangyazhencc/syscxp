package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.tunnel.tunnel.TunnelVO;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-11-13.
 * @Description: 实时查询测速结果.
 */

@SuppressCredentialCheck
public class APIQueryMonitorResultMsg extends APISyncCallMessage {
    @APIParam(emptyString = false)
    private Long start;

    @APIParam(emptyString = false)
    private Long end;

    @APIParam(emptyString = false)
    private String[] metrics;

    @APIParam(emptyString = false, resourceType = TunnelVO.class)
    private String tunnelUuid;

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public String[] getMetrics() {
        return metrics;
    }

    public void setMetrics(String[] metrics) {
        this.metrics = metrics;
    }
}
