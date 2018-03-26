package com.syscxp.tunnel.tunnel.job;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-12-29.
 * @Description: .
 */
public enum  MonitorJobType {
    CONTROLLER_START,
    CONTROLLER_STOP,
    CONTROLLER_MODIFY,
    CONTROLLER_ROLLBACK,
    CONTROLLER_DELETE,
    AGENT_START,
    AGENT_STOP,
    AGENT_MODIFY,
    AGENT_ADD_ROUTE,
    AGENT_DELETE_ROUTE
}
