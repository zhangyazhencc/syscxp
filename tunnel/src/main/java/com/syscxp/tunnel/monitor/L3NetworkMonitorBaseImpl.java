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
import com.syscxp.header.tunnel.network.L3EndpointState;
import com.syscxp.header.tunnel.network.L3EndpointVO;
import com.syscxp.header.tunnel.network.L3EndpointVO_;
import com.syscxp.header.tunnel.switchs.PhysicalSwitchVO;
import com.syscxp.header.tunnel.switchs.SwitchVO;
import com.syscxp.header.tunnel.switchs.SwitchVO_;
import com.syscxp.tunnel.sdnController.ControllerCommands;
import com.syscxp.tunnel.sdnController.ControllerRestConstant;
import com.syscxp.tunnel.tunnel.job.MonitorJobType;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
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
    @Autowired
    private RESTFacade restf;

    @Override
    @Transactional
    public void startMonitor(L3EndpointVO l3EndpointVO, List<L3NetworkMonitorVO> srcL3NetworkMonitorVOS) {
        // 配置监控机路由job(同步)
        addAgentRoute(l3EndpointVO);

        // 开启控制器监控job
        createL3EndpointJob(MonitorJobType.CONTROLLER_START, l3EndpointVO, "L3-开启控机制监控");

        // 开启监控机监控job
        for (L3NetworkMonitorVO srcVO : srcL3NetworkMonitorVOS) {
            dbf.getEntityManager().persist(srcVO);
            createL3NetworkMonitorJob(MonitorJobType.AGENT_START, srcVO, "L3-开启监控机监控" + srcVO.getUuid());
        }
    }

    @Override
    public void startMonitor(L3EndpointVO l3EndpointVO) {
        if (l3EndpointVO.getState() == L3EndpointState.Enabled &&
                StringUtils.isNotEmpty(l3EndpointVO.getMonitorIp())) {

            // 配置监控机路由job(同步)
            addAgentRoute(l3EndpointVO);

            // 开启控制器监控job
            createL3EndpointJob(MonitorJobType.CONTROLLER_START, l3EndpointVO, "L3-开启控机制监控");

            // 开启监控机监控
            List<L3NetworkMonitorVO> srcVOS = Q.New(L3NetworkMonitorVO.class)
                    .eq(L3NetworkMonitorVO_.l3NetworkUuid, l3EndpointVO.getL3NetworkUuid())
                    .eq(L3NetworkMonitorVO_.srcL3EndpointUuid, l3EndpointVO.getEndpointUuid())
                    .list();
            for (L3NetworkMonitorVO srcVO : srcVOS)
                createL3NetworkMonitorJob(MonitorJobType.AGENT_START, srcVO, "L3-开启监控机监控" + srcVO.getUuid());

            List<L3NetworkMonitorVO> dstVOS = Q.New(L3NetworkMonitorVO.class)
                    .eq(L3NetworkMonitorVO_.l3NetworkUuid, l3EndpointVO.getL3NetworkUuid())
                    .eq(L3NetworkMonitorVO_.dstL3EndpointUuid, l3EndpointVO.getEndpointUuid())
                    .list();
            for (L3NetworkMonitorVO dstVO : dstVOS)
                createL3NetworkMonitorJob(MonitorJobType.AGENT_START, dstVO, "L3-开启监控机监控" + dstVO.getUuid());
        }
    }

    @Override
    @Transactional
    public void stopMonitor(L3EndpointVO l3EndpointVO) {
        // 关闭控制器监控
        createL3EndpointJob(MonitorJobType.CONTROLLER_STOP, l3EndpointVO, "L3-关闭控机制监控");

        // 删除监控机路由
        createL3EndpointJob(MonitorJobType.AGENT_DELETE_ROUTE, l3EndpointVO, "L3-删除监控机路由");

        // 关闭监控机监控
        List<L3NetworkMonitorVO> srcVOS = Q.New(L3NetworkMonitorVO.class)
                .eq(L3NetworkMonitorVO_.l3NetworkUuid, l3EndpointVO.getL3NetworkUuid())
                .eq(L3NetworkMonitorVO_.dstL3EndpointUuid, l3EndpointVO.getUuid())
                .list();
        for (L3NetworkMonitorVO srcVO : srcVOS) {
            dbf.getEntityManager().remove(srcVO);
            createL3NetworkMonitorJob(MonitorJobType.AGENT_STOP, srcVO, "L3-关闭监控机监控" + srcVO.getUuid());
        }

        List<L3NetworkMonitorVO> dstVOS = Q.New(L3NetworkMonitorVO.class)
                .eq(L3NetworkMonitorVO_.dstL3EndpointUuid, l3EndpointVO.getUuid())
                .eq(L3NetworkMonitorVO_.l3NetworkUuid, l3EndpointVO.getL3NetworkUuid())
                .list();
        for (L3NetworkMonitorVO dstVO : dstVOS) {
            dbf.getEntityManager().remove(dstVO);
            createL3NetworkMonitorJob(MonitorJobType.AGENT_STOP, dstVO, "L3-关闭监控机监控" + dstVO.getUuid());
        }

    }

    @Override
    @Transactional
    public void stopMonitorByDisableL3Endpoint(L3EndpointVO l3EndpointVO) {
        // 关闭控制器监控
        createL3EndpointJob(MonitorJobType.CONTROLLER_STOP, l3EndpointVO, "L3-关闭控机制监控");

        // 删除监控机路由
        createL3EndpointJob(MonitorJobType.AGENT_DELETE_ROUTE, l3EndpointVO, "L3-删除监控机路由");

        // 关闭监控机监控
        List<L3NetworkMonitorVO> srcVOS = Q.New(L3NetworkMonitorVO.class)
                .eq(L3NetworkMonitorVO_.dstL3EndpointUuid, l3EndpointVO.getUuid())
                .list();
        for (L3NetworkMonitorVO srcVO : srcVOS)
            createL3NetworkMonitorJob(MonitorJobType.AGENT_STOP, srcVO, "L3-关闭监控机监控" + srcVO.getUuid());

        List<L3NetworkMonitorVO> dstVOS = Q.New(L3NetworkMonitorVO.class)
                .eq(L3NetworkMonitorVO_.dstL3EndpointUuid, l3EndpointVO.getUuid())
                .list();
        for (L3NetworkMonitorVO dstVO : dstVOS)
            createL3NetworkMonitorJob(MonitorJobType.AGENT_STOP, dstVO, "L3-关闭监控机监控" + dstVO.getUuid());
    }

    @Override
    @Transactional
    public void deleteMonitorData(L3EndpointVO l3EndpointVO) {
        if (l3EndpointVO != null && StringUtils.isNotEmpty(l3EndpointVO.getMonitorIp())) {
            List<L3NetworkMonitorVO> srcVOS = Q.New(L3NetworkMonitorVO.class)
                    .eq(L3NetworkMonitorVO_.srcL3EndpointUuid, l3EndpointVO.getEndpointUuid())
                    .list();
            if (!srcVOS.isEmpty())
                dbf.getEntityManager().remove(srcVOS);

            List<L3NetworkMonitorVO> dstVOS = Q.New(L3NetworkMonitorVO.class)
                    .eq(L3NetworkMonitorVO_.dstL3EndpointUuid, l3EndpointVO.getEndpointUuid())
                    .list();
            if (!dstVOS.isEmpty())
                dbf.getEntityManager().remove(dstVOS);
        }
    }

    @Override
    @Transactional
    public void updateMonitorVO(L3EndpointVO l3EndpointVO, List<L3NetworkMonitorVO> srcL3NetworkMonitorVOS) {
        // 处理已存在的监控数据
        List<L3NetworkMonitorVO> oldVOS = Q.New(L3NetworkMonitorVO.class)
                .eq(L3NetworkMonitorVO_.l3NetworkUuid, l3EndpointVO.getL3NetworkUuid())
                .eq(L3NetworkMonitorVO_.srcL3EndpointUuid, l3EndpointVO.getEndpointUuid())
                .list();
        for (L3NetworkMonitorVO oldVO : oldVOS) {
            boolean isExist = false;
            for (L3NetworkMonitorVO newVO : srcL3NetworkMonitorVOS) {
                if (!isExist) {
                    if (oldVO.getL3NetworkUuid().equals(newVO.getUuid())
                            && oldVO.getSrcL3EndpointUuid().equals(newVO.getSrcL3EndpointUuid())
                            && oldVO.getDstL3EndpointUuid().equals(newVO.getDstL3EndpointUuid())) {
                        srcL3NetworkMonitorVOS.remove(newVO);
                        isExist = true;
                    }
                }
            }

            if (!isExist) {
                dbf.getEntityManager().remove(oldVO);
                createL3NetworkMonitorJob(MonitorJobType.AGENT_STOP, oldVO, "L3-关闭监控机监控" + oldVO.getUuid());
            }
        }

        // 处理前端监控数据
        for (L3NetworkMonitorVO newVO : srcL3NetworkMonitorVOS) {
            dbf.getEntityManager().persist(newVO);
            createL3NetworkMonitorJob(MonitorJobType.AGENT_START, newVO, "L3-开启监控机监控" + newVO.getUuid());
        }
    }

    @Override
    public void updateMonitorIp(L3EndpointVO l3EndpointVO, List<L3NetworkMonitorVO> srcL3NetworkMonitorVOS) {
        // 修改本端命名空间ip
        updateAgentRoute(l3EndpointVO);

        updateMonitorVO(l3EndpointVO, srcL3NetworkMonitorVOS);

        // 处理对端监控数据
        List<L3NetworkMonitorVO> dstVOS = Q.New(L3NetworkMonitorVO.class)
                .eq(L3NetworkMonitorVO_.l3NetworkUuid, l3EndpointVO.getL3NetworkUuid())
                .eq(L3NetworkMonitorVO_.dstL3EndpointUuid, l3EndpointVO.getEndpointUuid())
                .list();
        for (L3NetworkMonitorVO dstVO : dstVOS)
            createL3NetworkMonitorJob(MonitorJobType.AGENT_MODIFY, dstVO, "L3-修改监控机IP" + dstVO.getUuid());
    }

    private void createL3NetworkMonitorJob(MonitorJobType jobType, L3NetworkMonitorVO vo, String queueName) {
        L3NetworkMonitorJob monitorJob = new L3NetworkMonitorJob();

        monitorJob.setL3NetworkMonitorVO(vo);
        monitorJob.setJobType(jobType);
        jobf.execute(queueName, Platform.getManagementServerId(), monitorJob);
    }

    private void createL3EndpointJob(MonitorJobType jobType, L3EndpointVO vo, String queueName) {
        L3NetworkMonitorJob monitorJob = new L3NetworkMonitorJob();

        monitorJob.setL3EndpointVO(vo);
        monitorJob.setJobType(jobType);
        jobf.execute(queueName, Platform.getManagementServerId(), monitorJob);
    }

    @Override
    public void addAgentRoute(L3EndpointVO vo) {
        MonitorAgentCommands.L3RouteCommand cmd = getAgentRouteCommand(vo);
        String hostIp = getHostIp(vo.getPhysicalSwitchUuid());

        MonitorAgentBaseImpl.AgentResponse resp = monitorAgent.httpCall(
                hostIp, MonitorAgentConstant.L3_ADD_ROUTE, JSONObject.toJSONString(cmd), MonitorAgentBaseImpl.AgentResponse.class);

        if (!resp.isSuccess())
            throw new RuntimeException(String.format("[ADD AGENT ROUTE FAILED] HostIp: %s , Params: %s , Method: %s, Error: %s"
                    , hostIp, cmd, MonitorAgentConstant.L3_ADD_ROUTE, resp.getMsg()));
    }

    @Override
    public void deleteAgentRoute(L3EndpointVO vo) {
        MonitorAgentCommands.L3RouteCommand cmd = getAgentRouteCommand(vo);
        String hostIp = getHostIp(vo.getPhysicalSwitchUuid());

        MonitorAgentBaseImpl.AgentResponse resp = monitorAgent.httpCall(
                hostIp, MonitorAgentConstant.L3_DELETE_ROUTE, JSONObject.toJSONString(cmd), MonitorAgentBaseImpl.AgentResponse.class);

        if (!resp.isSuccess())
            throw new RuntimeException(String.format("[DELETE AGENT ROUTE FAILED] HostIp: %s , Params: %s , Method: %s, Error: %s"
                    , hostIp, cmd, MonitorAgentConstant.L3_DELETE_ROUTE, resp.getMsg()));
    }

    // 修改监控ip
    public void updateAgentRoute(L3EndpointVO vo) {
        MonitorAgentCommands.L3RouteCommand cmd = getAgentRouteCommand(vo);
        String hostIp = getHostIp(vo.getPhysicalSwitchUuid());

        MonitorAgentBaseImpl.AgentResponse resp = monitorAgent.httpCall(
                hostIp, MonitorAgentConstant.L3_UPDATE_ROUTE, JSONObject.toJSONString(cmd), MonitorAgentBaseImpl.AgentResponse.class);

        if (!resp.isSuccess())
            throw new RuntimeException(String.format("[MODIFY AGENT ROUTE FAILED] HostIp: %s , Params: %s , Method: %s, Error: %s"
                    , hostIp, cmd, MonitorAgentConstant.L3_UPDATE_ROUTE, resp.getMsg()));
    }

    private MonitorAgentCommands.L3RouteCommand getAgentRouteCommand(L3EndpointVO l3EndpointVO) {
        PhysicalSwitchVO physicalSwitchVO = dbf.findByUuid(l3EndpointVO.getPhysicalSwitchUuid(),PhysicalSwitchVO.class);

        MonitorAgentCommands.L3RouteCommand cmd = new MonitorAgentCommands.L3RouteCommand();
        cmd.setL3endpoint_id(l3EndpointVO.getUuid());
        cmd.setLocal_ip(l3EndpointVO.getLocalIP());
        cmd.setMonitor_ip(l3EndpointVO.getMonitorIp());
        cmd.setVlan(l3EndpointVO.getVlan());
        HostSwitchMonitorVO hostSwitchMonitorVO = getHostSwitchMonitorVO(physicalSwitchVO.getUuid());
        cmd.setInterface_name(hostSwitchMonitorVO.getInterfaceName());

        return cmd;
    }

    @Override
    public void startAgentMonitor(L3NetworkMonitorVO vo) {
        L3EndpointVO l3EndpointVO = dbf.findByUuid(vo.getSrcL3EndpointUuid(), L3EndpointVO.class);
        MonitorAgentCommands.L3MonitorCommand cmd = getAgentMonitorCommand(l3EndpointVO, vo);
        String hostIp = getHostIp(l3EndpointVO.getPhysicalSwitchUuid());

        MonitorAgentBaseImpl.AgentResponse resp = monitorAgent.httpCall(
                hostIp, MonitorAgentConstant.L3_START_MONITOR, JSONObject.toJSONString(cmd), MonitorAgentBaseImpl.AgentResponse.class);

        if (!resp.isSuccess())
            throw new RuntimeException(String.format("[START AGENT MONITOR FAILED] HostIp: %s , Params: %s , Method: %s, Error: %s"
                    , hostIp, cmd, MonitorAgentConstant.L3_START_MONITOR, resp.getMsg()));
    }

    @Override
    public void stopAgentMonitor(L3NetworkMonitorVO vo) {
        L3EndpointVO l3EndpointVO = dbf.findByUuid(vo.getSrcL3EndpointUuid(), L3EndpointVO.class);
        String hostIp = getHostIp(l3EndpointVO.getPhysicalSwitchUuid());

        MonitorAgentCommands.L3EndpointBase cmd = new MonitorAgentCommands.L3EndpointBase();
        cmd.setL3endpoint_id(l3EndpointVO.getUuid());

        MonitorAgentBaseImpl.AgentResponse resp = monitorAgent.httpCall(
                hostIp, MonitorAgentConstant.L3_STOP_MONITOR, JSONObject.toJSONString(cmd), MonitorAgentBaseImpl.AgentResponse.class);

        if (!resp.isSuccess())
            throw new RuntimeException(String.format("[STOP AGENT MONITOR FAILED] HostIp: %s , Params: %s , Method: %s, Error: %s"
                    , hostIp, cmd, MonitorAgentConstant.L3_STOP_MONITOR, resp.getMsg()));
    }

    @Override
    public void updateAgentMonitor(L3NetworkMonitorVO vo) {
        L3EndpointVO l3EndpointVO = dbf.findByUuid(vo.getSrcL3EndpointUuid(), L3EndpointVO.class);
        MonitorAgentCommands.L3MonitorCommand cmd = getAgentMonitorCommand(l3EndpointVO, vo);
        String hostIp = getHostIp(l3EndpointVO.getPhysicalSwitchUuid());

        MonitorAgentBaseImpl.AgentResponse resp = monitorAgent.httpCall(
                hostIp, MonitorAgentConstant.L3_UPDATE_MONITOR, JSONObject.toJSONString(cmd), MonitorAgentBaseImpl.AgentResponse.class);

        if (!resp.isSuccess())
            throw new RuntimeException(String.format("[UPDATE AGENT MONITOR FAILED] HostIp: %s , Params: %s , Method: %s, Error: %s"
                    , hostIp, cmd, MonitorAgentConstant.L3_UPDATE_MONITOR, resp.getMsg()));
    }

    private MonitorAgentCommands.L3MonitorCommand getAgentMonitorCommand(L3EndpointVO l3EndpointVO, L3NetworkMonitorVO l3NetworkMonitorVO) {
        MonitorAgentCommands.L3MonitorCommand cmd = new MonitorAgentCommands.L3MonitorCommand();
        cmd.setSrc_l3endpoint_id(l3NetworkMonitorVO.getSrcL3EndpointUuid());
        cmd.setDst_l3endpoint_id(l3NetworkMonitorVO.getDstL3EndpointUuid());
        cmd.setMonitor_ip(l3EndpointVO.getMonitorIp());

        return cmd;
    }

    @Override
    public void startControllerMonitor(L3EndpointVO vo) {
        ControllerCommands.L3MonitorCommand cmd = getL3ControllerCommand(vo);

        RyuControllerBaseImpl.RyuNetworkResponse resp = ryuController.httpCall(
                ControllerRestConstant.START_L3_MONITOR, JSONObject.toJSONString(cmd), RyuControllerBaseImpl.RyuNetworkResponse.class);

        if (!resp.isSuccess())
            throw new RuntimeException(String.format("[START CONTROLLER AGENT ERROR] , Params: %s , Method: %s, Error: %s"
                    , cmd, ControllerRestConstant.START_L3_MONITOR, resp.getMsg()));
    }

    @Override
    public void stopControllerMonitor(L3EndpointVO vo) {
        ControllerCommands.L3MonitorCommand cmd = getL3ControllerCommand(vo);

        RyuControllerBaseImpl.RyuNetworkResponse resp = ryuController.httpCall(
                ControllerRestConstant.STOP_L3_MONITOR, JSONObject.toJSONString(cmd), RyuControllerBaseImpl.RyuNetworkResponse.class);

        if (!resp.isSuccess())
            throw new RuntimeException(String.format("[STOP CONTROLLER AGENT ERROR] , Params: %s , Method: %s, Error: %s"
                    , cmd, ControllerRestConstant.STOP_L3_MONITOR, resp.getMsg()));
    }

    private ControllerCommands.L3MonitorCommand getL3ControllerCommand(L3EndpointVO l3EndpointVO) {
        PhysicalSwitchVO physicalSwitchVO = dbf.findByUuid(l3EndpointVO.getPhysicalSwitchUuid(),PhysicalSwitchVO.class);

        ControllerCommands.L3MonitorMpls l3Mpls = new ControllerCommands.L3MonitorMpls();
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

        List<ControllerCommands.L3MonitorMpls> l3MonitorMplsList = new ArrayList<>();
        l3MonitorMplsList.add(l3Mpls);

        ControllerCommands.L3MonitorCommand cmd = new ControllerCommands.L3MonitorCommand();
        cmd.setNet_id(l3EndpointVO.getL3NetworkUuid());
        cmd.setMpls_switches(l3MonitorMplsList);

        return cmd;
    }

    /***
     * 按连接点查询物理交换机
     * @param endpointUuid
     * @return
     */
    private PhysicalSwitchVO getPhysicalSwitchVO(String endpointUuid) {
        PhysicalSwitchVO physicalSwitchVO;

        List<SwitchVO> switchVOS = Q.New(SwitchVO.class)
                .eq(SwitchVO_.endpointUuid, endpointUuid)
                .list();
        if (!switchVOS.isEmpty()) {
            physicalSwitchVO = dbf.findByUuid(switchVOS.get(0).getPhysicalSwitchUuid(), PhysicalSwitchVO.class);
        } else
            throw new RuntimeException(String.format("[getPhysicalSwitchUuid] failed to get SwitchVO by endpointUuid %s",
                    endpointUuid));

        if (physicalSwitchVO == null)
            throw new RuntimeException(String.format("[getPhysicalSwitchUuid] failed to get PhysicalSwitchVO by uuid %s",
                    switchVOS.get(0).getPhysicalSwitchUuid()));

        return physicalSwitchVO;
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

    private HostSwitchMonitorVO getHostSwitchMonitorByHostIp(String hostIp) {
        List<HostSwitchMonitorVO> hostSwitchMonitorVOS;

        List<HostVO> hostVOS = Q.New(HostVO.class).eq(HostVO_.hostIp, hostIp).list();
        if (!hostVOS.isEmpty()) {
            hostSwitchMonitorVOS = Q.New(HostSwitchMonitorVO.class)
                    .eq(HostSwitchMonitorVO_.hostUuid, hostVOS.get(0).getUuid())
                    .list();
        } else
            throw new IllegalArgumentException(String.format("No HostVO with hostIp %s", hostIp));

        if (hostSwitchMonitorVOS.isEmpty())
            throw new IllegalArgumentException(String.format("No HostSwitchMonitorVO with hostIp %s", hostIp));

        return hostSwitchMonitorVOS.get(0);
    }

    private L3EndpointVO getL3EndpointByPhysicalSwitchUuid(String physicalSwitchUuid) {
        List<L3EndpointVO> l3EndpointVOS = new ArrayList<>();

        List<SwitchVO> switchVOS = Q.New(SwitchVO.class)
                .eq(SwitchVO_.physicalSwitchUuid, physicalSwitchUuid)
                .list();

        if (!switchVOS.isEmpty()) {
            l3EndpointVOS = Q.New(L3EndpointVO.class)
                    .eq(L3EndpointVO_.endpointUuid, switchVOS.get(0).getEndpointUuid())
                    .notNull(L3EndpointVO_.monitorIp)
                    .notEq(L3EndpointVO_.monitorIp, StringUtils.EMPTY)
                    .list();
        } else
            logger.info(String.format("failed to get SwitchVO by physicalSwitchUuid %s", physicalSwitchUuid));

        if (l3EndpointVOS.isEmpty())
            return null;
        else
            return l3EndpointVOS.get(0);
    }

    @Override
    public boolean start() {
        restf.registerSyncHttpCallHandler("MONITOR/L3INFO", HashMap.class,
                paramMap -> {
                    MonitorAgentCommands.L3SyncResponse response = new MonitorAgentCommands.L3SyncResponse();

                    String hostIp = paramMap.get("monitorHostIp").toString();
                    String msg = "";
                    try {
                        HostSwitchMonitorVO hostSwitchMonitor;
                        L3EndpointVO l3EndpointVO;
                        try {
                            hostSwitchMonitor = getHostSwitchMonitorByHostIp(hostIp);
                            l3EndpointVO = getL3EndpointByPhysicalSwitchUuid(hostSwitchMonitor.getPhysicalSwitchUuid());
                        } catch (Exception e) {
                            throw new IllegalArgumentException(String.format("no data exist! %s", e.getMessage()));
                        }

                        MonitorAgentCommands.L3RouteCommand cmd = getAgentRouteCommand(l3EndpointVO);
                        response.setL3_route(cmd);

                        List<MonitorAgentCommands.L3MonitorCommand> l3MonitorCmds = new ArrayList<>();
                        List<L3NetworkMonitorVO> l3MonitorVOS = Q.New(L3NetworkMonitorVO.class)
                                .eq(L3NetworkMonitorVO_.srcL3EndpointUuid, l3EndpointVO.getEndpointUuid())
                                .list();
                        for (L3NetworkMonitorVO l3MonitorVO : l3MonitorVOS)
                            l3MonitorCmds.add(getAgentMonitorCommand(l3EndpointVO, l3MonitorVO));
                        response.setL3_endpints(l3MonitorCmds);

                        response.setSuccess(true);
                        response.setMsg("success");
                    } catch (Exception e) {
                        response.setSuccess(false);
                        response.setMsg(String.format("[MONITOR/L3INFO] L3 monitor sync failed! " +
                                "Agent Host IP:  %s, Error: %s", hostIp, e.getMessage()));
                        logger.error(response.getMsg());
                    }

                    return JSONObjectUtil.toJsonString(response);
                });

        return true;
    }

    @Override
    public boolean stop() {
        return false;
    }

}
