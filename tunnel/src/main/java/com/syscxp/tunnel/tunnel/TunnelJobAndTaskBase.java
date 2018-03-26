package com.syscxp.tunnel.tunnel;

import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.CloudBusCallBack;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.job.JobQueueFacade;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.message.MessageReply;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.tunnel.*;
import com.syscxp.tunnel.tunnel.job.*;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Create by DCY on 2018/1/24
 */
@Configurable(preConstruction = true, dependencyCheck = true, autowire = Autowire.BY_TYPE)
public class TunnelJobAndTaskBase {
    private static final CLogger logger = Utils.getLogger(TunnelJobAndTaskBase.class);

    @Autowired
    private JobQueueFacade jobf;

    @Autowired
    private CloudBus bus;

    @Autowired
    private DatabaseFacade dbf;

    /**
     * 强制删除专线-下发删除ZK数据
     * */
    public void taskDeleteTunnelZK(String tunnelUuid, String accountUuid, String commands){
        logger.info("下发删除ZK数据，创建任务：DeleteTunnelControlZKJob");
        DeleteTunnelControlZKJob job = new DeleteTunnelControlZKJob();
        job.setTunnelUuid(tunnelUuid);
        job.setCommands(commands);
        job.setAccountUuid(accountUuid);
        jobf.execute("强制删除专线-删除ZK数据", Platform.getManagementServerId(), job);
    }

    /**
     * 开通专线仅保存数据-下发保存ZK数据
     * */
    public void taskCreateTunnelZK(String tunnelUuid){
        logger.info("下发保存ZK数据，创建任务：CreateTunnelControlZKJob");
        CreateTunnelControlZKJob job = new CreateTunnelControlZKJob();
        job.setTunnelUuid(tunnelUuid);
        jobf.execute("开通仅保存专线-保存ZK数据", Platform.getManagementServerId(), job);
    }

    /**
     * 专线恢复连接仅保存-下发保存ZK数据
     * */
    public void taskEnableTunnelZK(String tunnelUuid){
        logger.info("下发保存ZK数据，创建任务：EnabledOrDisabledTunnelControlZKJob");
        EnabledOrDisabledTunnelControlZKJob job = new EnabledOrDisabledTunnelControlZKJob();
        job.setTunnelUuid(tunnelUuid);
        job.setJobType(TunnelState.Enabled);
        jobf.execute("恢复连接仅保存专线-保存ZK数据", Platform.getManagementServerId(), job);
    }

    /**
     * 专线断开连接仅保存-下发删除ZK数据
     * */
    public void taskDisableTunnelZK(String tunnelUuid){
        logger.info("下发删除ZK数据，创建任务：EnabledOrDisabledTunnelControlZKJob");
        EnabledOrDisabledTunnelControlZKJob job = new EnabledOrDisabledTunnelControlZKJob();
        job.setTunnelUuid(tunnelUuid);
        job.setJobType(TunnelState.Disabled);
        jobf.execute("断开连接仅保存专线-删除ZK数据", Platform.getManagementServerId(), job);
    }

    /**
     * 专线修改端口或 VLAN-下发修改ZK数据
     * */
    public void taskModifyTunnelPortsZK(String tunnelUuid){
        logger.info("下发修改端口ZK数据，创建任务：ModifyTunnelPortsControlZKJob");
        ModifyTunnelPortsControlZKJob job = new ModifyTunnelPortsControlZKJob();
        job.setTunnelUuid(tunnelUuid);
        jobf.execute("修改端口或VLAN仅保存数据-保存ZK数据", Platform.getManagementServerId(), job);
    }

    /**
     * 创建专线失败的回滚任务
     * */
    public void taskRollBackCreateTunnel(String tunnelUuid){
        logger.info("下发回滚创建专线，创建任务：RollBackCreateTunnelJob");
        RollBackCreateTunnelJob job = new RollBackCreateTunnelJob();
        job.setTunnelUuid(tunnelUuid);
        jobf.execute("创建专线失败-控制器回滚", Platform.getManagementServerId(), job);
    }

    /**
     *删除专线控制器下发
     * */
    public void taskDeleteTunnel(TunnelVO vo) {
        TaskResourceVO taskResourceVO = new TunnelBase().newTaskResourceVO(vo, TaskType.Delete);

        DeleteTunnelMsg deleteTunnelMsg = new DeleteTunnelMsg();
        deleteTunnelMsg.setTunnelUuid(vo.getUuid());
        deleteTunnelMsg.setTaskUuid(taskResourceVO.getUuid());
        bus.makeLocalServiceId(deleteTunnelMsg, TunnelConstant.SERVICE_ID);
        bus.send(deleteTunnelMsg, new CloudBusCallBack(null) {
            @Override
            public void run(MessageReply reply) {
                if (reply.isSuccess()) {
                    deleteTunnelForRelationJob(vo, "删除专线");
                } else {
                    logger.info("下发删除通道失败，创建任务：DeleteTunnelControlJob");
                    DeleteTunnelControlJob job = new DeleteTunnelControlJob();
                    job.setTunnelUuid(vo.getUuid());
                    jobf.execute("删除专线-控制器下发", Platform.getManagementServerId(), job);
                }
            }
        });
    }

