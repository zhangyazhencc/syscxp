package com.syscxp.tunnel.sdk.sdn.service;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-10-09.
 * @Description: .
 */
public interface RyuRestConstant {
    // 同步测试接口
    static final String SYNC_TEST_URL = "http://localhost:8088/demo/sync";

    // 异步测试接口
    static final String ASYNC_TEST_URL = "http://localhost:8088/demo/async";

    // 监控通道配置下发接口
    static final String MONITOR_CONFIG_START = "http://192.168.211.224:8080/tunnel_monitor/start";
}
