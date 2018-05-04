package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-05-02.
 * @Description: .
 */
@InnerCredentialCheck
public class APIQueryL3NetworkMonitorDetailMsg extends APISyncCallMessage {

    @APIParam
    private List<String> monitorUuids;

    public List<String> getMonitorUuids() {
        return monitorUuids;
    }

    public void setMonitorUuids(List<String> monitorUuids) {
        this.monitorUuids = monitorUuids;
    }
}
