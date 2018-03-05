package com.syscxp.tunnel.monitor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.db.UpdateQuery;
import com.syscxp.core.job.JobQueueFacade;
import com.syscxp.core.thread.ChainTask;
import com.syscxp.core.thread.PeriodicTask;
import com.syscxp.core.thread.SyncTaskChain;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.core.workflow.FlowChainBuilder;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.core.workflow.*;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.errorcode.OperationFailureException;
import com.syscxp.header.host.*;
import com.syscxp.header.identity.SessionInventory;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.tunnel.MonitorConstant;
import com.syscxp.header.tunnel.host.*;
import com.syscxp.header.tunnel.monitor.*;
import com.syscxp.header.tunnel.switchs.*;
import com.syscxp.header.tunnel.tunnel.*;
import com.syscxp.tunnel.identity.IdentityInterceptor;
import com.syscxp.tunnel.sdnController.ControllerCommands;
import com.syscxp.tunnel.sdnController.ControllerRestConstant;
import com.syscxp.tunnel.tunnel.TunnelBase;
import com.syscxp.tunnel.tunnel.job.MonitorJobType;
import com.syscxp.tunnel.tunnel.job.TunnelMonitorJob;
import com.syscxp.utils.Utils;
import com.syscxp.utils.data.SizeUnit;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.utils.network.NetworkUtils;
import groovy.transform.TailRecursive;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import javax.persistence.StoredProcedureQuery;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.syscxp.core.Platform.argerr;
import static com.syscxp.core.Platform.err;

