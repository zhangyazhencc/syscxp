package com.syscxp.tunnel.monitor;

import com.syscxp.core.Platform;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.job.JobQueueFacade;
import com.syscxp.header.Component;
import com.syscxp.header.tunnel.monitor.L3NetworkMonitorVO;
import com.syscxp.header.tunnel.network.L3EndpointState;
import com.syscxp.header.tunnel.network.L3EndpointVO;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-03-22.
 * @Description: .
 */
public class L3NetworkMonitorBaseImpl implements L3NetworkMonitorBase, Component {

    private static final CLogger logger = Utils.getLogger(L3NetworkMonitorBaseImpl.class);

    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private MonitorAgentBase monitorAgent;
    @Autowired
    private RyuControllerBase ryuController;
    @Autowired
    private JobQueueFacade jobf;

    @Override
    @Transactional
    public void startMonitor(L3EndpointVO l3EndpointVO, List<L3NetworkMonitorVO> l3NetworkMonitorVOS) {
        if (l3EndpointVO.getState() == L3EndpointState.Enabled) {
            // 开启控制器监控
            startControllerMonitor(l3EndpointVO);

            // 配置监控机路由
            addAgentRoute(l3EndpointVO);

            // 下发监控机监控
            startAgentMonitor(l3NetworkMonitorVOS);
        }
    }

    @Override
    public void stopMonitor(L3EndpointVO l3EndpointVO, List<L3NetworkMonitorVO> l3NetworkMonitorVOS) {

    }

    @Override
    public L3NetworkMonitorVO updateSrcMonitor(L3EndpointVO l3EndpointVO, List<L3NetworkMonitorVO> l3NetworkMonitorVOS) {
        return null;
    }

    @Override
    public L3NetworkMonitorVO updateDstMonitor(L3EndpointVO l3EndpointVO, List<L3NetworkMonitorVO> l3NetworkMonitorVOS) {
        return null;
    }

    @Override
    public void addAgentRoute(L3EndpointVO vo) {

    }

    @Override
    public void deleteAgentRoute(L3EndpointVO vo) {

    }

    @Override
    public void startAgentMonitor(List<L3NetworkMonitorVO> l3NetworkMonitorVOS) {

    }

    @Override
    public void stopAgentMonitor(L3NetworkMonitorVO vo) {

    }

    @Override
    public void updateAgentMonitor(L3NetworkMonitorVO vo) {

    }

    @Override
    public void startControllerMonitor(L3EndpointVO vo) {
        RyuControllerBaseImpl.RyuNetworkResponse resp = new RyuControllerBaseImpl.RyuNetworkResponse();

        // resp = monitorAgent.httpCall();
    }

    @Override
    public void stopControllerMonitor(L3EndpointVO vo) {
        RyuControllerBaseImpl.RyuNetworkResponse resp = new RyuControllerBaseImpl.RyuNetworkResponse();

        // resp = monitorAgent.httpCall();
    }

    @Override
    public boolean start() {
        return false;
    }

    @Override
    public boolean stop() {
        return false;
    }

}
