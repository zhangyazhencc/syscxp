package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-11-13.
 * @Description: 实时查询测速结果.
 */
public class APIQueryNettoolResultMsg extends APISyncCallMessage {
    @APIParam(emptyString = false)
    private String guid;

    @APIParam(emptyString = false)
    private String hostIp;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }
}