/**
 * Created by DCY on 2017-09-07
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
public class MonitorManagerImpl extends AbstractService implements MonitorManager, ApiMessageInterceptor, HostDeleteExtensionPoint {

    private static final CLogger logger = Utils.getLogger(MonitorManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private ThreadFacade thdf;
    @Autowired
    private RESTFacade restf;
    @Autowired
    private IdentityInterceptor identityInterceptor;
    @Autowired
    private JobQueueFacade jobf;

    @Override
    @MessageSafe
    public void handleMessage(Message msg) {
        if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            handleLocalMessage(msg);
        }
    }

    private void handleLocalMessage(Message msg) {
        bus.dealWithUnknownMessage(msg);
    }

    private void handleApiMessage(APIMessage msg) {
        if (msg instanceof APIStartTunnelMonitorMsg) {
            handle((APIStartTunnelMonitorMsg) msg);
        } else if (msg instanceof APIStopTunnelMonitorMsg) {
            handle((APIStopTunnelMonitorMsg) msg);
        } else if (msg instanceof APIRestartTunnelMonitorMsg) {
            handle((APIRestartTunnelMonitorMsg) msg);
        } else if (msg instanceof APIInitTunnelMonitorMsg) {
            handle((APIInitTunnelMonitorMsg) msg);
        } else if (msg instanceof APIInitTunnelMonitorRollbackMsg) {
            handle((APIInitTunnelMonitorRollbackMsg) msg);
        } else if (msg instanceof APIQuerySpeedTestTunnelNodeMsg) {
            handle((APIQuerySpeedTestTunnelNodeMsg) msg);
        } else if (msg instanceof APICreateSpeedRecordsMsg) {
            handle((APICreateSpeedRecordsMsg) msg);
        } else if (msg instanceof APIQuerySpeedResultMsg) {
            handle((APIQuerySpeedResultMsg) msg);
        } else if (msg instanceof APIUpdateSpeedRecordsMsg) {
            handle((APIUpdateSpeedRecordsMsg) msg);
        } else if (msg instanceof APIDeleteSpeedRecordsMsg) {
            handle((APIDeleteSpeedRecordsMsg) msg);
        } else if (msg instanceof APICreateNettoolRecordMsg) {
            handle((APICreateNettoolRecordMsg) msg);
        } else if (msg instanceof APIQueryNettoolResultMsg) {
            handle((APIQueryNettoolResultMsg) msg);
        } else if (msg instanceof APIQueryNettoolNodeMsg) {
            handle((APIQueryNettoolNodeMsg) msg);
        } else if (msg instanceof APIQueryNettoolMonitorHostMsg) {
            handle((APIQueryNettoolMonitorHostMsg) msg);
        } else if (msg instanceof APIQueryMonitorResultMsg) {
            handle((APIQueryMonitorResultMsg) msg);
        } else if (msg instanceof APIQuerySwitchPortTrafficMsg) {
            handle((APIQuerySwitchPortTrafficMsg) msg);
        } else if (msg instanceof APICreateSpeedTestTunnelMsg) {
            handle((APICreateSpeedTestTunnelMsg) msg);
        } else if (msg instanceof APIDeleteSpeedTestTunnelMsg) {
            handle((APIDeleteSpeedTestTunnelMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    @Transactional
    private void handle(APIStartTunnelMonitorMsg msg) {
        APIStartTunnelMonitorEvent event = new APIStartTunnelMonitorEvent(msg.getId());

        TunnelVO tunnelVO = dbf.findByUuid(msg.getTunnelUuid(), TunnelVO.class);
        if (tunnelVO.getState() != TunnelState.Enabled)
            throw new IllegalArgumentException(String.format("can not start monitor for %s tunnel!", tunnelVO.getState()));

        if (tunnelVO.getMonitorState() == TunnelMonitorState.Enabled)
            throw new IllegalArgumentException("tunnel monitor already started !");

        if (jobf.isExist(tunnelVO.getUuid(), TunnelMonitorJob.class))
            throw new IllegalStateException("unhandled job exists for this tunnel monitor, please try later！");

        // 初始化监控通道
        tunnelVO.setMonitorCidr(msg.getMonitorCidr());
        tunnelVO.setMonitorState(TunnelMonitorState.Enabled);
        tunnelVO.setStatus(TunnelStatus.Connected);
        dbf.getEntityManager().merge(tunnelVO);

        // 开启控制器监控
        startControllerMonitor(tunnelVO.getUuid());

        // 开启agent监控
        try {
            startAgentMonitor(tunnelVO.getUuid());
        } catch (Exception e) {
            try {
                stopControllerMonitor(msg.getTunnelUuid());
            } catch (Exception e1) {
                logger.info(String.format("start job to stop controller monitor[tunnel: %s MonitorJobType: %s]"
                        , msg.getTunnelUuid(), MonitorJobType.STOP.toString()));

                TunnelMonitorJob monitorJob = new TunnelMonitorJob();
                monitorJob.setTunnelUuid(msg.getTunnelUuid());
                monitorJob.setJobType(MonitorJobType.STOP);
                jobf.execute("关闭监控失败-关闭监控", Platform.getManagementServerId(), monitorJob);
            }

            throw new RuntimeException(String.format("failed to start agent monitor![Tunnel:%s] Error: %s",
                    tunnelVO.getName(), e.getMessage()));
        }

        event.setInventory(TunnelInventory.valueOf(tunnelVO));
        bus.publish(event);
    }

    @Transactional
    private void handle(APIStopTunnelMonitorMsg msg) {
        TunnelVO tunnelVO = dbf.findByUuid(msg.getTunnelUuid(), TunnelVO.class);

        if (tunnelVO.getMonitorState() != TunnelMonitorState.Enabled)
            throw new IllegalArgumentException(String.format("can only stop monitor for tunnels which monitor status is [%s]!"
                    , TunnelMonitorState.Enabled));

        if (jobf.isExist(tunnelVO.getUuid(), TunnelMonitorJob.class))
            throw new IllegalStateException("unhandled job exists for this tunnel monitor, please try later！");

        if (tunnelVO.getState() == TunnelState.Enabled) {
            tunnelVO.setStatus(TunnelStatus.Connected);
        }
        tunnelVO.setMonitorState(TunnelMonitorState.Disabled);
        tunnelVO.setMonitorCidr("");
        tunnelVO = dbf.getEntityManager().merge(tunnelVO);

        List<TunnelMonitorVO> tunnelMonitorVOS = Q.New(TunnelMonitorVO.class)
                .eq(TunnelMonitorVO_.tunnelUuid, msg.getTunnelUuid())
                .list();
        for (TunnelMonitorVO tunnelMonitorVO : tunnelMonitorVOS)
            dbf.getEntityManager().remove(tunnelMonitorVO);

        // 关闭agent监控，失败后报错
        try {
            stopAgentMonitor(tunnelVO.getUuid());
        } catch (Exception e) {
            throw new RuntimeException(String.format("failed to stop agent monitor[tunnel: %s] Error: %s"
                    , tunnelVO.getName(), e.getMessage()));
        }

        // job关闭控制器监控
        TunnelMonitorJob monitorJob = new TunnelMonitorJob();
        monitorJob.setTunnelUuid(msg.getTunnelUuid());
        monitorJob.setJobType(MonitorJobType.STOP);
        jobf.execute("关闭监控失败-关闭监控", Platform.getManagementServerId(), monitorJob);

        APIStopTunnelMonitorEvent event = new APIStopTunnelMonitorEvent(msg.getId());
        event.setInventory(TunnelInventory.valueOf(tunnelVO));
        bus.publish(event);
    }

    @Transactional
    private void handle(APIRestartTunnelMonitorMsg msg) {
        APIRestartTunnelMonitorEvent event = new APIRestartTunnelMonitorEvent(msg.getId());

        TunnelVO tunnelVO = dbf.findByUuid(msg.getTunnelUuid(), TunnelVO.class);
        if (tunnelVO.getState() != TunnelState.Enabled)
            throw new IllegalArgumentException(String.format("can not restart monitor for %s tunnel!", tunnelVO.getState()));

        if (tunnelVO.getMonitorState() != TunnelMonitorState.Enabled)
            throw new IllegalArgumentException(String.format("can only restart monitor for tunnels which monitor status is [%s]!"
                    , TunnelMonitorState.Enabled));

        if (jobf.isExist(tunnelVO.getUuid(), TunnelMonitorJob.class))
            throw new IllegalStateException("unhandled job exists for this tunnel monitor, please try later！");

        tunnelVO.setMonitorCidr(msg.getMonitorCidr());
        dbf.getEntityManager().merge(tunnelVO);

        try {
            updateAgentMonitor(tunnelVO.getUuid());
        } catch (Exception e) {
            throw new RuntimeException(String.format("failed to modify monitor CIDR. [tunnel: %s] Error: %s"
                    , tunnelVO.getName(), e.getMessage()));
        }

        TunnelMonitorJob monitorJob = new TunnelMonitorJob();
        monitorJob.setTunnelUuid(msg.getTunnelUuid());
        monitorJob.setJobType(MonitorJobType.MODIFY);
        jobf.execute("修改监控失败-修改监控", Platform.getManagementServerId(), monitorJob);

        event.setInventory(TunnelInventory.valueOf(tunnelVO));
        logger.info(String.format("%s reset cidr success!", tunnelVO.getName()));
        bus.publish(event);
    }

    private void handle(APIInitTunnelMonitorRollbackMsg msg) {
        List<TunnelVO> tunnelVOS = Q.New(TunnelVO.class)
                .eq(TunnelVO_.monitorState, TunnelMonitorState.Enabled)
                .list();

        //异步下发控制器（仅删除zk）
        AsyncRestTemplate restTemplate = new AsyncRestTemplate();
        String url = getControllerUrl(ControllerRestConstant.STOP_TUNNEL_MONITOR_ZK);
        Map<String, String> cmd = new HashMap<>();
        for (TunnelVO tunnelVO : tunnelVOS) {
            if (StringUtils.isNotEmpty(msg.getTunnelUuid())) {
                if (!msg.getTunnelUuid().equals(tunnelVO.getUuid()))
                    continue;
            }
            // 仅处理导入tunnel
            if (tunnelVO.getUuid().length() == 32)
                continue;

            cmd.put("tunnel_id", tunnelVO.getUuid());

            HttpHeaders headers = new HttpHeaders();
            MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
            headers.setContentType(type);
            HttpEntity<String> entity = new HttpEntity<String>(JSON.toJSONString(cmd), headers);

            ListenableFuture<ResponseEntity<String>> futureEntity =
                    restTemplate.postForEntity(url, entity, String.class);
            futureEntity.addCallback(new ListenableFutureCallback<ResponseEntity<String>>() {
                @Override
                public void onSuccess(ResponseEntity<String> entity) {
                    logger.debug(String.format("===【TunnelUuid: %s】控制器返回：%s！", tunnelVO.getUuid(), entity.getBody()));

                    if (entity.getStatusCode() == HttpStatus.OK) {
                        ControllerCommands.ControllerRestResponse response =
                                JSON.parseObject(entity.getBody(), ControllerCommands.ControllerRestResponse.class);

                        if (response.isSuccess()) {
                            // 更新状态
                            UpdateQuery.New(TunnelMonitorVO.class).eq(TunnelMonitorVO_.tunnelUuid, tunnelVO.getUuid()).delete();

                            tunnelVO.setMonitorState(TunnelMonitorState.Disabled);
                            // tunnelVO.setMonitorCidr("");
                            dbf.update(tunnelVO);

                            logger.debug(String.format("===【TunnelUuid: %s】删除ZK成功！", tunnelVO.getUuid()));
                        } else {
                            throw new RuntimeException(String.format("===【TunnelUuid: %s】删除ZK失败！Error: %s", tunnelVO.getUuid(), response.getMsg()));
                        }

                    } else {
                        throw new RuntimeException(String.format("===【TunnelUuid: %s】发送控制器失败！【StatusCode: %s】", tunnelVO.getUuid(), entity.getStatusCode()));
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    throw new RuntimeException(String.format("===【TunnelUuid: %s】删除ZK失败,Error: %s", tunnelVO.getUuid(), t.getMessage()));
                }
            });
        }

        APIInitTunnelMonitorRollbackEvent event = new APIInitTunnelMonitorRollbackEvent(msg.getId());
        bus.publish(event);
    }

    private void handle(APIInitTunnelMonitorMsg msg) {
//        List<TunnelVO> tunnelVOS = Q.New(TunnelVO.class)
//                .notNull(TunnelVO_.monitorCidr)
//                .notEq(TunnelVO_.monitorCidr, "")
//                .eq(TunnelVO_.monitorState, TunnelMonitorState.Disabled)
//                .list();
        // 初始化TunnelMonitorVO数据
//        for (TunnelVO tunnelVO : tunnelVOS) {
//            try {
//                initTunnelMonitor(tunnelVO.getUuid());
//            } catch (Exception e) {
//                throw new RuntimeException(String.format("===【TunnelUuid: %s】初始化失败,Error: %s", tunnelVO.getUuid(), e.getMessage()));
//            }
//        }
//
//        //调用存储过程修改监控ip
//        StoredProcedureQuery procUpdateMonitor = dbf.getEntityManager().createStoredProcedureQuery("proc_monitor_init_update_monitor_ip");
//        procUpdateMonitor.execute();

        List<TunnelVO> tunnelVOS = Q.New(TunnelVO.class)
                .notNull(TunnelVO_.monitorCidr)
                .notEq(TunnelVO_.monitorCidr, "")
                .eq(TunnelVO_.monitorState, TunnelMonitorState.Disabled)
                .list();

        // 异步下发控制器（仅保存zk）
        AsyncRestTemplate restTemplate = new AsyncRestTemplate();
        String url = getControllerUrl(ControllerRestConstant.START_TUNNEL_MONITOR_ZK);
        for (TunnelVO tunnelVO : tunnelVOS) {
            List<TunnelMonitorVO> tunnelMonitorVOS = Q.New(TunnelMonitorVO.class)
                    .eq(TunnelMonitorVO_.tunnelUuid, tunnelVO.getUuid())
                    .list();
            ControllerCommands.TunnelMonitorCommand cmd =
                    getControllerMonitorCommand(tunnelVO.getUuid(), false, tunnelMonitorVOS);

            HttpHeaders headers = new HttpHeaders();
            MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
            headers.setContentType(type);
            HttpEntity<String> entity = new HttpEntity<String>(JSON.toJSONString(cmd), headers);

            ListenableFuture<ResponseEntity<String>> futureEntity =
                    restTemplate.postForEntity(url, entity, String.class);
            futureEntity.addCallback(new ListenableFutureCallback<ResponseEntity<String>>() {
                @Override
                public void onSuccess(ResponseEntity<String> entity) {
                    logger.debug(String.format("===【TunnelUuid: %s】控制器返回：%s！", tunnelVO.getUuid(), entity.getBody()));

                    if (entity.getStatusCode() == HttpStatus.OK) {
                        ControllerCommands.ControllerRestResponse response =
                                JSON.parseObject(entity.getBody(), ControllerCommands.ControllerRestResponse.class);

                        if (response.isSuccess()) {
                            // 更新状态
                            tunnelVO.setMonitorState(TunnelMonitorState.Enabled);
                            dbf.update(tunnelVO);

                            logger.debug(String.format("===【TunnelUuid: %s】添加ZK成功！", tunnelVO.getUuid()));
                        } else {
                            throw new RuntimeException(String.format("===【TunnelUuid: %s】添加ZK失败！Error: %s", tunnelVO.getUuid(), response.getMsg()));
                        }

                    } else {
                        throw new RuntimeException(String.format("===【TunnelUuid: %s】发送控制器失败！【StatusCode: %s】", tunnelVO.getUuid(), entity.getStatusCode()));
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    throw new RuntimeException(String.format("===【TunnelUuid: %s】发送控制器失败,Error: %s", tunnelVO.getUuid(), t.getMessage()));
                }
            });
        }

        APIInitTunnelMonitorEvent event = new APIInitTunnelMonitorEvent(msg.getId());
        bus.publish(event);
    }

    /***
     * 创建专线监控通道
     * @param
     * @return 创建通道监控
     */
    @Transactional
    public List<TunnelMonitorVO> initTunnelMonitor(String tunnelUuid) {
        TunnelVO tunnelVO = dbf.getEntityManager().find(TunnelVO.class, tunnelUuid);
        // 共点不能使用相同的cidr，防止ip重复
        List<TunnelVO> tunnelVOS = Q.New(TunnelVO.class)
                .eq(TunnelVO_.monitorCidr, tunnelVO.getMonitorCidr())
                .eq(TunnelVO_.vsi, tunnelVO.getVsi())
                .notEq(TunnelVO_.uuid, tunnelVO.getUuid())
                .list();
        if (!tunnelVOS.isEmpty())
            throw new IllegalArgumentException(String.format("monitor cidr %s has been used by tunnel %s, " +
                    "plsase enter another cidr!", tunnelVO.getMonitorCidr(), tunnelVOS.get(0).getName()));

        // 删除遗留数据
        List<TunnelMonitorVO> tunnelMonitorVOS = Q.New(TunnelMonitorVO.class)
                .eq(TunnelMonitorVO_.tunnelUuid, tunnelVO.getUuid())
                .list();
        for (TunnelMonitorVO tunnelMonitorVO : tunnelMonitorVOS)
            dbf.getEntityManager().remove(tunnelMonitorVO);

        tunnelMonitorVOS = new ArrayList<>();
        // 获取tunnel两端交换机端口
        List<TunnelSwitchPortVO> portVOS = getMonitorTunnelSwitchPortByTunnelId(tunnelVO.getUuid());

        String physicalSwitchUuid = "";
        for (TunnelSwitchPortVO tunnelSwitchPortVO : portVOS) {
            if (tunnelSwitchPortVO.getPhysicalSwitchUuid().equals(physicalSwitchUuid))
                throw new RuntimeException(String.format("can't start monitor for tunnel on the same switch!"));
            else
                physicalSwitchUuid = tunnelSwitchPortVO.getPhysicalSwitchUuid();

            TunnelMonitorVO tunnelMonitorVO = new TunnelMonitorVO();
            tunnelMonitorVO.setUuid(Platform.getUuid());
            tunnelMonitorVO.setTunnelUuid(tunnelVO.getUuid());
            tunnelMonitorVO.setTunnelSwitchPortUuid(tunnelSwitchPortVO.getUuid());
            // 监控IP
            String monitorIp = generateMonitorIp(tunnelMonitorVO, tunnelVO.getMonitorCidr());
            tunnelMonitorVO.setMonitorIp(monitorIp);
            // 监控主机
            String hostUuid = getHostUuid(tunnelSwitchPortVO.getSwitchPortUuid());
            tunnelMonitorVO.setHostUuid(hostUuid);
            dbf.getEntityManager().persist(tunnelMonitorVO);

            tunnelMonitorVOS.add(tunnelMonitorVO);
        }

        if (tunnelMonitorVOS.isEmpty())
            throw new IllegalArgumentException(String.format(" Failed to init tunnel monitor！ %s ", tunnelVO.getName()));

        return tunnelMonitorVOS;
    }

    /***
     * 开启控制器监控
     * @param tunnelUuid
     * @return
     */
    @Transactional
    public void startControllerMonitor(String tunnelUuid) {
        if (dbf.isExist(tunnelUuid, TunnelVO.class)) {
            initTunnelMonitor(tunnelUuid);

            List<TunnelMonitorVO> tunnelMonitorVOS = Q.New(TunnelMonitorVO.class)
                    .eq(TunnelMonitorVO_.tunnelUuid, tunnelUuid)
                    .list();
            ControllerCommands.TunnelMonitorCommand cmd = getControllerMonitorCommand(tunnelUuid, false, tunnelMonitorVOS);

            String url = getControllerUrl(ControllerRestConstant.START_TUNNEL_MONITOR);
            ControllerCommands.ControllerRestResponse response = sendControllerCommand(url, JSONObjectUtil.toJsonString(cmd));
            if (!response.isSuccess()) {
                if (needRollback(response)) {
                    TunnelMonitorJob monitorJob = new TunnelMonitorJob();
                    monitorJob.setTunnelUuid(cmd.getTunnel_id());
                    monitorJob.setJobType(MonitorJobType.ROLLBACK);

                    jobf.execute("开启监控失败-回滚", Platform.getManagementServerId(), monitorJob);
                }

                throw new RuntimeException(String.format("Failure to execute RYU start command [TunnelUuid: %s]! Error:%s"
                        , cmd.getTunnel_id(), response.getMsg()));
            }
        }
    }

    /***
     * 开启控制器监控，不判断回滚（job调用）
     * @param tunnelUuid
     * @return
     */
    @Transactional
    public void startControllerMonitorNoRollback(String tunnelUuid) {
        if (dbf.isExist(tunnelUuid, TunnelVO.class)) {
            // job stop时，没有清除数据，此处不需要初始化
            // initTunnelMonitor(tunnelUuid);

            List<TunnelMonitorVO> tunnelMonitorVOS = Q.New(TunnelMonitorVO.class)
                    .eq(TunnelMonitorVO_.tunnelUuid, tunnelUuid)
                    .list();
            ControllerCommands.TunnelMonitorCommand cmd = getControllerMonitorCommand(tunnelUuid, false, tunnelMonitorVOS);

            String url = getControllerUrl(ControllerRestConstant.START_TUNNEL_MONITOR);
            ControllerCommands.ControllerRestResponse response = sendControllerCommand(url, JSONObjectUtil.toJsonString(cmd));
            if (!response.isSuccess())
                throw new RuntimeException(String.format("Failure to execute RYU start command [TunnelUuid: %s]! Error:%s"
                        , cmd.getTunnel_id(), response.getMsg()));
        }
    }

    /***
     * 开启监控下发失败，且回滚失败，job调用监控回滚命令下发至控制器
     * @param tunnelUuid
     * @return
     */
    @Transactional
    public void startControllerMonitorRollback(String tunnelUuid) {
        if (dbf.isExist(tunnelUuid, TunnelVO.class)) {
            List<TunnelMonitorVO> tunnelMonitorVOS = Q.New(TunnelMonitorVO.class)
                    .eq(TunnelMonitorVO_.tunnelUuid, tunnelUuid)
                    .list();
            ControllerCommands.TunnelMonitorCommand cmd = getControllerMonitorCommand(tunnelUuid, true, tunnelMonitorVOS);

            String url = getControllerUrl(ControllerRestConstant.START_TUNNEL_MONITOR);
            ControllerCommands.ControllerRestResponse response = sendControllerCommand(url, JSONObjectUtil.toJsonString(cmd));
            if (!response.isSuccess())
                throw new RuntimeException(String.format("Failure to execute RYU start command [TunnelUuid: %s]! Error:%s"
                        , cmd.getTunnel_id(), response.getMsg()));
        }
    }

    /**
     * 停止控制器监控
     */
    @Transactional
    public void stopControllerMonitor(String tunnelUuid) {
        Map<String, String> cmd = new HashMap<>();
        cmd.put("tunnel_id", tunnelUuid);

        String url = getControllerUrl(ControllerRestConstant.STOP_TUNNEL_MONITOR);
        ControllerCommands.ControllerRestResponse response = sendControllerCommand(url, JSONObjectUtil.toJsonString(cmd));
        if (!response.isSuccess())
            throw new RuntimeException(String.format("Failure to execute RYU stop command! Error:%s"
                    , response.getMsg()));
    }

    /**
     * 控制器命令修改：修改监控ip、vlan、带宽、端口（跨交换机）
     */
    @Transactional
    public void modifyControllerMonitor(String tunnelUuid) {
        if (dbf.isExist(tunnelUuid, TunnelVO.class)) {
            initTunnelMonitor(tunnelUuid);

            List<TunnelMonitorVO> tunnelMonitorVOS = Q.New(TunnelMonitorVO.class).eq(TunnelMonitorVO_.tunnelUuid, tunnelUuid).list();
            ControllerCommands.TunnelMonitorCommand cmd = getControllerMonitorCommand(tunnelUuid, false, tunnelMonitorVOS);

            String url = getControllerUrl(ControllerRestConstant.MODIFY_TUNNEL_MONITOR);
            ControllerCommands.ControllerRestResponse response = sendControllerCommand(url, JSONObjectUtil.toJsonString(cmd));
            if (!response.isSuccess())
                throw new RuntimeException(String.format("Failure to execute RYU modify command [TunnelUuid : %s]! Error:%s"
                        , cmd.getTunnel_id(), response.getMsg()));
        }
    }

    /**
     * 删除监控（job调用：删除tunnel、删除monitor host）
     */
    @Transactional
    public void deleteControllerMonitor(String tunnelUuid) {
        TunnelVO tunnelVO = dbf.findByUuid(tunnelUuid, TunnelVO.class);
        if (tunnelVO != null) {
            tunnelVO.setMonitorState(TunnelMonitorState.Disabled);
            tunnelVO.setStatus(TunnelStatus.Connected);
            tunnelVO.setMonitorCidr("");
            dbf.update(tunnelVO);
        }
        UpdateQuery.New(TunnelMonitorVO.class).eq(TunnelMonitorVO_.tunnelUuid, tunnelUuid).delete();

        Map<String, String> cmd = new HashMap<>();
        cmd.put("tunnel_id", tunnelUuid);

        String url = getControllerUrl(ControllerRestConstant.STOP_TUNNEL_MONITOR);
        ControllerCommands.ControllerRestResponse response = sendControllerCommand(url, JSONObjectUtil.toJsonString(cmd));
        if (!response.isSuccess())
            throw new RuntimeException(String.format("Failure to execute RYU stop command! Error:%s"
                    , response.getMsg()));

    }

    /**
     * 根据控制器返回值判断是否需要回滚
     *
     * @param response
     * @return
     */
    public boolean needRollback(ControllerCommands.ControllerRestResponse response) {
        boolean needRollback = false;

        if (response.isRollback() != null || response.isRollback())
            needRollback = false;
        else
            needRollback = true;

        return needRollback;
    }

    /**
     * 获取监控下发controller命令
     *
     * @param tunnelUuid
     * @return
     */
    public ControllerCommands.TunnelMonitorCommand getControllerMonitorCommand(String tunnelUuid, boolean rollback
            , List<TunnelMonitorVO> tunnelMonitorVOS) {
        List<ControllerCommands.TunnelMonitorMpls> mplsList = new ArrayList<>();
        List<ControllerCommands.TunnelMonitorSdn> sdnList = new ArrayList<>();

        Map<String, String> monitorIp = new HashMap<>();
        Map<String, String> monitorPort = new HashMap<>();

        getIpPort(tunnelMonitorVOS, monitorIp, monitorPort);

        TunnelVO tunnelVO = Q.New(TunnelVO.class).eq(TunnelVO_.uuid, tunnelUuid).find();
        for (TunnelMonitorVO tunnelMonitorVO : tunnelMonitorVOS) {
            PhysicalSwitchVO physicalSwitchVO = getPhysicalSwitchByTunnelSwitchPort(
                    tunnelMonitorVO.getTunnelSwitchPortUuid());
            TunnelSwitchPortVO tunnelSwitchPortVO = Q.New(TunnelSwitchPortVO.class)
                    .eq(TunnelSwitchPortVO_.uuid, tunnelMonitorVO.getTunnelSwitchPortUuid())
                    .find();
            SwitchPortVO switchPortVO = Q.New(SwitchPortVO.class)
                    .eq(SwitchPortVO_.uuid, tunnelSwitchPortVO.getSwitchPortUuid())
                    .find();

            ControllerCommands.TunnelMonitorMpls mpls = new ControllerCommands.TunnelMonitorMpls();
            if (PhysicalSwitchType.MPLS.equals(physicalSwitchVO.getType())) {
                mpls.setUuid(physicalSwitchVO.getUuid());
                mpls.setM_ip(physicalSwitchVO.getmIP());
                mpls.setUsername(physicalSwitchVO.getUsername());
                mpls.setPassword(physicalSwitchVO.getPassword());
                mpls.setSwitch_type(physicalSwitchVO.getSwitchModel().getModel());
                mpls.setSub_type(physicalSwitchVO.getSwitchModel().getSubModel());
                mpls.setVlan_id(tunnelSwitchPortVO.getVlan());
                mpls.setBandwidth(SizeUnit.BYTE.toKiloByte(tunnelVO.getBandwidth()));
                mpls.setVni(tunnelVO.getVsi());

                HostSwitchMonitorVO hostSwitchMonitorVO = getHostSwitchMonitorByHostUuid(tunnelMonitorVO.getHostUuid(), physicalSwitchVO.getUuid());
                mpls.setPort_name(hostSwitchMonitorVO.getPhysicalSwitchPortName());

                mplsList.add(mpls);
            } else if (PhysicalSwitchType.SDN.equals(physicalSwitchVO.getType())) {
                // 获取上联口对应的物理交换机作为mpls数据
                PhysicalSwitchUpLinkRefVO upLinkRef = (PhysicalSwitchUpLinkRefVO) Q.New(PhysicalSwitchUpLinkRefVO.class)
                        .eq(PhysicalSwitchUpLinkRefVO_.physicalSwitchUuid, physicalSwitchVO.getUuid())
                        .list().get(0);
                PhysicalSwitchVO uplinkPhysicalSwitch = Q.New(PhysicalSwitchVO.class)
                        .eq(PhysicalSwitchVO_.uuid, upLinkRef.getUplinkPhysicalSwitchUuid())
                        .find();

                ControllerCommands.TunnelMonitorSdn sdn = new ControllerCommands.TunnelMonitorSdn();
                sdn.setM_ip(physicalSwitchVO.getmIP());
                sdn.setUplink(upLinkRef.getPortName());
                sdn.setBandwidth(SizeUnit.BYTE.toKiloByte(tunnelVO.getBandwidth()));
                sdn.setVlan_id(tunnelSwitchPortVO.getVlan());
                sdn.setUuid(physicalSwitchVO.getUuid());

                if (tunnelSwitchPortVO.getSortTag().equals(InterfaceType.A.toString())) {
                    sdn.setNw_src(removeMaskFromIp(monitorIp.get(InterfaceType.A.toString())));
                    sdn.setNw_dst(removeMaskFromIp(monitorIp.get(InterfaceType.Z.toString())));
                    sdn.setIn_port(monitorPort.get(InterfaceType.A.toString()));
                } else if (tunnelSwitchPortVO.getSortTag().equals(InterfaceType.Z.toString())) {
                    sdn.setNw_src(removeMaskFromIp(monitorIp.get(InterfaceType.Z.toString())));
                    sdn.setNw_dst(removeMaskFromIp(monitorIp.get(InterfaceType.A.toString())));
                    sdn.setIn_port(monitorPort.get(InterfaceType.Z.toString()));
                }

                sdnList.add(sdn);
            }
        }

        if (sdnList.isEmpty() && mplsList.isEmpty())
            throw new IllegalArgumentException("failed to generate controller command!");

        return ControllerCommands.TunnelMonitorCommand.valueOf(tunnelUuid, rollback, sdnList, mplsList);
    }

    /***
     * 获取RYU控制器服务url
     * @param method
     * @return
     */
    private String getControllerUrl(String method) {
        String url = CoreGlobalProperty.CONTROLLER_MANAGER_URL + method;

        return url;
    }

    private ControllerCommands.ControllerRestResponse sendControllerCommand(String url, String jsonCommand) {
        ControllerCommands.ControllerRestResponse response = new ControllerCommands.ControllerRestResponse();
        try {
            logger.info(String.format("======= Begin to send controller command: url: %s command: %s", url, jsonCommand));

            response = restf.syncJsonPost(url, jsonCommand, ControllerCommands.ControllerRestResponse.class);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMsg(String.format("unable to post %s. %s", url, e.getMessage()));
        }

        return response;
    }

    /***
     * 开启agnet监控
     * @return
     */
    private void startAgentMonitor(String tunnelUuid) {
        List<TunnelMonitorVO> tunnelMonitorVOS = Q.New(TunnelMonitorVO.class)
                .eq(TunnelMonitorVO_.tunnelUuid, tunnelUuid)
                .list();

        // 下发监控agent配置
        for (TunnelMonitorVO tunnelMonitor : tunnelMonitorVOS) {
            MonitorAgentCommands.AgentIcmp agentIcmp = getAgentIcmp(tunnelMonitor);

            String hostIp = getMonitorHostIpByTunnelMonitorUuid(tunnelMonitor.getUuid());
            String url = getMonitorAgentUrl(hostIp, MonitorAgentConstant.START_MONITOR);

            sendMonitorAgentCommand(url, JSONObjectUtil.toJsonString(agentIcmp));
        }
    }

    /***
     * 关闭agent监控
     * @param tunnelUuid
     * @param tunnelUuid
     */
    private void stopAgentMonitor(String tunnelUuid) {
        List<TunnelMonitorVO> tunnelMonitorVOS = Q.New(TunnelMonitorVO.class)
                .eq(TunnelMonitorVO_.tunnelUuid, tunnelUuid)
                .list();

        for (TunnelMonitorVO tunnelMonitor : tunnelMonitorVOS) {
            String hostIp = getMonitorHostIpByTunnelMonitorUuid(tunnelMonitor.getUuid());
            Map<String, String> tunnelMap = new HashMap<>();
            tunnelMap.put("tunnel_id", tunnelMonitor.getTunnelUuid());

            String url = getMonitorAgentUrl(hostIp, MonitorAgentConstant.STOP_MONITOR);
            sendMonitorAgentCommand(url, JSONObjectUtil.toJsonString(tunnelMap));
        }
    }

    /***
     * 修改agent监控
     */
    private void updateAgentMonitor(String tunnelUuid) {
        List<TunnelMonitorVO> tunnelMonitorVOS = Q.New(TunnelMonitorVO.class)
                .eq(TunnelMonitorVO_.tunnelUuid, tunnelUuid)
                .list();

        // 下发监控agent配置
        for (TunnelMonitorVO tunnelMonitor : tunnelMonitorVOS) {
            MonitorAgentCommands.AgentIcmp agentIcmp = getAgentIcmp(tunnelMonitor);
            String hostIp = getMonitorHostIpByTunnelMonitorUuid(tunnelMonitor.getUuid());
            String url = getMonitorAgentUrl(hostIp, MonitorAgentConstant.UPDATE_MONITOR);

            sendMonitorAgentCommand(url, JSONObjectUtil.toJsonString(agentIcmp));
        }
    }

    /***
     * 获取ICMP信息
     * @param tunnelMonitorVO
     * @return
     */
    public MonitorAgentCommands.AgentIcmp getAgentIcmp(TunnelMonitorVO tunnelMonitorVO) {

        MonitorAgentCommands.AgentIcmp icmp = new MonitorAgentCommands.AgentIcmp();

        icmp.setTunnel_id(tunnelMonitorVO.getTunnelUuid());
        icmp.setSrc_monitor_ip(tunnelMonitorVO.getMonitorIp());

        TunnelSwitchPortVO tunnelSwitchPortVO =
                dbf.findByUuid(tunnelMonitorVO.getTunnelSwitchPortUuid(), TunnelSwitchPortVO.class);
        icmp.setVlan(tunnelSwitchPortVO.getVlan());

        SwitchPortVO switchPortVO = dbf.findByUuid(tunnelSwitchPortVO.getSwitchPortUuid(), SwitchPortVO.class);
        PhysicalSwitchVO physicalSwitch = dbf.findByUuid(switchPortVO.getSwitchs().getPhysicalSwitchUuid(), PhysicalSwitchVO.class);
        icmp.setSwitch_mip(physicalSwitch.getmIP());

        HostSwitchMonitorVO hostSwitchMonitorVO = getHostSwitchMonitorByHostUuid(tunnelMonitorVO.getHostUuid(), physicalSwitch.getUuid());
        icmp.setInterface_name(hostSwitchMonitorVO.getInterfaceName());

        List<TunnelMonitorVO> tunnelMonitors = Q.New(TunnelMonitorVO.class)
                .eq(TunnelMonitorVO_.tunnelUuid, tunnelMonitorVO.getTunnelUuid())
                .notEq(TunnelMonitorVO_.uuid, tunnelMonitorVO.getUuid())
                .list();
        if (!tunnelMonitors.isEmpty())
            icmp.setDst_monitor_ip(tunnelMonitors.get(0).getMonitorIp());

        return icmp;
    }

    /***
     * 获取监控agent 服务url
     * @param hostIp
     * @param method
     * @return
     */
    private String getMonitorAgentUrl(String hostIp, String method) {
        String url = "http://" + hostIp + ":" + MonitorAgentConstant.SERVER_PORT + method;

        return url;
    }

    /***
     * 发送监控agent命令
     * @param url
     * @param command
     * @return
     */
    private void sendMonitorAgentCommand(String url, String command) {
        MonitorAgentCommands.RestResponse response = new MonitorAgentCommands.RestResponse();
        try {
            logger.info(String.format("======= Begin to send agent command: url: %s command: %s", url, command));

            response = restf.syncJsonPost(url,
                    command, MonitorAgentCommands.RestResponse.class);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMsg(String.format("[MonitorAgent]unable to post %s. %s", url, e.getMessage()));
        }

        if (!response.isSuccess())
            throw new RuntimeException(String.format("failed to send agent command! Error: %s"
                    , response.getMsg()));
    }

    /***
     * 获取OpenTSDB服务url
     * @param method
     * @return
     */
    private String getOpenTSDBUrl(String method) {
        String url = CoreGlobalProperty.OPENTSDB_SERVER_URL + method;

        return url;
    }

    /**
     * 生成监控ip
     *
     * @param tunnelMonitorVO
     * @param monitorCidr
     * @return
     */
    private String generateMonitorIp(TunnelMonitorVO tunnelMonitorVO, String monitorCidr) {
        String ip = null;

        if (StringUtils.isEmpty(monitorCidr))
            throw new RuntimeException(String.format("fial to generate monitor, monitr cidr is empty! [TunnelUuid: %s]"
                    , tunnelMonitorVO.getTunnelUuid()));

        List<String> locatedIps = getLocatedIps(tunnelMonitorVO.getTunnelUuid(), monitorCidr);

        CIDRUtils cidrUtils = null;
        try {
            cidrUtils = new CIDRUtils(monitorCidr);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        String startIp = cidrUtils.getNetworkAddress();
        String endIp = cidrUtils.getBroadcastAddress();

        long s = NetworkUtils.ipv4StringToLong(startIp);
        long e = NetworkUtils.ipv4StringToLong(endIp);
        for (; s <= e; s++) {
            ip = NetworkUtils.longToIpv4String(s);
            if (!locatedIps.contains(ip)) {
                break;
            }
        }

        if (ip == null || ip.length() == 0)
            throw new IllegalArgumentException(String.format("Fail to generate monitor ip from CIDR %s", monitorCidr));

        //掩码
        String mask = "/" + StringUtils.substringAfterLast(monitorCidr, "/");

        return ip + mask;
    }

    /***
     * 获取已分配的监控ip
     * @param monitorCidr
     * @return
     */
    private List<String> getLocatedIps(String tunnelUuid, String monitorCidr) {
        List<String> locateIps = new ArrayList<>();

        List<TunnelMonitorVO> tunnelMonitorVOS = Q.New(TunnelMonitorVO.class)
                .eq(TunnelMonitorVO_.tunnelUuid, tunnelUuid)
                .list();

        for (TunnelMonitorVO tunnelMonitorVO : tunnelMonitorVOS)
            locateIps.add(StringUtils.substringBeforeLast(tunnelMonitorVO.getMonitorIp(), "/"));

        String cidrPrefix = monitorCidr.substring(0, monitorCidr.lastIndexOf("."));
        locateIps.add(cidrPrefix + ".0");
        locateIps.add(cidrPrefix + ".255");

        return locateIps;
    }

    /**
     * 去除ip中的掩码
     *
     * @param maskIp
     * @return
     */
    private String removeMaskFromIp(String maskIp) {
        String ip = "";
        if (maskIp.contains("/"))
            ip = StringUtils.substringBefore(maskIp, "/");
        else
            ip = maskIp;

        return ip;
    }

    /**
     * 创建监控通道测速
     *
     * @param msg
     * @return 测速记录
     */
    private SpeedRecordsVO generateSpeedRecord(APICreateSpeedRecordsMsg msg) {
        SpeedRecordsVO vo = new SpeedRecordsVO();

        vo.setUuid(Platform.getUuid());
        vo.setTunnelUuid(msg.getTunnelUuid());
        vo.setProtocolType(msg.getProtocolType());
        vo.setDuration(msg.getDuration());
        vo.setStatus(SpeedRecordStatus.TESTING);
        vo.setSrcNodeUuid(msg.getSrcNodeUuid());
        vo.setDstNodeUuid(msg.getDstNodeUuid());

        if (msg.getSession() != null) {
            if (StringUtils.isNotEmpty(msg.getSession().getUuid())) {
                SessionInventory sessionInventory = identityInterceptor.getSessionInventory(msg.getSession().getUuid());
                vo.setAccountUuid(sessionInventory.getAccountUuid());
            }
        }

        TunnelMonitorVO srcTunnelMonitor = getTunnelMonitorByNodeAndTunnel(msg.getSrcNodeUuid(), msg.getTunnelUuid());
        vo.setSrcTunnelMonitorUuid(srcTunnelMonitor.getUuid());

        TunnelMonitorVO dstTunnelMonitors = getTunnelMonitorByNodeAndTunnel(msg.getDstNodeUuid(), msg.getTunnelUuid());
        vo.setDstTunnelMonitorUuid(dstTunnelMonitors.getUuid());

        return vo;
    }

    /***
     * 发送测速命令
     * @param vo
     */
    private void sendSpeedTestCommand(SpeedRecordsVO vo) {
        Map<String, Object> commamdMap = getSpeedTestCommand(vo);

        // server
        String serverCommand = JSONObjectUtil.toJsonString(
                commamdMap.get(MonitorAgentCommands.SpeedCommandType.server.toString()));
        String serverUrl = getMonitorAgentUrl(
                getMonitorHostIpByTunnelMonitorUuid(vo.getSrcTunnelMonitorUuid()), MonitorAgentConstant.IPERF);
        sendMonitorAgentCommand(serverUrl, serverCommand);

        String clientCommand = JSONObjectUtil.toJsonString(
                commamdMap.get(MonitorAgentCommands.SpeedCommandType.client.toString()));
        String clientUrl = getMonitorAgentUrl(
                getMonitorHostIpByTunnelMonitorUuid(vo.getDstTunnelMonitorUuid()), MonitorAgentConstant.IPERF);
        sendMonitorAgentCommand(clientUrl, clientCommand);
    }

    private String getMonitorHostIpByTunnelMonitorUuid(String tunnelMonitorUuid) {
        TunnelMonitorVO tunnelMonitorVO = Q.New(TunnelMonitorVO.class).eq(TunnelMonitorVO_.uuid, tunnelMonitorUuid).find();
        String monitorHostIp = Q.New(MonitorHostVO.class)
                .eq(MonitorHostVO_.uuid, tunnelMonitorVO.getHostUuid())
                .select(MonitorHostVO_.hostIp).findValue();

        return monitorHostIp;
    }

    /**
     * 获取测速下发命令
     *
     * @param vo：SpeedRecordsVO
     * @return：server、client测速命令
     */
    private Map<String, Object> getSpeedTestCommand(SpeedRecordsVO vo) {
        Map<String, Object> commandMap = new HashMap<>();

        TunnelVO tunnelVO = Q.New(TunnelVO.class).eq(TunnelVO_.uuid, vo.getTunnelUuid()).find();
        int port = MonitorAgentCommands.getPort();

        // 服务端
        MonitorAgentCommands.SpeedRecordServer server = new MonitorAgentCommands.SpeedRecordServer();
        server.setGuid(vo.getUuid());
        server.setTunnel_id(vo.getTunnelUuid());
        server.setTime(vo.getDuration());
        server.setType(MonitorAgentCommands.SpeedCommandType.server);
        server.setPort(port);

        TunnelMonitorVO srcTunnelMonitor = Q.New(TunnelMonitorVO.class)
                .eq(TunnelMonitorVO_.uuid, vo.getSrcTunnelMonitorUuid())
                .find();

        TunnelSwitchPortVO srcTunnelSwitchPortVO =
                dbf.findByUuid(srcTunnelMonitor.getTunnelSwitchPortUuid(), TunnelSwitchPortVO.class);

        server.setSrc_ip(removeMaskFromIp(srcTunnelMonitor.getMonitorIp()));
        server.setInterface_name(getHostSwitchMonitorByHostUuid(srcTunnelMonitor.getHostUuid(), srcTunnelSwitchPortVO.getPhysicalSwitchUuid()).getInterfaceName());
        server.setVlan(getTunnelSwitchPortByUuid(srcTunnelMonitor.getTunnelSwitchPortUuid()).getVlan());
        commandMap.put(MonitorAgentCommands.SpeedCommandType.server.toString(), server);

        // 客户端
        MonitorAgentCommands.SpeedRecordClient client = new MonitorAgentCommands.SpeedRecordClient();
        client.setTunnel_id(vo.getTunnelUuid());
        client.setTime(vo.getDuration());
        client.setType(MonitorAgentCommands.SpeedCommandType.client);
        client.setPort(port);

        TunnelMonitorVO dstTunnelMonitor = Q.New(TunnelMonitorVO.class)
                .eq(TunnelMonitorVO_.uuid, vo.getDstTunnelMonitorUuid())
                .find();

        TunnelSwitchPortVO dstTunnelSwitchPortVO =
                dbf.findByUuid(dstTunnelMonitor.getTunnelSwitchPortUuid(), TunnelSwitchPortVO.class);

        client.setSrc_ip(removeMaskFromIp(dstTunnelMonitor.getMonitorIp()));
        client.setInterface_name(getHostSwitchMonitorByHostUuid(dstTunnelMonitor.getHostUuid(), dstTunnelSwitchPortVO.getPhysicalSwitchUuid()).getInterfaceName());
        client.setVlan(getTunnelSwitchPortByUuid(dstTunnelMonitor.getTunnelSwitchPortUuid()).getVlan());
        client.setDst_ip(removeMaskFromIp(srcTunnelMonitor.getMonitorIp()));
        client.setProtocol(vo.getProtocolType());
        client.setBandwidth(SizeUnit.BYTE.toMegaByte(tunnelVO.getBandwidth())); //M

        commandMap.put(MonitorAgentCommands.SpeedCommandType.client.toString(), client);

        return commandMap;
    }

    /***
     * 更新tunnel监控状态
     * @param tunnelUuid
     * @param monitorCidr
     * @param monitorState
     */
    private TunnelVO updateTunnel(String tunnelUuid, String monitorCidr, TunnelMonitorState monitorState) {
        // 更新tunnel状态
        TunnelVO tunnelVO = Q.New(TunnelVO.class)
                .eq(TunnelVO_.uuid, tunnelUuid)
                .find();

        tunnelVO.setMonitorState(monitorState);
        tunnelVO.setStatus(TunnelStatus.Connected);
        if (StringUtils.isNotEmpty(monitorCidr)) {
            tunnelVO.setMonitorCidr(monitorCidr);
        }

        dbf.getEntityManager().merge(tunnelVO);
        return tunnelVO;
    }

    private void handle(APIQuerySpeedTestTunnelNodeMsg msg) {
        APIQuerySpeedTestTunnelNodeReply reply = new APIQuerySpeedTestTunnelNodeReply();

        List<SpeedTestTunnelVO> speedTestTunnelVOS = Q.New(SpeedTestTunnelVO.class).list();
        for (int i = 0; i < speedTestTunnelVOS.size(); i++) {
            SpeedTestTunnelVO speedTestTunnelVO = speedTestTunnelVOS.get(i);
            if (!speedTestTunnelVO.getTunnelVO().getState().equals(TunnelState.Enabled) ||
                    !speedTestTunnelVO.getTunnelVO().getStatus().equals(TunnelStatus.Connected) ||
                    !speedTestTunnelVO.getTunnelVO().getMonitorState().equals(TunnelMonitorState.Enabled)) {

                speedTestTunnelVOS.remove(i);
            }
        }

        reply.setInventories(SpeedTestTunnelNodeInventory.valueOf(speedTestTunnelVOS));
        bus.reply(msg, reply);
    }

    @Transactional
    private void handle(APICreateSpeedRecordsMsg msg) {
        APICreateSpeedRecordsEvent event = new APICreateSpeedRecordsEvent(msg.getId());

        // 创建纪录
        SpeedRecordsVO vo = generateSpeedRecord(msg);

        // 下发测速命令
        sendSpeedTestCommand(vo);

        // 保存测试记录
        vo = dbf.persistAndRefresh(vo);

        event.setInventory(StartSpeedRecordsInventory.valueOf(vo
                , getMonitorHostIpByTunnelMonitorUuid(vo.getSrcTunnelMonitorUuid())));
        bus.publish(event);
    }

    private void handle(APIQuerySpeedResultMsg msg) {
        APIQuerySpeedResultReply reply = new APIQuerySpeedResultReply();

        List<MonitorAgentCommands.SpeedResult> results = new ArrayList<>();

        Map<String, String> map = new HashMap<>();
        map.put("guid", msg.getUuid());
        String command = JSONObjectUtil.toJsonString(map);
        String url = getMonitorAgentUrl(msg.getHostIp(), MonitorAgentConstant.IPERF_RESULT);

        String resp = restf.getRESTTemplate().postForObject(url, command, String.class);

        results = JSON.parseArray(resp, MonitorAgentCommands.SpeedResult.class);

        if (reply.isSuccess())
            reply.setInventories(SpeedResultInventory.valueOf(results));

        bus.reply(msg, reply);
    }

    private void handle(APIUpdateSpeedRecordsMsg msg) {
        SpeedRecordsVO vo = dbf.findByUuid(msg.getUuid(), SpeedRecordsVO.class);

        vo.setAvgSpeed(msg.getAvgSpeed());
        vo.setMaxSpeed(msg.getMaxSpeed());
        vo.setMinSpeed(msg.getMinSpeed());
        vo.setStatus(msg.getStatus());

        vo = dbf.updateAndRefresh(vo);

        APIUpdateSpeedRecordsEvent event = new APIUpdateSpeedRecordsEvent(msg.getId());
        event.setInventory(SpeedRecordsInventory.valueOf(vo));
        bus.publish(event);
    }

    private void handle(APIDeleteSpeedRecordsMsg msg) {
        SpeedRecordsVO vo = dbf.findByUuid(msg.getUuid(), SpeedRecordsVO.class);

        dbf.remove(vo);

        APIDeleteSpeedRecordsEvent event = new APIDeleteSpeedRecordsEvent(msg.getId());
        event.setInventory(SpeedRecordsInventory.valueOf(vo));
        bus.publish(event);
    }

    private void handle(APICreateNettoolRecordMsg msg) {
        APICreateNettoolRecordEvent event = new APICreateNettoolRecordEvent(msg.getId());

        MonitorAgentCommands.NettoolCommand nettoolCommand = new MonitorAgentCommands.NettoolCommand();
        nettoolCommand.setCommand(msg.getCommand());
        nettoolCommand.setGuid(Platform.getUuid());
        nettoolCommand.setRemote_ip(msg.getRemoteIp());
        String command = JSONObjectUtil.toJsonString(nettoolCommand);

        MonitorHostVO host = dbf.findByUuid(msg.getMonitorHostUuid(), MonitorHostVO.class);
        String url = getMonitorAgentUrl(host.getHostIp(), MonitorAgentConstant.NETTOOL);

        sendMonitorAgentCommand(url, command);

        event.setInventory(NettoolRecordInventory.valueOf(nettoolCommand, host.getHostIp()));
        bus.publish(event);
    }

    private void handle(APIQueryNettoolResultMsg msg) {
        APIQueryNettoolResultReply reply = new APIQueryNettoolResultReply();
        List<MonitorAgentCommands.NettoolResult> result = new ArrayList<>();

        Map<String, String> map = new HashMap<>();
        map.put("guid", msg.getGuid());
        String command = JSONObjectUtil.toJsonString(map);
        String url = getMonitorAgentUrl(msg.getHostIp(), MonitorAgentConstant.NETTOOL_RESULT);

        String resp = restf.getRESTTemplate().postForObject(url, command, String.class);

        result = JSON.parseArray(resp, MonitorAgentCommands.NettoolResult.class);

        if (reply.isSuccess())
            reply.setInventories(NettoolResultInventory.valueOf(result));

        bus.reply(msg, reply);
    }

    private void handle(APIQueryNettoolNodeMsg msg) {
        APIQueryNettoolNodeReply reply = new APIQueryNettoolNodeReply();

        List<MonitorHostVO> monitorHostVOS = Q.New(MonitorHostVO.class)
                .eq(MonitorHostVO_.monitorType, MonitorType.NETTOOL)
                .eq(MonitorHostVO_.status, HostStatus.Connected)
                .eq(MonitorHostVO_.state, HostState.Enabled)
                .list();

        reply.setInventories(MonitorHostInventory.valueOf1(monitorHostVOS));

        bus.reply(msg, reply);
    }

    private void handle(APIQueryNettoolMonitorHostMsg msg) {
        APIQueryNettoolMonitorHostReply reply = new APIQueryNettoolMonitorHostReply();

        List<MonitorHostVO> monitorHostVOS = Q.New(MonitorHostVO.class)
                .eq(MonitorHostVO_.monitorType, MonitorType.NETTOOL)
                .eq(MonitorHostVO_.status, HostStatus.Connected)
                .eq(MonitorHostVO_.state, HostState.Enabled)
                .list();

        reply.setInventories(NettoolMonitorHostInventory.valueOf(monitorHostVOS));

        bus.reply(msg, reply);
    }

    private void handle(APIQueryMonitorResultMsg msg) {
        APIQueryMonitorResultReply reply = new APIQueryMonitorResultReply();

        String url = getOpenTSDBUrl(OpenTSDBCommands.restMethod.OPEN_TSDB_QUERY);
        String condition = getOpenTSDBQueryCondition(msg);

        logger.info(String.format("======= Begin to get OpenTSDB data url: %s condition: %s", url, condition));
        String resp = "";
        try {
            resp = restf.getRESTTemplate().postForObject(url, condition, String.class);
        } catch (Exception e) {
            resp = "";
        }

        List<OpenTSDBCommands.QueryResult> results = new ArrayList<>();
        if (!StringUtils.isEmpty(resp)) {
            results = JSON.parseArray(resp, OpenTSDBCommands.QueryResult.class);
            for (OpenTSDBCommands.QueryResult result : results) {
                String mIP = result.getTags().getEndpoint();
                List<PhysicalSwitchVO> physicalSwitchVOS = Q.New(PhysicalSwitchVO.class)
                        .eq(PhysicalSwitchVO_.mIP, mIP)
                        .list();

                if (physicalSwitchVOS.isEmpty())
                    throw new IllegalArgumentException(String.format("Fail to get physical switch by mIP %s", mIP));

                result.setNodeUuid(physicalSwitchVOS.get(0).getNodeUuid());
            }
        }

        reply.setInventories(OpenTSDBResultInventory.valueOf(results));
        bus.reply(msg, reply);
    }

    /***
     * 获取OpenTSDB查询条件
     * @param msg
     * @return
     */
    private String getOpenTSDBQueryCondition(APIQueryMonitorResultMsg msg) {
        List<OpenTSDBCommands.Query> queries = new ArrayList<>();

        TunnelVO tunnel = Q.New(TunnelVO.class).eq(TunnelVO_.uuid, msg.getTunnelUuid()).find();
        for (TunnelSwitchPortVO tunnelPort : tunnel.getTunnelSwitchPortVOS()) {
            if (tunnelPort.getSortTag().equals(InterfaceType.A.toString()) ||
                    tunnelPort.getSortTag().equals(InterfaceType.Z.toString())) {
                PhysicalSwitchVO physicalSwitch = getPhysicalSwitchBySwitchPort(tunnelPort.getSwitchPortUuid());
                if (physicalSwitch == null)
                    throw new IllegalArgumentException(String.format("No physical switch exist under switch port %s"
                            , tunnelPort.getSwitchPortUuid()));

                OpenTSDBCommands.Tags tags;
                for (String metric : msg.getMetrics()) {
                    if (!"switch".equals(metric.substring(0, metric.indexOf("."))))
                        tags = new OpenTSDBCommands.Tags(physicalSwitch.getmIP()
                                , "Vlanif" + tunnelPort.getVlan(), msg.getTunnelUuid());
                    else
                        tags = new OpenTSDBCommands.Tags(physicalSwitch.getmIP()
                                , "Vlanif" + tunnelPort.getVlan());

                    OpenTSDBCommands.Query query = new OpenTSDBCommands.Query("avg", metric, tags);
                    queries.add(query);
                }
            }
        }
        OpenTSDBCommands.QueryCondition condition = new OpenTSDBCommands.QueryCondition();
        condition.setStart(msg.getStart());
        condition.setEnd(msg.getEnd());
        condition.setQueries(queries);

        return JSONObjectUtil.toJsonString(condition);
    }

    private void handle(APIQuerySwitchPortTrafficMsg msg) {
        APIQuerySwitchPortTrafficReply reply = new APIQuerySwitchPortTrafficReply();

        PhysicalSwitchVO physicalSwitch = getPhysicalSwitchBySwitchPort(msg.getSwitchPortUuid());
        SwitchPortVO switchPortVO = dbf.findByUuid(msg.getSwitchPortUuid(), SwitchPortVO.class);

        List<OpenTSDBCommands.Query> queries = new ArrayList<>();
        for (String metric : msg.getMetrics()) {
            OpenTSDBCommands.Tags tags = new OpenTSDBCommands.Tags(physicalSwitch.getmIP()
                    , switchPortVO.getPortName());

            OpenTSDBCommands.Query query = new OpenTSDBCommands.Query("avg", metric, tags);
            queries.add(query);
        }
        OpenTSDBCommands.QueryCondition condition = new OpenTSDBCommands.QueryCondition();
        condition.setStart(msg.getStart());
        condition.setEnd(msg.getEnd());
        condition.setQueries(queries);

        String url = getOpenTSDBUrl(OpenTSDBCommands.restMethod.OPEN_TSDB_QUERY);
        String resp = "";
        try {
            resp = restf.getRESTTemplate().postForObject(url, JSONObjectUtil.toJsonString(condition), String.class);
        } catch (Exception e) {
            resp = "";
        }

        List<OpenTSDBCommands.QueryResult> results = new ArrayList<>();
        if (!StringUtils.isEmpty(resp))
            results = JSON.parseArray(resp, OpenTSDBCommands.QueryResult.class);

        reply.setInventories(OpenTSDBResultInventory.valueOf(results));
        bus.reply(msg, reply);
    }

    private void handle(APICreateSpeedTestTunnelMsg msg) {
        SpeedTestTunnelVO vo = new SpeedTestTunnelVO();

        vo.setUuid(Platform.getUuid());
        vo.setTunnelUuid(msg.getTunnelUuid());

        vo = dbf.persistAndRefresh(vo);

        APICreateSpeedTestTunnelEvent event = new APICreateSpeedTestTunnelEvent(msg.getId());
        event.setInventory(SpeedTestTunnelInventory.valueOf(vo));
        bus.publish(event);
    }

    private void handle(APIDeleteSpeedTestTunnelMsg msg) {
        SpeedTestTunnelVO vo = dbf.findByUuid(msg.getUuid(), SpeedTestTunnelVO.class);

        dbf.remove(vo);

        APIDeleteSpeedTestTunnelEvent event = new APIDeleteSpeedTestTunnelEvent(msg.getId());
        event.setInventory(SpeedTestTunnelInventory.valueOf(vo));

        bus.publish(event);
    }

    /***
     * 按tunnel交换机端口，查询监控主机uuid
     * @param switchPortUuid
     * @return 监控主机uuid
     */
    private String getHostUuid(String switchPortUuid) {
        String switcUuid = Q.New(SwitchPortVO.class).
                eq(SwitchPortVO_.uuid, switchPortUuid)
                .select(SwitchPortVO_.switchUuid)
                .findValue();

        String physicalSwitchUuid = Q.New(SwitchVO.class).
                eq(SwitchVO_.uuid, switcUuid)
                .select(SwitchVO_.physicalSwitchUuid).findValue();

        List<String> hostUuids = Q.New(HostSwitchMonitorVO.class).
                eq(HostSwitchMonitorVO_.physicalSwitchUuid, physicalSwitchUuid).
                select(HostSwitchMonitorVO_.hostUuid).
                list();

        if (hostUuids.isEmpty())
            throw new IllegalArgumentException("Failed to get monitor host!");

        return hostUuids.get(0);
    }

    /***
     *
     * @param monitorIp
     * @param monitorPort
     */
    private void getIpPort(List<TunnelMonitorVO> tunnelMonitorVOS, Map<String, String> monitorIp
            , Map<String, String> monitorPort) {
        for (TunnelMonitorVO tunnelMonitorVO : tunnelMonitorVOS) {
            TunnelSwitchPortVO tunnelSwitchPortVO = Q.New(TunnelSwitchPortVO.class)
                    .eq(TunnelSwitchPortVO_.uuid, tunnelMonitorVO.getTunnelSwitchPortUuid())
                    .find();

            String physicalSwitchPortName = Q.New(HostSwitchMonitorVO.class)
                    .eq(HostSwitchMonitorVO_.hostUuid, tunnelMonitorVO.getHostUuid())
                    .eq(HostSwitchMonitorVO_.physicalSwitchUuid, tunnelSwitchPortVO.getPhysicalSwitchUuid())
                    .select(HostSwitchMonitorVO_.physicalSwitchPortName)
                    .findValue();

            monitorIp.put(tunnelSwitchPortVO.getSortTag(), tunnelMonitorVO.getMonitorIp());
            monitorPort.put(tunnelSwitchPortVO.getSortTag(), physicalSwitchPortName);
        }
    }

    /***
     * tunnel修改switch port，修改unnelMonitorVO.hostUuid
     */
    public void updateTunnelMonitorHostByTunnelSwitchPort(String tunnelSwitchPortUuid) {
        TunnelSwitchPortVO tunnelSwitchPortVO = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.uuid, tunnelSwitchPortUuid)
                .find();

        MonitorHostVO monitorHostVO = getHostBySwitchPort(tunnelSwitchPortVO.getSwitchPortUuid());
        TunnelMonitorVO tunnelMonitorVO = getTunnelMonitorByTunnelSwitchPort(tunnelSwitchPortUuid);

        if (tunnelMonitorVO != null && monitorHostVO != null) {
            tunnelMonitorVO.setHostUuid(monitorHostVO.getUuid());
            dbf.update(tunnelMonitorVO);
        }
    }


    /***
     * 按switchPortUuid查询SwitchVO
     * @param switchPortUuid
     * @return
     */
    public SwitchVO getSwitchBySwitchPort(String switchPortUuid) {
        String switchUuid = Q.New(SwitchPortVO.class)
                .eq(SwitchPortVO_.uuid, switchPortUuid)
                .select(SwitchPortVO_.switchUuid)
                .findValue();

        SwitchVO switchVO = Q.New(SwitchVO.class)
                .eq(SwitchVO_.uuid, switchUuid)
                .find();

        if (switchVO == null)
            throw new IllegalArgumentException(String.format("Failed to get logical switch by switch port uuid %s!"
                    , switchPortUuid));

        return switchVO;
    }

    /***
     * 按switchPortUuid查询PhysicalSwitchVO
     * @param switchPortUuid
     * @return PhysicalSwitchVO
     */
    public PhysicalSwitchVO getPhysicalSwitchBySwitchPort(String switchPortUuid) {
        String switchUuid = Q.New(SwitchPortVO.class)
                .eq(SwitchPortVO_.uuid, switchPortUuid)
                .select(SwitchPortVO_.switchUuid)
                .findValue();

        String physicalSwitchUuid = Q.New(SwitchVO.class)
                .eq(SwitchVO_.uuid, switchUuid)
                .select(SwitchVO_.physicalSwitchUuid)
                .findValue();

        PhysicalSwitchVO physicalSwitchVO = Q.New(PhysicalSwitchVO.class)
                .eq(PhysicalSwitchVO_.uuid, physicalSwitchUuid)
                .find();

        if (physicalSwitchVO == null)
            throw new IllegalArgumentException(String.format("Failed to get physical switch by switch port uuid %s!"
                    , switchPortUuid));

        return physicalSwitchVO;
    }

    /***
     * 按tunnelSwitchPortUuid查询PhysicalSwitchVO
     * @param tunnelSwitchPortUuid
     * @return PhysicalSwitchVO
     */
    public PhysicalSwitchVO getPhysicalSwitchByTunnelSwitchPort(String tunnelSwitchPortUuid) {
        String switchPortUuid = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.uuid, tunnelSwitchPortUuid)
                .select(TunnelSwitchPortVO_.switchPortUuid)
                .findValue();

        String switchUuid = Q.New(SwitchPortVO.class)
                .eq(SwitchPortVO_.uuid, switchPortUuid)
                .select(SwitchPortVO_.switchUuid)
                .findValue();

        String physicalSwitchUuid = Q.New(SwitchVO.class)
                .eq(SwitchVO_.uuid, switchUuid)
                .select(SwitchVO_.physicalSwitchUuid)
                .findValue();

        PhysicalSwitchVO physicalSwitchVO = Q.New(PhysicalSwitchVO.class)
                .eq(PhysicalSwitchVO_.uuid, physicalSwitchUuid)
                .find();

        if (physicalSwitchVO == null)
            throw new IllegalArgumentException(String.format("Failed to get physical switch by tunnel switch port uuid %s!"
                    , tunnelSwitchPortUuid));

        return physicalSwitchVO;
    }

    /***
     * 按uuid获取SwitchModel
     * @param uuid
     * @return
     */
    public SwitchModelVO getSwitchModel(String uuid) {
        SwitchModelVO vo = Q.New(SwitchModelVO.class)
                .eq(SwitchModelVO_.uuid, uuid)
                .find();

        if (vo == null)
            throw new IllegalArgumentException(String.format("Failed to get switch model %s!", uuid));

        return vo;
    }

    /***
     * 按switchPortUuid查询监控Host
     * @param switchPortUuid
     * @return
     */
    public MonitorHostVO getHostBySwitchPort(String switchPortUuid) {
        String switchUuid = Q.New(SwitchPortVO.class)
                .eq(SwitchPortVO_.uuid, switchPortUuid)
                .select(SwitchPortVO_.switchUuid)
                .findValue();

        String physicalSwitchUuid = Q.New(SwitchVO.class)
                .eq(SwitchVO_.uuid, switchUuid)
                .select(SwitchVO_.physicalSwitchUuid)
                .findValue();

        List<String> hostUuids = Q.New(HostSwitchMonitorVO.class)
                .eq(HostSwitchMonitorVO_.physicalSwitchUuid, physicalSwitchUuid)
                .select(HostSwitchMonitorVO_.hostUuid)
                .list();

        if (hostUuids.isEmpty())
            throw new IllegalArgumentException(String.format("Failed to get host switch ref by physical switch %s!"
                    , physicalSwitchUuid));

        List<MonitorHostVO> monitorHostVOS = Q.New(MonitorHostVO.class)
                .eq(MonitorHostVO_.uuid, hostUuids.get(0))
                .list();

        if (monitorHostVOS.isEmpty())
            return null;

        return monitorHostVOS.get(0);
    }

    /***
     * 按节点、类型查找监控主机
     * @param nodeUuid
     * @param monitorType
     * @return
     */
    private MonitorHostVO getMonitorHostByNodeAndType(String nodeUuid, MonitorType monitorType) {
        List<MonitorHostVO> hosts = Q.New(MonitorHostVO.class)
                .eq(MonitorHostVO_.nodeUuid, nodeUuid)
                .eq(MonitorHostVO_.monitorType, monitorType)
                .eq(MonitorHostVO_.state, HostState.Enabled)
                .eq(MonitorHostVO_.status, HostStatus.Connected)
                .list();

        if (hosts.isEmpty())
            throw new IllegalArgumentException(String.format("Failed to get monitor by node %s and monitor type %s!"
                    , nodeUuid, monitorType));

        return hosts.get(0);
    }

    /**
     * 按nodeUuid与tunnelUuid查询监控通道
     *
     * @param
     * @return
     */
    public TunnelMonitorVO getTunnelMonitorByNodeAndTunnel(String nodeUuid, String tunnelUuid) {

        TunnelMonitorVO tunnelMonitorVO = null;

        List<PhysicalSwitchVO> physicalSwitchVOS = Q.New(PhysicalSwitchVO.class)
                .eq(PhysicalSwitchVO_.nodeUuid, nodeUuid).list();
        for (PhysicalSwitchVO physicalSwitchVO : physicalSwitchVOS) {
            List<HostSwitchMonitorVO> hostSwitchMonitorVOS = Q.New(HostSwitchMonitorVO.class)
                    .eq(HostSwitchMonitorVO_.physicalSwitchUuid, physicalSwitchVO.getUuid()).list();

            if (!hostSwitchMonitorVOS.isEmpty()) {
                tunnelMonitorVO = Q.New(TunnelMonitorVO.class)
                        .eq(TunnelMonitorVO_.hostUuid, hostSwitchMonitorVOS.get(0).getHostUuid())
                        .eq(TunnelMonitorVO_.tunnelUuid, tunnelUuid).find();
            }

            if (tunnelMonitorVO != null)
                break;
        }

        if (tunnelMonitorVO == null)
            throw new ApiMessageInterceptionException(argerr("No Tunnel Monitor under tunnel: %s node:%s"
                    , tunnelUuid, nodeUuid));

        return tunnelMonitorVO;
    }

    /**
     * 按switchPortUuid查询端点监控信息
     *
     * @param
     * @return
     */
    public TunnelMonitorVO getExistTunnelMonitorByTunnelSwitchPost(String switchPortUuid) {

        MonitorHostVO monitorHostVO = getHostBySwitchPort(switchPortUuid);

        List<TunnelMonitorVO> tunnelMonitorVO = Q.New(TunnelMonitorVO.class)
                .eq(TunnelMonitorVO_.hostUuid, monitorHostVO.getUuid())
                .list();

        if (tunnelMonitorVO.isEmpty())
            throw new IllegalArgumentException(String.format("failed to get tunnel monitor by host %s"
                    , monitorHostVO.getUuid()));

        return tunnelMonitorVO.get(0);
    }

    public HostSwitchMonitorVO getHostSwitchMonitorByHostUuid(String hostUuid, String physicalSwitchUuid) {
        List<HostSwitchMonitorVO> vos = Q.New(HostSwitchMonitorVO.class)
                .eq(HostSwitchMonitorVO_.hostUuid, hostUuid)
                .eq(HostSwitchMonitorVO_.physicalSwitchUuid, physicalSwitchUuid)
                .list();

        if (vos.isEmpty())
            throw new IllegalArgumentException(String.format("Failed to get host switch ref by host %s!", hostUuid));


        return vos.get(0);
    }

    /***
     * 按tunnelUuid查询监控通道
     * @param tunnelUuid
     * @return
     */
    public List<TunnelMonitorVO> getTunnelMonitorByTunnel(String tunnelUuid) {
        return Q.New(TunnelMonitorVO.class)
                .eq(TunnelMonitorVO_.tunnelUuid, tunnelUuid)
                .list();
    }

    public TunnelSwitchPortVO getTunnelSwitchPortByUuid(String uuid) {
        return Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.uuid, uuid)
                .find();
    }

    /***
     * 按tunnelSwitchPortUuid查询监控通道
     * @return
     */
    public TunnelMonitorVO getTunnelMonitorByTunnelSwitchPort(String tunnelSwitchPortUuid) {
        return Q.New(TunnelMonitorVO.class)
                .eq(TunnelMonitorVO_.tunnelSwitchPortUuid, tunnelSwitchPortUuid)
                .find();
    }

    /***
     * monitor获取tunnel对应的两端SwitchPort（只取A/Z）
     * @param tunnelId
     * @return List<TunnelSwitchPortVO
     */
    public List<TunnelSwitchPortVO> getMonitorTunnelSwitchPortByTunnelId(String tunnelId) {
        Set sortTags = new HashSet();
        sortTags.add("A");
        sortTags.add("Z");
        List<TunnelSwitchPortVO> portVOS = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, tunnelId)
                .in(TunnelSwitchPortVO_.sortTag, sortTags)
                .list();

        return portVOS;
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if (msg instanceof APICreateSpeedRecordsMsg) {
            validate((APICreateSpeedRecordsMsg) msg);
        } else if (msg instanceof APICreateSpeedTestTunnelMsg) {
            validate((APICreateSpeedTestTunnelMsg) msg);
        }

        return msg;
    }

    private void validate(APICreateSpeedRecordsMsg msg) {
        //测速tunnel是否有未完成的测速
        List<SpeedRecordsVO> vos = Q.New(SpeedRecordsVO.class)
                .eq(SpeedRecordsVO_.tunnelUuid, msg.getTunnelUuid())
                .eq(SpeedRecordsVO_.status, SpeedRecordStatus.TESTING)
                .list();

        if (vos.size() > 0) {
            long retryTime = TimeUnit.SECONDS.toMinutes(CoreGlobalProperty.RESET_SPEEDRECORD_INTERVAL);
            throw new ApiMessageInterceptionException(
                    argerr("Uncompleted test records exists, please retry %s miniters later!", retryTime));
        }
    }

    private void validate(APICreateSpeedTestTunnelMsg msg) {
        SpeedTestTunnelVO vo = Q.New(SpeedTestTunnelVO.class)
                .eq(SpeedTestTunnelVO_.tunnelUuid, msg.getTunnelUuid())
                .find();

        if (vo != null)
            throw new ApiMessageInterceptionException(argerr("Tunnel already exsited!"));

        TunnelVO tunnelVO = dbf.findByUuid(msg.getTunnelUuid(), TunnelVO.class);
        if (tunnelVO.getState() != TunnelState.Enabled)
            throw new ApiMessageInterceptionException(argerr("Tunnel %s is not enabled!", tunnelVO.getName()));

        if (tunnelVO.getMonitorState() != TunnelMonitorState.Enabled)
            throw new ApiMessageInterceptionException(argerr("Tunnel %s monitor state is not enabled!"
                    , tunnelVO.getName()));
    }

    /**
     * 验证监控主机IP合法性
     *
     * @param hostIp：监控主机IP
     */
    private void validateHostIp(String hostIp) {
        String ip = "";
        int port = -1;

        String[] hostAddress = hostIp.split(":");
        ip = hostAddress[0];
        try {
            port = Integer.parseInt(hostAddress[1]);
            if (!NetworkUtils.isLegalPort(port)) {
                throw new ApiMessageInterceptionException(argerr("Illegal host port %s！", port));
            }
        } catch (Exception e) {
            throw new ApiMessageInterceptionException(argerr("Illegal host port %s！", port));
        }

        if (!NetworkUtils.isIpv4Address(ip))
            throw new ApiMessageInterceptionException(argerr("Illegal host IP %s！", ip));
    }

    @Override
    public boolean start() {
        resetSpeedRecordStatus();

        restf.registerSyncHttpCallHandler("FalconTunnel", MonitorAgentCommands.FalconGetTunnelCommand.class,
                cmd -> {
                    MonitorAgentCommands.FalconResponse falconResponse = new MonitorAgentCommands.FalconResponse();
                    try {
                        List<EndpointTunnelsInventory> tunnels = getSharePointTunnels(cmd);

                        falconResponse.setSuccess(true);
                        falconResponse.setMsg("success");
                        falconResponse.setInventories(tunnels);
                    } catch (Exception e) {
                        falconResponse.setSuccess(false);
                        falconResponse.setMsg(String.format("[FalconTunnel] Get tunnels failed! " +
                                "tunnelUuid:  %s, Error: %s", cmd.getTunnelUuid(), e.getMessage()));
                        logger.error(falconResponse.getMsg());
                    }

                    return JSONObjectUtil.toJsonString(falconResponse);
                });

        restf.registerSyncHttpCallHandler("MONITOR/ICMPINFO", HashMap.class,
                map -> {
                    MonitorAgentCommands.RestResponse restResponse = new MonitorAgentCommands.RestResponse();
                    try {
                        List<MonitorAgentCommands.AgentIcmp> agentIcmps = getIcmpsByMonitorHostIp(map.get("monitorHostIp").toString());

                        if (agentIcmps == null || agentIcmps.isEmpty()) {
                            restResponse.setSuccess(false);
                            restResponse.setMsg("no data exist!");
                        } else {
                            restResponse.setSuccess(true);
                            restResponse.setMsg(JSONObjectUtil.toJsonString(agentIcmps));
                        }
                    } catch (Exception e) {
                        restResponse.setSuccess(false);
                        restResponse.setMsg(String.format("[ICMPINFO] failed to get icmp info by monitor host IP %s! " +
                                "Error: %s", map.get("monitorHostIp").toString(), e.getMessage()));
                        logger.error(restResponse.getMsg());
                    }

                    return JSON.toJSONString(restResponse, SerializerFeature.WriteMapNullValue);
                });

        restf.registerSyncHttpCallHandler("MONITOR/VLANENDPOINT", Object.class,
                object -> {
                    Map resultMap = new HashMap();

                    try {
                        List list = getVlanEndpoints();

                        if (list.isEmpty()) {
                            resultMap.put("success", false);
                            resultMap.put("msg", "no data exist!");
                        } else {
                            resultMap.put("success", true);
                            resultMap.put("msg", list);
                        }
                    } catch (Exception e) {
                        resultMap.put("success", false);
                        resultMap.put("msg", String.format(
                                "[MONITOR/VLANENDPOINT] fail to get vlan_endpoint info! Error: %s", e.getMessage()));
                        logger.error(resultMap.get("msg").toString());
                    }

                    return JSONObjectUtil.toJsonString(resultMap);
                });

        class tsdbResp {
            List msg;
            String success;

            public List getMsg() {
                return msg;
            }

            public void setMsg(List msg) {
                this.msg = msg;
            }

            public String getSuccess() {
                return success;
            }

            public void setSuccess(String success) {
                this.success = success;
            }
        }

        return true;
    }

    private void resetSpeedRecordStatus() {
        resetSpeedrecordInterval = CoreGlobalProperty.RESET_SPEEDRECORD_INTERVAL;
        expiredSpeedRecordTime = CoreGlobalProperty.EXPIRED_SPEEDRECORD_TIME;
        if (resetSpeedRecordStatusThread != null) {
            resetSpeedRecordStatusThread.cancel(true);
        }

        resetSpeedRecordStatusThread = thdf.submitPeriodicTask(new ResetSpeedRecordStatusThread(), 10);
    }

    private Future<Void> resetSpeedRecordStatusThread = null;
    private int resetSpeedrecordInterval;
    private int expiredSpeedRecordTime;

    @Override
    public void preDeleteHost(HostInventory inventory) throws HostException {

    }

    @Override
    public void beforeDeleteHost(HostInventory inventory) {
        logger.info("BeforeDeleteHost: " + inventory.getUuid());

        // 删除监控主机与物理交换机的关联
        UpdateQuery.New(HostSwitchMonitorVO.class)
                .eq(HostSwitchMonitorVO_.hostUuid, inventory.getUuid())
                .delete();

        // 停止所有连接该监控主机的专线监控
        thdf.chainSubmit(new ChainTask(null) {
            @Override
            public String getSyncSignature() {
                return String.format("Before-Delete-Host - %s", inventory.getUuid());
            }

            @Override
            public void run(final SyncTaskChain chain) {
                logger.info(String.format("[删除监控主机：%s]启动线程-删除所有连接监控主机的专线监控", inventory.getName()));

                List<TunnelMonitorVO> hostTunnelMonitors = Q.New(TunnelMonitorVO.class)
                        .eq(TunnelMonitorVO_.hostUuid, inventory.getUuid())
                        .list();
                for (TunnelMonitorVO tunnelMonitorVO : hostTunnelMonitors) {
                    TunnelVO tunnelVO = dbf.findByUuid(tunnelMonitorVO.getTunnelUuid(), TunnelVO.class);
                    if (tunnelVO != null && tunnelVO.getMonitorState() == TunnelMonitorState.Enabled) {
                        updateTunnel(tunnelMonitorVO.getTunnelUuid(), "", TunnelMonitorState.Disabled);
                        UpdateQuery.New(TunnelMonitorVO.class).eq(TunnelMonitorVO_.tunnelUuid, tunnelMonitorVO.getTunnelUuid()).delete();

                        TunnelMonitorJob monitorJob = new TunnelMonitorJob();
                        monitorJob.setTunnelUuid(tunnelMonitorVO.getTunnelUuid());
                        monitorJob.setJobType(MonitorJobType.DELETE);
                        jobf.execute("删除监控主机-删除监控", Platform.getManagementServerId(), monitorJob);
                    }
                }

                chain.next();
            }

            @Override
            public String getName() {
                return getSyncSignature();
            }
        });
    }


    @Override
    public void afterDeleteHost(HostInventory inventory) {

    }

    private class ResetSpeedRecordStatusThread implements PeriodicTask {

        @Override
        public TimeUnit getTimeUnit() {
            return TimeUnit.SECONDS;
        }

        @Override
        public long getInterval() {
            return TimeUnit.SECONDS.toSeconds(resetSpeedrecordInterval);
        }

        @Override
        public String getName() {
            return "reset-speedrecord-status-" + Platform.getManagementServerId();
        }

        private long getExpiredTime(Date createDate, int duration) {
            return createDate.getTime() + (expiredSpeedRecordTime + duration) * 1000;
        }

        @Override
        public void run() {
            try {
                logger.info(LocalTime.now() + ": 重置测速纪录状态");
                List<SpeedRecordsVO> list = Q.New(SpeedRecordsVO.class)
                        .eq(SpeedRecordsVO_.status, SpeedRecordStatus.TESTING)
                        .list();

                if (!list.isEmpty()) {
                    long currentTime = System.currentTimeMillis();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    for (SpeedRecordsVO vo : list) {
                        long expiredTime = getExpiredTime(vo.getCreateDate(), vo.getDuration());
                        String d = format.format(expiredTime);
                        String c = format.format(currentTime);

                        if (System.currentTimeMillis() > expiredTime) {
                            vo.setStatus(SpeedRecordStatus.FAILURE);
                            dbf.update(vo);
                        }
                    }
                }
            } catch (Throwable t) {
                logger.warn("unhandled exception!");
            }
        }
    }

    /***
     * 按物理交换机mIP与vlan查找所有共点专线
     * @param cmd：查询条件
     * @return：共点专线清单
     */
    private List<EndpointTunnelsInventory> getSharePointTunnels(MonitorAgentCommands.FalconGetTunnelCommand cmd) {
        List<MonitorAgentCommands.EndpointTunnel> endpointTunnels = new ArrayList<>();

        TunnelVO cmdTunnel = dbf.findByUuid(cmd.getTunnelUuid(), TunnelVO.class);

        List<TunnelVO> tunnelVOS = Q.New(TunnelVO.class)
                .eq(TunnelVO_.vsi, cmdTunnel.getVsi())
                .eq(TunnelVO_.state, TunnelState.Enabled)
                .list();

        TunnelBase tunnelBase = new TunnelBase();
        for (TunnelVO tunnelVO : tunnelVOS) {
            MonitorAgentCommands.EndpointTunnel endpointTunnel = new MonitorAgentCommands.EndpointTunnel();

            if (tunnelBase.isSharePoint(cmdTunnel, tunnelVO)) {
                for (TunnelSwitchPortVO tunnelSwitchPort : tunnelVO.getTunnelSwitchPortVOS()) {
                    if (tunnelSwitchPort.getSortTag().equals(InterfaceType.A.toString())) {
                        endpointTunnel.setNodeA(tunnelSwitchPort.getEndpointVO().getNodeVO().getName());
                        endpointTunnel.setEndpoingAMip(getPhysicalSwitchBySwitchPort(
                                tunnelSwitchPort.getSwitchPortUuid()).getmIP());
                        endpointTunnel.setEndpointAVlan(tunnelSwitchPort.getVlan());
                    } else if (tunnelSwitchPort.getSortTag().equals(InterfaceType.Z.toString())) {
                        endpointTunnel.setNodeZ(tunnelSwitchPort.getEndpointVO().getNodeVO().getName());
                        endpointTunnel.setEndpoingZMip(getPhysicalSwitchBySwitchPort(
                                tunnelSwitchPort.getSwitchPortUuid()).getmIP());
                        endpointTunnel.setEndpointZVlan(tunnelSwitchPort.getVlan());
                    }
                }

                endpointTunnel.setTunnelUuid(tunnelVO.getUuid());
                endpointTunnel.setTunnelName(tunnelVO.getName());
                endpointTunnel.setBandwidth(tunnelVO.getBandwidth());
                endpointTunnel.setAccountUuid(tunnelVO.getOwnerAccountUuid());

                endpointTunnels.add(endpointTunnel);
            }
        }

        return EndpointTunnelsInventory.valueOf(endpointTunnels);
    }

    /***
     * 按监控机ip获取所有icmp数据
     * -
     * @param monitorHostIp
     * @return
     */
    private List<MonitorAgentCommands.AgentIcmp> getIcmpsByMonitorHostIp(String monitorHostIp) {
        List<MonitorAgentCommands.AgentIcmp> icmps = new ArrayList<>();
        List<MonitorHostVO> monitorHostVOS = Q.New(MonitorHostVO.class)
                .eq(MonitorHostVO_.hostIp, monitorHostIp)
                .list();
        if (monitorHostVOS.isEmpty())
            return null;

        List<TunnelMonitorVO> hostTunnelMonitorVOS = Q.New(TunnelMonitorVO.class)
                .eq(TunnelMonitorVO_.hostUuid, monitorHostVOS.get(0).getUuid())
                .list();
        if (hostTunnelMonitorVOS.isEmpty())
            return null;

        for (TunnelMonitorVO hostTunnelMonitorVO : hostTunnelMonitorVOS) {
            TunnelVO tunnelVO = dbf.findByUuid(hostTunnelMonitorVO.getTunnelUuid(), TunnelVO.class);
            if (tunnelVO != null
                    && tunnelVO.getMonitorState() == TunnelMonitorState.Enabled
                    && tunnelVO.getState() == TunnelState.Enabled) {
                MonitorAgentCommands.AgentIcmp agentIcmp = getAgentIcmp(hostTunnelMonitorVO);
                if (agentIcmp != null)
                    icmps.add(agentIcmp);
            }
        }

        return icmps;
    }

    /***
     * OpenTSDB 聚合程序查询vlan、交换机ip映射列表
     * @return
     */
    private List getVlanEndpoints() {
        List list = new ArrayList();

        List<TunnelVO> tunnelVOS = Q.New(TunnelVO.class)
                .eq(TunnelVO_.state, TunnelState.Enabled)
                .eq(TunnelVO_.monitorState, TunnelMonitorState.Enabled)
                .list();

        for (TunnelVO tunnelVO : tunnelVOS) {
            for (TunnelSwitchPortVO tunnelSwitchPortVO : tunnelVO.getTunnelSwitchPortVOS()) {
                List subList = new ArrayList();

                PhysicalSwitchVO physicalSwitchVO = getPhysicalSwitchBySwitchPort(tunnelSwitchPortVO.getSwitchPortUuid());

                subList.add(tunnelSwitchPortVO.getVlan());
                subList.add(physicalSwitchVO.getmIP());

                list.add(subList);
            }
        }

        return list;
    }

    @Override
    public boolean stop() {
        return true;
    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(MonitorConstant.SERVICE_ID);
    }
}
