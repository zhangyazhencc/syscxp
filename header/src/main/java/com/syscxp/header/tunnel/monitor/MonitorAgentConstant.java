package com.syscxp.header.tunnel.monitor;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-11-13.
 * @Description: .
 */
public interface MonitorAgentConstant {
    static final String SERVER_PORT = "7079";
    static final String IPERF = "/iperf/iperf";
    static final String IPERF_RESULT = "/iperf/results";
    static final String NETTOOL = "/netools/netools";
    static final String NETTOOL_RESULT = "/netools/results";
    static final String START_MONITOR = "/icmp/start_monitor";
    static final String UPDATE_MONITOR = "/icmp/update_monitor";
    static final String STOP_MONITOR = "/icmp/stop_monitor";

    static final String L3_ADD_ROUTE = "/l3net/add_route";
    static final String L3_DELETE_ROUTE = "/l3net/delete_route";
    static final String L3_UPDATE_ROUTE = "/l3net/update_route";
    static final String L3_START_MONITOR = "/l3net/start_monitor";
    static final String L3_UPDATE_MONITOR = "/l3net/update_monitor";
    static final String L3_UPDATE_MONITOR_IP = "/l3net/update_monitor_ip";
    static final String L3_STOP_MONITOR = "/l3net/stop_monitor";

}