    /**
     *恢复连接控制器下发
     * */
    public void taskEnableTunnel(TunnelVO vo,ReturnValueCompletion<TunnelInventory> completionTask) {
        TaskResourceVO taskResourceVO = new TunnelBase().newTaskResourceVO(vo, TaskType.Enabled);

        EnabledTunnelMsg enabledTunnelMsg = new EnabledTunnelMsg();
        enabledTunnelMsg.setTunnelUuid(vo.getUuid());
        enabledTunnelMsg.setTaskUuid(taskResourceVO.getUuid());
        bus.makeLocalServiceId(enabledTunnelMsg, TunnelConstant.SERVICE_ID);
        bus.send(enabledTunnelMsg, new CloudBusCallBack(null) {
            @Override
            public void run(MessageReply reply) {
                if (reply.isSuccess()) {
                    jobf.removeJob(vo.getUuid(), EnabledOrDisabledTunnelControlJob.class);

                    enabledTunnelForRelationJob(vo, "恢复专线连接");
                    completionTask.success(TunnelInventory.valueOf(dbf.reload(vo)));
                } else {

                    logger.info("恢复专线连接失败，创建任务：EnabledOrDisabledTunnelControlJob");
                    EnabledOrDisabledTunnelControlJob job = new EnabledOrDisabledTunnelControlJob();
                    job.setTunnelUuid(vo.getUuid());
                    job.setJobType(TunnelState.Enabled);
                    jobf.execute("恢复专线连接-控制器下发", Platform.getManagementServerId(), job);

                    completionTask.fail(reply.getError());
                }
            }
        });
    }

    /**
     *开通控制器下发
     * */
    public void taskOpenTunnel(TunnelVO vo,ReturnValueCompletion<TunnelInventory> completionTask) {
        TaskResourceVO taskResourceVO = new TunnelBase().newTaskResourceVO(vo, TaskType.Create);

        EnabledTunnelMsg enabledTunnelMsg = new EnabledTunnelMsg();
        enabledTunnelMsg.setTunnelUuid(vo.getUuid());
        enabledTunnelMsg.setTaskUuid(taskResourceVO.getUuid());
        bus.makeLocalServiceId(enabledTunnelMsg, TunnelConstant.SERVICE_ID);
        bus.send(enabledTunnelMsg, new CloudBusCallBack(null) {
            @Override
            public void run(MessageReply reply) {
                if (reply.isSuccess()) {

                    jobf.removeJob(vo.getUuid(), RollBackCreateTunnelJob.class);

                    completionTask.success(TunnelInventory.valueOf(dbf.reload(vo)));
                } else {

                    if(reply.getError().getDetails().contains("failed to execute the command and rollback")){
                        logger.info("开通专线失败，控制器回滚失败，开始回滚控制器.");
                        taskRollBackCreateTunnel(vo.getUuid());
                    }

                    completionTask.fail(reply.getError());
                }
            }
        });
    }

    /**
     * 关闭连接控制器下发
     * */
    public void taskDisableTunnel(TunnelVO vo,ReturnValueCompletion<TunnelInventory> completionTask) {
        TaskResourceVO taskResourceVO = new TunnelBase().newTaskResourceVO(vo, TaskType.Disabled);

        DisabledTunnelMsg disabledTunnelMsg = new DisabledTunnelMsg();
        disabledTunnelMsg.setTunnelUuid(vo.getUuid());
        disabledTunnelMsg.setTaskUuid(taskResourceVO.getUuid());
        bus.makeLocalServiceId(disabledTunnelMsg, TunnelConstant.SERVICE_ID);
        bus.send(disabledTunnelMsg, new CloudBusCallBack(null) {
            @Override
            public void run(MessageReply reply) {
                if (reply.isSuccess()) {
                    jobf.removeJob(vo.getUuid(), EnabledOrDisabledTunnelControlJob.class);

                    disabledTunnelForRelationJob(vo, "关闭专线连接");
                    completionTask.success(TunnelInventory.valueOf(dbf.reload(vo)));
                } else {

                    logger.info("关闭专线连接失败，创建任务：EnabledOrDisabledTunnelControlJob");
                    EnabledOrDisabledTunnelControlJob job = new EnabledOrDisabledTunnelControlJob();
                    job.setTunnelUuid(vo.getUuid());
                    job.setJobType(TunnelState.Disabled);
                    jobf.execute("关闭专线连接-控制器下发", Platform.getManagementServerId(), job);

                    completionTask.fail(reply.getError());
                }
            }
        });
    }

