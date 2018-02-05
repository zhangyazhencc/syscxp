package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.message.APIEvent;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-02-01.
 * @Description: .
 */
public class APIInitTunnelMonitorRollbackEvent extends APIEvent {
    public APIInitTunnelMonitorRollbackEvent(String apiId) {super(apiId);}

    public APIInitTunnelMonitorRollbackEvent(){}
}
