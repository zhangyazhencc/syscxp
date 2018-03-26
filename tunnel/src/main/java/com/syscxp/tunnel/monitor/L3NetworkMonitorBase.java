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
    void startMonitor(L3EndpointVO l3EndpointVO, List<L3NetworkMonitorVO> l3NetworkMonitorVOS);

    void stopMonitor(L3EndpointVO l3EndpointVO, List<L3NetworkMonitorVO> l3NetworkMonitorVOS);

    L3NetworkMonitorVO updateSrcMonitor(L3EndpointVO l3EndpointVO, List<L3NetworkMonitorVO> l3NetworkMonitorVOS);

    L3NetworkMonitorVO updateDstMonitor(L3EndpointVO l3EndpointVO, List<L3NetworkMonitorVO> l3NetworkMonitorVOS);


    void addAgentRoute(L3EndpointVO vo);

    void deleteAgentRoute(L3EndpointVO vo);

    void startAgentMonitor(L3NetworkMonitorVO vo);

    void stopAgentMonitor(L3NetworkMonitorVO vo);

    void modifyAgentMonitor(L3NetworkMonitorVO vo);

    void startControllerMonitor(L3EndpointVO vo);

    void stopControllerMonitor(L3EndpointVO vo);
}