    /**
     * 删除专线的关联任务
     * */
    public void deleteTunnelForRelationJob(TunnelVO vo, String queueName) {
        if (vo.getMonitorState() == TunnelMonitorState.Enabled) {

            logger.info("删除通道成功，并创建任务：TunnelMonitorJob");
            TunnelMonitorJob job = new TunnelMonitorJob();
            job.setTunnelUuid(vo.getUuid());
            job.setJobType(MonitorJobType.CONTROLLER_DELETE);
            jobf.execute(queueName + "-停止监控", Platform.getManagementServerId(), job);
        }
        logger.info("删除通道成功，并创建任务：DeleteResourcePolicyRefJob");
        DeleteResourcePolicyRefJob job2 = new DeleteResourcePolicyRefJob();
        job2.setTunnelUuid(vo.getUuid());
        jobf.execute(queueName + "-策略同步", Platform.getManagementServerId(), job2);

        logger.info("删除通道成功，并创建任务：TerminateAliEdgeRouterJob");
        TerminateAliEdgeRouterJob job4 = new TerminateAliEdgeRouterJob();
        job4.setTunnelUuid(vo.getUuid());
        jobf.execute(queueName + "-中止阿里边界路由器", Platform.getManagementServerId(), job4);
    }

    /**
     * 修改专线带宽的关联任务
     * */
    public void updateTunnelBandwidthForRelationJob(TunnelVO vo, String queueName) {
        TunnelBase tunnelBase = new TunnelBase();

        if (vo.getMonitorState() == TunnelMonitorState.Enabled) {
            logger.info("修改带宽成功，并创建任务：TunnelMonitorJob");
            TunnelMonitorJob job = new TunnelMonitorJob();
            job.setTunnelUuid(vo.getUuid());
            job.setJobType(MonitorJobType.CONTROLLER_MODIFY);
            jobf.execute(queueName + "-更新监控", Platform.getManagementServerId(), job);


        }
        logger.info("修改带宽成功，并创建任务：UpdateTunnelInfoForFalconJob");
        UpdateTunnelInfoForFalconJob job2 = new UpdateTunnelInfoForFalconJob();
        job2.setTunnelUuid(vo.getUuid());
        job2.setBandwidth(vo.getBandwidth());
        TunnelSwitchPortVO tunnelSwitchPortA = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, vo.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "A")
                .find();
        TunnelSwitchPortVO tunnelSwitchPortZ = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, vo.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "Z")
                .find();
        job2.setSwitchAVlan(tunnelSwitchPortA.getVlan());
        job2.setSwitchBVlan(tunnelSwitchPortZ.getVlan());
        job2.setSwitchAIp(tunnelBase.getPhysicalSwitchMip(tunnelSwitchPortA.getSwitchPortUuid()));
        job2.setSwitchBIp(tunnelBase.getPhysicalSwitchMip(tunnelSwitchPortZ.getSwitchPortUuid()));
        job2.setAccountUuid(vo.getOwnerAccountUuid());
        jobf.execute(queueName + "-策略同步", Platform.getManagementServerId(), job2);
    }

    /**
     * 恢复专线连接的关联任务
     * */
    public void enabledTunnelForRelationJob(TunnelVO vo, String queueName) {
        if (vo.getMonitorState() == TunnelMonitorState.Enabled) {
            logger.info("专线恢复连接成功，并创建任务：TunnelMonitorJob");
            TunnelMonitorJob job = new TunnelMonitorJob();
            job.setTunnelUuid(vo.getUuid());
            job.setJobType(MonitorJobType.CONTROLLER_START);
            jobf.execute(queueName + "-开启监控", Platform.getManagementServerId(), job);
        }

        logger.info("专线恢复连接成功，并创建任务：StartOrStopResourceAlarmJob");
        StartOrStopResourceAlarmJob job2 = new StartOrStopResourceAlarmJob();
        job2.setTunnelUuid(vo.getUuid());
        job2.setJobType(AlarmJobType.Start);
        jobf.execute(queueName + "-开启告警", Platform.getManagementServerId(), job2);
    }

    /**
     * 断开专线连接的关联任务
     * */
    public void disabledTunnelForRelationJob(TunnelVO vo, String queueName) {
        if (vo.getMonitorState() == TunnelMonitorState.Enabled) {
            logger.info("专线关闭连接成功，并创建任务：TunnelMonitorJob");
            TunnelMonitorJob job = new TunnelMonitorJob();
            job.setTunnelUuid(vo.getUuid());
            job.setJobType(MonitorJobType.CONTROLLER_STOP);
            jobf.execute(queueName + "-停止监控", Platform.getManagementServerId(), job);
        }

        logger.info("专线关闭连接成功，并创建任务：StartOrStopResourceAlarmJob");
        StartOrStopResourceAlarmJob job2 = new StartOrStopResourceAlarmJob();
        job2.setTunnelUuid(vo.getUuid());
        job2.setJobType(AlarmJobType.Stop);
        jobf.execute(queueName + "-关闭告警", Platform.getManagementServerId(), job2);
    }
}
