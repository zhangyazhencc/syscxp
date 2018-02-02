package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.message.APIEvent;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-02-01.
 * @Description: .
 */
public class APIInitTunnelMonitorEvent extends APIEvent {
    public APIInitTunnelMonitorEvent(String apiId) {super(apiId);}

    public APIInitTunnelMonitorEvent(){}
}
