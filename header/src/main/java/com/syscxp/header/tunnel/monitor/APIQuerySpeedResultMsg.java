package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.tunnel.TunnelVO;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-11-13.
 * @Description: 实时查询测速结果.
 */
public class APIQuerySpeedResultMsg extends APISyncCallMessage {
    @APIParam(emptyString = false,resourceType = SpeedRecordsVO.class)
    private String uuid;

    @APIParam(emptyString = false,resourceType = TunnelMonitorVO.class)
    private String srcTunnelMonitorUuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public String getSrcTunnelMonitorUuid() {
        return srcTunnelMonitorUuid;
    }

    public void setSrcTunnelMonitorUuid(String srcTunnelMonitorUuid) {
        this.srcTunnelMonitorUuid = srcTunnelMonitorUuid;
    }
}
