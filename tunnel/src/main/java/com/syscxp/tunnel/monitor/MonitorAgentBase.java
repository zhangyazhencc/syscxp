package com.syscxp.tunnel.monitor;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-03-22.
 * @Description: .
 */
public interface MonitorAgentBase {
    <T> T httpCall(String hostIp, String method, String command, Class<T> returnClass);
}
