package com.syscxp.tunnel.monitor;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-03-22.
 * @Description: .
 */
public interface RyuControllerBase {
    <T> T httpCall(String method, String command, Class<T> returnClass);
}