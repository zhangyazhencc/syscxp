package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.tunnel.MonitorConstant;
import com.syscxp.header.tunnel.tunnel.TunnelVO;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-11-13.
 * @Description: 实时查询测速结果.
 */

@Action(services = {"tunnel"}, category = MonitorConstant.ACTION_CATEGORY, names = {"read"})
public class APIQueryMonitorResultMsg extends APISyncCallMessage {
    @APIParam(emptyString = false)
    private Long start;

    @APIParam(emptyString = false)
    private Long end;

    @APIParam(emptyString = false)
    private String metric;

    @APIParam(emptyString = false,resourceType = TunnelVO.class)
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

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }
}
