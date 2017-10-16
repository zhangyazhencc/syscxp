package com.syscxp.tunnel.manage;



/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-10-09.
 * @Description: .
 */
public interface ControllerRestConstant {
    //测试
    static final String SYNC_TEST = "/demo/sync";
    static final String ASYNC_TEST = "/demo/async";

    static final String START_TUNNEL = "/tunnel/tunnel_start";
    static final String START_MODIFY = "/tunnel/tunnel_modify";
    static final String STOP_TUNNEL = "/tunnel/tunnel_stop";
    static final String START_TUNNEL_MONITOR = "/tunnel_monitor/start";
    static final String STOP_TUNNEL_MONITOR = "/tunnel_monitor/stop";
}
