package com.syscxp.tunnel.monitor;

import com.alibaba.fastjson.JSONObject;
import com.syscxp.core.Platform;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.job.JobQueueFacade;
import com.syscxp.header.Component;
import com.syscxp.header.host.HostVO;
import com.syscxp.header.host.HostVO_;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.tunnel.host.HostSwitchMonitorVO;
import com.syscxp.header.tunnel.host.HostSwitchMonitorVO_;
import com.syscxp.header.tunnel.monitor.*;
import com.syscxp.header.tunnel.network.L3EndpointVO;
import com.syscxp.header.tunnel.network.L3EndpointVO_;
import com.syscxp.header.tunnel.switchs.PhysicalSwitchVO;
import com.syscxp.tunnel.sdnController.ControllerCommands;
import com.syscxp.tunnel.sdnController.ControllerRestConstant;
import com.syscxp.tunnel.tunnel.job.MonitorJobType;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private RESTFacade restf;

    /***
     * 前端开启监控
     * @param l3EndpointVO
     * @param srcL3NetworkMonitorVOS：页面选择的监控数据
     */
    @Override
    public void startMonitor(L3EndpointVO l3EndpointVO, List<L3NetworkMonitorVO> srcL3NetworkMonitorVOS) {

        // 开启监控机监控job
        for (L3NetworkMonitorVO monitorVO : srcL3NetworkMonitorVOS)
            dbf.persist(monitorVO);

        // 配置监控机路由job
        createRouteJob(MonitorJobType.AGENT_ADD_ROUTE, l3EndpointVO, "L3-添加监控机路由");

        // 开启控制器监控job
        createRouteJob(MonitorJobType.CONTROLLER_START, l3EndpointVO, "L3-开启控机制监控");
    }

    /***
     * job开启监控
     * @param l3EndpointVO
     */
    @Override
    public void startMonitor(L3EndpointVO l3EndpointVO) {

        // 配置监控机路由job
        createRouteJob(MonitorJobType.AGENT_ADD_ROUTE, l3EndpointVO, "L3-添加监控机路由");

        // 开启控制器监控job
        createRouteJob(MonitorJobType.CONTROLLER_START, l3EndpointVO, "L3-开启控机制监控");

        // 开启监控机监控
        // 开启监控机监控添加路由时，已开启监控机本端的监控)
        List<L3NetworkMonitorVO> dstVOS = Q.New(L3NetworkMonitorVO.class)
                .eq(L3NetworkMonitorVO_.l3NetworkUuid, l3EndpointVO.getL3NetworkUuid())
                .eq(L3NetworkMonitorVO_.dstL3EndpointUuid, l3EndpointVO.getEndpointUuid())
                .list();
        for (L3NetworkMonitorVO dstVO : dstVOS)
            createMonitorJob(MonitorJobType.AGENT_START, l3EndpointVO, dstVO, "L3-开启监控机监控" + dstVO.getUuid());

    }

    @Override
    public void stopMonitor(L3EndpointVO l3EndpointVO) {
        // 关闭控制器监控
        createRouteJob(MonitorJobType.CONTROLLER_STOP, l3EndpointVO, "L3-关闭控机制监控");

        // 删除监控机路由
        createRouteJob(MonitorJobType.AGENT_DELETE_ROUTE, l3EndpointVO, "L3-删除监控机路由");

        // 关闭监控机监控
        // 仅关闭对端监控，删除路由时，已关闭监控机本端的监控)
        List<L3NetworkMonitorVO> srcVOS = Q.New(L3NetworkMonitorVO.class)
                .eq(L3NetworkMonitorVO_.dstL3EndpointUuid, l3EndpointVO.getUuid())
                .list();
        for (L3NetworkMonitorVO srcVO : srcVOS)
            dbf.remove(srcVO);

        List<L3NetworkMonitorVO> dstVOS = Q.New(L3NetworkMonitorVO.class)
                .eq(L3NetworkMonitorVO_.dstL3EndpointUuid, l3EndpointVO.getUuid())
                .eq(L3NetworkMonitorVO_.l3NetworkUuid, l3EndpointVO.getL3NetworkUuid())
                .list();
        for (L3NetworkMonitorVO dstVO : dstVOS) {
            dbf.remove(dstVO);
            createMonitorJob(MonitorJobType.AGENT_STOP, l3EndpointVO, dstVO, "L3-关闭监控机监控" + dstVO.getUuid());
        }

    }

    @Override
    public void stopMonitorByDisableL3Endpoint(L3EndpointVO l3EndpointVO) {

        // 删除监控机路由
        createRouteJob(MonitorJobType.AGENT_DELETE_ROUTE, l3EndpointVO, "L3-删除监控机路由");

        // 关闭控制器监控
        createRouteJob(MonitorJobType.CONTROLLER_STOP, l3EndpointVO, "L3-关闭控机制监控");

        // 关闭监控机监控
        // 仅关闭对端监控，删除路由时，已关闭监控机本端的监控)
        List<L3NetworkMonitorVO> dstVOS = Q.New(L3NetworkMonitorVO.class)
                .eq(L3NetworkMonitorVO_.dstL3EndpointUuid, l3EndpointVO.getUuid())
                .list();
        for (L3NetworkMonitorVO dstVO : dstVOS)
            createMonitorJob(MonitorJobType.AGENT_STOP, l3EndpointVO, dstVO, "L3-关闭监控机监控" + dstVO.getUuid());
    }

    @Override
    public void deleteMonitorData(L3EndpointVO l3EndpointVO) {
        if (l3EndpointVO != null && StringUtils.isNotEmpty(l3EndpointVO.getMonitorIp())) {
            List<L3NetworkMonitorVO> srcVOS = Q.New(L3NetworkMonitorVO.class)
                    .eq(L3NetworkMonitorVO_.srcL3EndpointUuid, l3EndpointVO.getEndpointUuid())
                    .list();

            dbf.removeCollection(srcVOS, L3NetworkMonitorVO.class);

            List<L3NetworkMonitorVO> dstVOS = Q.New(L3NetworkMonitorVO.class)
                    .eq(L3NetworkMonitorVO_.dstL3EndpointUuid, l3EndpointVO.getEndpointUuid())
                    .list();
            dbf.removeCollection(dstVOS, L3NetworkMonitorVO.class);
        }
    }

    @Override
    public void updateSrcMonitor(L3EndpointVO l3EndpointVO, List<L3NetworkMonitorVO> srcL3NetworkMonitorVOS) {
        List<L3NetworkMonitorVO> l3NetworkMonitorVOS = Q.New(L3NetworkMonitorVO.class)
                .eq(L3NetworkMonitorVO_.srcL3EndpointUuid, l3EndpointVO.getUuid())
                .list();
        dbf.removeCollection(l3NetworkMonitorVOS, L3NetworkMonitorVO.class);

        dbf.persistCollection(srcL3NetworkMonitorVOS);

        for (L3NetworkMonitorVO monitorVO : srcL3NetworkMonitorVOS)
            createMonitorJob(MonitorJobType.AGENT_MODIFY, l3EndpointVO, monitorVO, "L3-修改监控数据");
    }

    @Override
    public void updateMonitorIp(L3EndpointVO l3EndpointVO, List<L3NetworkMonitorVO> srcL3NetworkMonitorVOS) {

        // 更新本端监控数据
        updateSrcMonitor(l3EndpointVO, srcL3NetworkMonitorVOS);

        // 处理对端监控数据
        List<L3NetworkMonitorVO> dstVOS = Q.New(L3NetworkMonitorVO.class)
                .eq(L3NetworkMonitorVO_.l3NetworkUuid, l3EndpointVO.getL3NetworkUuid())
                .eq(L3NetworkMonitorVO_.dstL3EndpointUuid, l3EndpointVO.getEndpointUuid())
                .list();
        for (L3NetworkMonitorVO dstMonitor : dstVOS) {
            L3EndpointVO dstEndpoint = dbf.findByUuid(dstMonitor.getSrcL3EndpointUuid(), l3EndpointVO.getClass());

            createMonitorJob(MonitorJobType.AGENT_MODIFY, dstEndpoint, dstMonitor, "L3-修改监控IP");
        }
    }

    private void createMonitorJob(MonitorJobType jobType, L3EndpointVO endpointVO, L3NetworkMonitorVO monitorVO, String queueName) {
        L3NetworkMonitorJob monitorJob = new L3NetworkMonitorJob();

        monitorJob.setL3EndpointUuid(endpointVO.getUuid());
        monitorJob.setL3NetworkMonitorUuid(monitorVO.getUuid());
        monitorJob.setJobType(jobType);
        jobf.execute(queueName, Platform.getManagementServerId(), monitorJob);
    }

    private void createRouteJob(MonitorJobType jobType, L3EndpointVO endpointVO, String queueName) {
        L3NetworkMonitorJob monitorJob = new L3NetworkMonitorJob();

        monitorJob.setL3EndpointUuid(endpointVO.getUuid());
        monitorJob.setJobType(jobType);
        jobf.execute(queueName, Platform.getManagementServerId(), monitorJob);
    }

    @Override
    public void addAgentRoute(String l3EndpointUuid) {
        L3EndpointVO end = dbf.findByUuid(l3EndpointUuid, L3EndpointVO.class);
        MonitorAgentCommands.L3AgentCommand cmd = getL3AgentCommand(end, null);
        String hostIp = getHostIp(end.getPhysicalSwitchUuid());

        MonitorAgentBaseImpl.AgentResponse resp = monitorAgent.httpCall(
                hostIp, MonitorAgentConstant.L3_ADD_ROUTE, JSONObject.toJSONString(cmd), MonitorAgentBaseImpl.AgentResponse.class);

        if (!resp.isSuccess())
            throw new RuntimeException(String.format("[ADD AGENT ROUTE FAILED] HostIp: %s , Params: %s , Method: %s, Error: %s"
                    , hostIp, cmd, MonitorAgentConstant.L3_ADD_ROUTE, resp.getMsg()));
    }

    @Override
    public void deleteAgentRoute(String l3EndpointUuid) {
        L3EndpointVO endpointVO = dbf.findByUuid(l3EndpointUuid, L3EndpointVO.class);
        MonitorAgentCommands.L3AgentCommand cmd = getL3AgentCommand(endpointVO, null);
        String hostIp = getHostIp(endpointVO.getPhysicalSwitchUuid());

        MonitorAgentBaseImpl.AgentResponse resp = monitorAgent.httpCall(
                hostIp, MonitorAgentConstant.L3_DELETE_ROUTE, JSONObject.toJSONString(cmd), MonitorAgentBaseImpl.AgentResponse.class);

        if (!resp.isSuccess())
            throw new RuntimeException(String.format("[DELETE AGENT ROUTE FAILED] HostIp: %s , Params: %s , Method: %s, Error: %s"
                    , hostIp, cmd, MonitorAgentConstant.L3_DELETE_ROUTE, resp.getMsg()));
    }

    // 修改互联IP，当前修改互联IP需要中止连接点，不需要单独调用此方法
    @Deprecated
    public void updateAgentRoute(String l3EndpointUuid) {
        L3EndpointVO endpointVO = dbf.findByUuid(l3EndpointUuid, L3EndpointVO.class);
        MonitorAgentCommands.L3AgentCommand cmd = getL3AgentCommand(endpointVO, null);
        String hostIp = getHostIp(endpointVO.getPhysicalSwitchUuid());

        MonitorAgentBaseImpl.AgentResponse resp = monitorAgent.httpCall(
                hostIp, MonitorAgentConstant.L3_UPDATE_ROUTE, JSONObject.toJSONString(cmd), MonitorAgentBaseImpl.AgentResponse.class);

        if (!resp.isSuccess())
            throw new RuntimeException(String.format("[MODIFY AGENT ROUTE FAILED] HostIp: %s , Params: %s , Method: %s, Error: %s"
                    , hostIp, cmd, MonitorAgentConstant.L3_UPDATE_ROUTE, resp.getMsg()));
    }

    @Override
    public void startAgentMonitor(String l3EndpointUuid, String L3NetworkMonitorUuid) {

        L3EndpointVO endpointVO = dbf.findByUuid(l3EndpointUuid, L3EndpointVO.class);
        L3NetworkMonitorVO monitorVO = dbf.findByUuid(L3NetworkMonitorUuid, L3NetworkMonitorVO.class);

        MonitorAgentCommands.L3AgentCommand cmd = getL3AgentCommand(endpointVO, monitorVO);
        String hostIp = getHostIp(endpointVO.getPhysicalSwitchUuid());

        MonitorAgentBaseImpl.AgentResponse resp = monitorAgent.httpCall(
                hostIp, MonitorAgentConstant.L3_START_MONITOR, JSONObject.toJSONString(cmd), MonitorAgentBaseImpl.AgentResponse.class);

        if (!resp.isSuccess())
            throw new RuntimeException(String.format("[START AGENT MONITOR FAILED] HostIp: %s , Params: %s , Method: %s, Error: %s"
                    , hostIp, cmd, MonitorAgentConstant.L3_START_MONITOR, resp.getMsg()));
    }

    @Override
    public void stopAgentMonitor(String l3EndpointUuid, String L3NetworkMonitorUuid) {

        L3EndpointVO endpointVO = dbf.findByUuid(l3EndpointUuid, L3EndpointVO.class);
        L3NetworkMonitorVO monitorVO = dbf.findByUuid(L3NetworkMonitorUuid, L3NetworkMonitorVO.class);

        MonitorAgentCommands.L3AgentCommand cmd = getL3AgentCommand(endpointVO, monitorVO);
        String hostIp = getHostIp(endpointVO.getPhysicalSwitchUuid());

        MonitorAgentBaseImpl.AgentResponse resp = monitorAgent.httpCall(
                hostIp, MonitorAgentConstant.L3_STOP_MONITOR, JSONObject.toJSONString(cmd), MonitorAgentBaseImpl.AgentResponse.class);

        if (!resp.isSuccess())
            throw new RuntimeException(String.format("[STOP AGENT MONITOR FAILED] HostIp: %s , Params: %s , Method: %s, Error: %s"
                    , hostIp, cmd, MonitorAgentConstant.L3_STOP_MONITOR, resp.getMsg()));
    }

    @Override
    public void updateAgentMonitor(String l3EndpointUuid, String L3NetworkMonitorUuid) {

        L3EndpointVO endpointVO = dbf.findByUuid(l3EndpointUuid, L3EndpointVO.class);
        L3NetworkMonitorVO monitorVO = dbf.findByUuid(L3NetworkMonitorUuid, L3NetworkMonitorVO.class);

        MonitorAgentCommands.L3AgentCommand cmd = getL3AgentCommand(endpointVO, monitorVO);
        String hostIp = getHostIp(endpointVO.getPhysicalSwitchUuid());

        MonitorAgentBaseImpl.AgentResponse resp = monitorAgent.httpCall(
                hostIp, MonitorAgentConstant.L3_UPDATE_MONITOR, JSONObject.toJSONString(cmd), MonitorAgentBaseImpl.AgentResponse.class);

        if (!resp.isSuccess())
            throw new RuntimeException(String.format("[UPDATE AGENT MONITOR FAILED] HostIp: %s , Params: %s , Method: %s, Error: %s"
                    , hostIp, cmd, MonitorAgentConstant.L3_UPDATE_MONITOR, resp.getMsg()));
    }

    /**
     * 发送agent参数获取方法
     *
     * @param endpointVO
     * @param monitorVO：如果为空，则查找连接点下所有的L3NetworkMonitorVO数据
     * @return
     */
    private MonitorAgentCommands.L3AgentCommand getL3AgentCommand(L3EndpointVO endpointVO, L3NetworkMonitorVO monitorVO) {
        MonitorAgentCommands.L3AgentCommand cmd = new MonitorAgentCommands.L3AgentCommand();

        // route
        cmd.setL3endpoint_id(endpointVO.getUuid());
        cmd.setVlan(endpointVO.getVlan());

        String mask = "/" + StringUtils.substringAfterLast(endpointVO.getIpCidr(), "/");
        cmd.setLocal_ip(endpointVO.getLocalIP() + mask);
        cmd.setMonitor_ip(endpointVO.getMonitorIp() + mask);

        PhysicalSwitchVO physicalSwitchVO = dbf.findByUuid(endpointVO.getPhysicalSwitchUuid(), PhysicalSwitchVO.class);
        cmd.setSwitch_ip(physicalSwitchVO.getmIP());

        HostSwitchMonitorVO hostSwitchMonitorVO = getHostSwitchMonitorVO(physicalSwitchVO.getUuid());
        cmd.setInterface_name(hostSwitchMonitorVO.getInterfaceName());

        // monitor
        List<MonitorAgentCommands.L3MonitorCommand> monitorCommands = new ArrayList<>();
        if (monitorVO != null) {
            monitorCommands.add(getMonitorCommand(monitorVO));
        } else {
            List<L3NetworkMonitorVO> srcMonitorVOS = Q.New(L3NetworkMonitorVO.class)
                    .eq(L3NetworkMonitorVO_.srcL3EndpointUuid, endpointVO.getUuid())
                    .list();
            for (L3NetworkMonitorVO srcMonitorVO : srcMonitorVOS) {
                monitorCommands.add(getMonitorCommand(srcMonitorVO));
            }
        }
        cmd.setMonitors(monitorCommands);

        return cmd;
    }

    private MonitorAgentCommands.L3MonitorCommand getMonitorCommand(L3NetworkMonitorVO l3NetworkMonitorVO) {
        MonitorAgentCommands.L3MonitorCommand monitorCmd = new MonitorAgentCommands.L3MonitorCommand();
        monitorCmd.setSrc_l3endpoint_id(l3NetworkMonitorVO.getSrcL3EndpointUuid());
        monitorCmd.setDst_l3endpoint_id(l3NetworkMonitorVO.getDstL3EndpointUuid());
        L3EndpointVO dstL3EndpointVO = dbf.findByUuid(l3NetworkMonitorVO.getDstL3EndpointUuid(), L3EndpointVO.class);
        monitorCmd.setMonitor_ip(dstL3EndpointVO.getMonitorIp());

        return monitorCmd;
    }

    @Override
    public void startControllerMonitor(String l3EndpointUuid) {

        L3EndpointVO endpointVO = dbf.findByUuid(l3EndpointUuid, L3EndpointVO.class);
        ControllerCommands.L3MonitorCommand cmd = getControllerCommand(endpointVO);

        RyuControllerBaseImpl.RyuNetworkResponse resp = ryuController.httpCall(
                ControllerRestConstant.START_L3_MONITOR, JSONObject.toJSONString(cmd), RyuControllerBaseImpl.RyuNetworkResponse.class);

        if (!resp.isSuccess())
            throw new RuntimeException(String.format("[START CONTROLLER AGENT ERROR] , Params: %s , Method: %s, Error: %s"
                    , cmd, ControllerRestConstant.START_L3_MONITOR, resp.getMsg()));
    }

    @Override
    public void stopControllerMonitor(String l3EndpointUuid) {

        L3EndpointVO endpointVO = dbf.findByUuid(l3EndpointUuid, L3EndpointVO.class);
        ControllerCommands.L3MonitorCommand cmd = getControllerCommand(endpointVO);

        RyuControllerBaseImpl.RyuNetworkResponse resp = ryuController.httpCall(
                ControllerRestConstant.STOP_L3_MONITOR, JSONObject.toJSONString(cmd), RyuControllerBaseImpl.RyuNetworkResponse.class);

        if (!resp.isSuccess())
            throw new RuntimeException(String.format("[STOP CONTROLLER AGENT ERROR] , Params: %s , Method: %s, Error: %s"
                    , cmd, ControllerRestConstant.STOP_L3_MONITOR, resp.getMsg()));
    }

    /**
     * 控制器下发参数
     *
     * @param l3EndpointVO
     * @return
     */
    private ControllerCommands.L3MonitorCommand getControllerCommand(L3EndpointVO l3EndpointVO) {
        PhysicalSwitchVO physicalSwitchVO = dbf.findByUuid(l3EndpointVO.getPhysicalSwitchUuid(), PhysicalSwitchVO.class);

        ControllerCommands.MplsSwitchBase l3Mpls = new ControllerCommands.MplsSwitchBase();
        l3Mpls.setUuid(physicalSwitchVO.getUuid());
        l3Mpls.setSwitch_type(physicalSwitchVO.getSwitchModel().getModel());
        l3Mpls.setSub_type(physicalSwitchVO.getSwitchModel().getSubModel());
        l3Mpls.setVlan_id(l3EndpointVO.getVlan());
        l3Mpls.setM_ip(physicalSwitchVO.getmIP());
        l3Mpls.setUsername(physicalSwitchVO.getUsername());
        l3Mpls.setPassword(physicalSwitchVO.getPassword());
        l3Mpls.setProtocal(physicalSwitchVO.getProtocol());
        l3Mpls.setPort(physicalSwitchVO.getPort());

        HostSwitchMonitorVO hostSwitchMonitorVO = getHostSwitchMonitorVO(physicalSwitchVO.getUuid());
        l3Mpls.setPort_name(hostSwitchMonitorVO.getPhysicalSwitchPortName());

        List<ControllerCommands.MplsSwitchBase> l3MonitorMplsList = new ArrayList<>();
        l3MonitorMplsList.add(l3Mpls);

        ControllerCommands.L3MonitorCommand cmd = new ControllerCommands.L3MonitorCommand();
        cmd.setNet_id(l3EndpointVO.getL3NetworkUuid());
        cmd.setMpls_switches(l3MonitorMplsList);

        return cmd;
    }

    /***
     * 按物理交换机查询监控接口
     * @param physicalSwitchUuid
     * @return
     */
    private HostSwitchMonitorVO getHostSwitchMonitorVO(String physicalSwitchUuid) {
        List<HostSwitchMonitorVO> hostSwitchMonitorVOS = Q.New(HostSwitchMonitorVO.class)
                .eq(HostSwitchMonitorVO_.physicalSwitchUuid, physicalSwitchUuid)
                .list();

        if (hostSwitchMonitorVOS.isEmpty())
            throw new RuntimeException(String.format("[getHostSwitchMonitorVO] failed to get HostSwitchMonitorVO by physicalSwitchUuid %s",
                    physicalSwitchUuid));

        return hostSwitchMonitorVOS.get(0);
    }

    /***
     * 按物理交换机查询监控机ip
     * @param physicalSwitchUuid
     * @return
     */
    private String getHostIp(String physicalSwitchUuid) {
        HostSwitchMonitorVO hostSwitchMonitorVO = getHostSwitchMonitorVO(physicalSwitchUuid);
        HostVO hostVO = dbf.findByUuid(hostSwitchMonitorVO.getHostUuid(), HostVO.class);
        if (hostVO == null)
            throw new RuntimeException(String.format("[getHostIp] failed to get HostVO by uuid %s",
                    hostSwitchMonitorVO.getHostUuid()));

        return hostVO.getHostIp();
    }

    /**
     * 按监控机IP获取监控机接口数据
     *
     * @param hostIp
     * @return
     */
    private HostSwitchMonitorVO getHostSwitchMonitorByHostIp(String hostIp) {
        List<HostSwitchMonitorVO> hostSwitchMonitorVOS = new ArrayList<>();

        List<HostVO> hostVOS = Q.New(HostVO.class).eq(HostVO_.hostIp, hostIp).list();
        if (!hostVOS.isEmpty()) {
            hostSwitchMonitorVOS = Q.New(HostSwitchMonitorVO.class)
                    .eq(HostSwitchMonitorVO_.hostUuid, hostVOS.get(0).getUuid())
                    .list();
        }

        if (hostSwitchMonitorVOS.isEmpty())
            return null;
        else
            return hostSwitchMonitorVOS.get(0);
    }

    @Override
    public boolean start() {
        // 监控机定时获取所有云网络监控数据
        restf.registerSyncHttpCallHandler("MONITOR/L3ENDPOINTS", HashMap.class,
                paramMap -> {
                    Map<String, Object> map = new HashMap<>();
                    List<MonitorAgentCommands.L3AgentCommand> cmds = new ArrayList<>();

                    String hostIp = paramMap.get("monitorHostIp").toString();
                    try {
                        HostSwitchMonitorVO hostSwitch = getHostSwitchMonitorByHostIp(hostIp);

                        if (hostSwitch != null) {
                            List<L3EndpointVO> l3EndpointVOS = Q.New(L3EndpointVO.class)
                                    .eq(L3EndpointVO_.physicalSwitchUuid, hostSwitch.getPhysicalSwitchUuid())
                                    .notNull(L3EndpointVO_.monitorIp)
                                    .notEq(L3EndpointVO_.monitorIp, StringUtils.EMPTY)
                                    .list();
                            for (L3EndpointVO l3EndpointVO : l3EndpointVOS) {
                                cmds.add(getL3AgentCommand(l3EndpointVO, null));
                            }
                        }

                        map.put("data", cmds);
                        map.put("success", true);
                        map.put("msg", "success");
                    } catch (Exception e) {
                        String errMsg = String.format("[MONITOR/L3INFO] L3 monitor sync failed! " +
                                "Agent Host IP:  %s, Error: %s", hostIp, e.getMessage());

                        map.put("success", false);
                        map.put("msg", errMsg);

                        logger.error(errMsg);
                    }

                    return JSONObjectUtil.toJsonString(map);
                });

        return true;
    }

    @Override
    public boolean stop() {
        return false;
    }

}
