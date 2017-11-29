package com.syscxp.tunnel.sdnController;



/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-10-09.
 * @Description: .
 */
public interface ControllerRestConstant {
    static final String START_TUNNEL = "/tunnel/create";
    static final String MODIFY_TUNNEL_BANDWIDTH = "/tunnel/bandwidth_modify";
    static final String MODIFY_TUNNEL_PORTS = "/tunnel/ports_modify";
    static final String STOP_TUNNEL = "/tunnel/delete";
    static final String TUNNEL_TRACE = "/tunnel_trace";

    static final String START_TUNNEL_MONITOR = "/tunnel_monitor/start";
    static final String STOP_TUNNEL_MONITOR = "/tunnel_monitor/stop";
}
