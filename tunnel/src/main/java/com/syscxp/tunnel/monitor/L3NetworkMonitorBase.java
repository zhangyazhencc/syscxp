package com.syscxp.tunnel.monitor;

import com.syscxp.header.tunnel.monitor.L3NetworkMonitorVO;
import com.syscxp.header.tunnel.monitor.MonitorAgentCommands;
import com.syscxp.header.tunnel.network.L3EndpointVO;

import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-03-22.
 * @Description: .
 */
public interface L3NetworkMonitorBase {
    /***
     * 开启监控，前端调用
     * 前端监控IP不为空，后端监控IP为空
     * @param l3EndpointVO
     * @param srcL3NetworkMonitorVOS
     */
    void startMonitor(L3EndpointVO l3EndpointVO, List<L3NetworkMonitorVO> srcL3NetworkMonitorVOS);

    /**
     * 停止监控，前端调用
     * 前端监控IP为空，后端监控IP不为空
     * @param l3EndpointVO
     */
    void stopMonitor(L3EndpointVO l3EndpointVO);

    /**
     * 仅处理前端传入的对端监控数据
     * 前端监控IP不为空，后端监控IP不为空，且前端监控IP = 后端监控IP
     * @param l3EndpointVO
     * @param srcL3NetworkMonitorVOS
     */
    void updateSrcMonitor(L3EndpointVO l3EndpointVO, List<L3NetworkMonitorVO> srcL3NetworkMonitorVOS);

    /**
     * 修改监控IP
     * 前端监控IP不为空，后端监控IP不为空，且前端监控IP ！= 后端监控IP
     * @param l3EndpointVO
     * @param srcL3NetworkMonitorVOS
     */
    void updateMonitorIp(L3EndpointVO l3EndpointVO, List<L3NetworkMonitorVO> srcL3NetworkMonitorVOS);

    /***
     * 连接点中止后恢复调用
     * 连接点中止后恢复，且监控ip不为空，恢复监控
     * @param l3EndpointVO
     */
    void startMonitor(L3EndpointVO l3EndpointVO);

    /**
     * 连接点中止后调用
     * 监控ip不为空
     * 不删除L3NetworkMonitorVO监控数据
     * @param l3EndpointVO
     */
    void stopMonitorByDisableL3Endpoint(L3EndpointVO l3EndpointVO);

    /**
     * 连接点中止后，删除连接点调用
     * 监控ip不为空
     * @param l3EndpointVO
     */
    void deleteMonitorData(L3EndpointVO l3EndpointVO);

    /**
     * 添加Agent监控路由，且开启该连接点下的监控
     * L3NetworkMonitorJob调用
     * @param vo
     */
    void addAgentRoute(L3EndpointVO vo);

    /**
     * 删除Agent监控路由，且停止该连接点下的监控
     * L3NetworkMonitorJob调用
     * @param vo
     */
    void deleteAgentRoute(L3EndpointVO vo);

    /**
     * 开启Agent ping监控线程（单个监控开启）
     * L3NetworkMonitorJob调用
     * @param endpointVO
     * @param monitorVO
     */
    void startAgentMonitor(L3EndpointVO endpointVO,L3NetworkMonitorVO monitorVO);

    /**
     * 停止Agent ping监控线程（单个监控停止）
     * L3NetworkMonitorJob调用
     * @param endpointVO
     * @param monitorVO
     */
    void stopAgentMonitor(L3EndpointVO endpointVO,L3NetworkMonitorVO monitorVO);

    /**
     * 更新Agent ping监控线程（批量更新）
     * 处理前端传入的对端监控数据
     * 前端监控IP不为空，后端监控IP不为空，且前端监控IP = 后端监控IP
     * L3NetworkMonitorJob调用
     * @param endpointVO
     * @param monitorVO
     */
    void updateAgentMonitor(L3EndpointVO endpointVO,L3NetworkMonitorVO monitorVO);

    /**
     * 开启控制器监控
     * 前端监控IP不为空，后端监控IP为空
     * @param vo
     */
    void startControllerMonitor(L3EndpointVO vo);

    /**
     * 停止控制器监控
     * 前端监控IP为空，后端监控IP不为空
     * @param vo
     */
    void stopControllerMonitor(L3EndpointVO vo);
}
