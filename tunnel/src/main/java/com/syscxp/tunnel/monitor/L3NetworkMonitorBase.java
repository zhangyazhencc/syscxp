package com.syscxp.tunnel.monitor;

import com.syscxp.header.tunnel.monitor.L3NetworkMonitorVO;
import com.syscxp.header.tunnel.network.L3EndpointVO;

import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-03-22.
 * @Description: .
 */
public interface L3NetworkMonitorBase {
    /***
     * 前端维护监控ip
     * @param l3EndpointVO
     * @param srcL3NetworkMonitorVOS
     */
    void startMonitor(L3EndpointVO l3EndpointVO, List<L3NetworkMonitorVO> srcL3NetworkMonitorVOS);

    /***
     * 连接点中止后恢复，且监控ip不为空，恢复监控
     * @param l3EndpointVO
     */
    void startMonitor(L3EndpointVO l3EndpointVO);

    /**
     * 前端删除监控ip
     * @param l3EndpointVO
     */
    void stopMonitor(L3EndpointVO l3EndpointVO);

    /**
     * 中止连接点，且监控ip不为空，中止监控（不删除L3NetworkMonitorVO监控数据）
     * @param l3EndpointVO
     */
    void stopMonitorByDisableL3Endpoint(L3EndpointVO l3EndpointVO);

    /**
     * 中止连接点后，删除链接点，删除原监控数据
     * @param l3EndpointVO
     */
    void deleteMonitorData(L3EndpointVO l3EndpointVO);

    void updateMonitorVO(L3EndpointVO l3EndpointVO, List<L3NetworkMonitorVO> srcL3NetworkMonitorVOS);

    void updateMonitorIp(L3EndpointVO l3EndpointVO, List<L3NetworkMonitorVO> srcL3NetworkMonitorVOS);

    void addAgentRoute(L3EndpointVO vo);

    void deleteAgentRoute(L3EndpointVO vo);

    void startAgentMonitor(L3NetworkMonitorVO vo);

    void stopAgentMonitor(L3NetworkMonitorVO vo);

    void updateAgentMonitor(L3NetworkMonitorVO vo);

    void startControllerMonitor(L3EndpointVO vo);

    void stopControllerMonitor(L3EndpointVO vo);
}
