package com.syscxp.tunnel.network;

import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.CloudBusCallBack;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.job.JobQueueFacade;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.message.MessageReply;
import com.syscxp.header.tunnel.L3NetWorkConstant;
import com.syscxp.header.tunnel.network.*;
import com.syscxp.header.tunnel.tunnel.*;
import com.syscxp.tunnel.network.job.*;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Create by DCY on 2018/3/14
 */
@Configurable(preConstruction = true, dependencyCheck = true, autowire = Autowire.BY_TYPE)
public class L3NetworkTaskBase {
    private static final CLogger logger = Utils.getLogger(L3NetworkTaskBase.class);

    @Autowired
    private JobQueueFacade jobf;

    @Autowired
    private CloudBus bus;

    @Autowired
    private DatabaseFacade dbf;

    /**
     * 修改L3带宽下发
     * */
    public void taskUpdateL3EndpointBandwidth(String l3EndpointUuid){
        logger.info("修改L3带宽，创建任务：UpdateL3EndpointBandwidthJob");
        UpdateL3EndpointBandwidthJob job = new UpdateL3EndpointBandwidthJob();
        job.setL3EndpointUuid(l3EndpointUuid);
        jobf.execute("修改L3带宽-控制器下发", Platform.getManagementServerId(), job);
    }

    /**
     * 删除L3连接点路由下发
     * */
    public void taskDeleteL3EndpointRoutes(String l3RouteUuid){
        logger.info("删除L3连接点路由，创建任务：DeleteL3EndpointRoutesJob");
        DeleteL3EndpointRoutesJob job = new DeleteL3EndpointRoutesJob();
        job.setL3RouteUuid(l3RouteUuid);
        jobf.execute("删除L3连接点路由-控制器下发", Platform.getManagementServerId(), job);
    }


    /**
     * 开通连接点控制器下发
     * */
    public void taskEnableL3EndPoint(L3EndPointVO vo, ReturnValueCompletion<L3EndPointInventory> completionTask){
        L3NetworkBase l3NetworkBase = new L3NetworkBase();
        TaskResourceVO taskResourceVO = l3NetworkBase.newTaskResourceVO(vo, TaskType.CreateL3Endpoint);

        CreateL3EndpointMsg createL3EndpointMsg = new CreateL3EndpointMsg();
        createL3EndpointMsg.setL3EndpointUuid(vo.getUuid());
        createL3EndpointMsg.setTaskUuid(taskResourceVO.getUuid());

        bus.makeLocalServiceId(createL3EndpointMsg, L3NetWorkConstant.SERVICE_ID);
        bus.send(createL3EndpointMsg, new CloudBusCallBack(null) {
            @Override
            public void run(MessageReply reply) {
                if (reply.isSuccess()) {
                    jobf.removeJob(vo.getUuid(), EnabledOrDisabledL3EndPointJob.class);

                    completionTask.success(L3EndPointInventory.valueOf(dbf.reload(vo)));
                } else {

                    if(reply.getError().getDetails().contains("failed to execute the command and rollback")){
                        logger.info("开通L3连接点失败，控制器回滚失败，开始回滚控制器.");

                        EnabledOrDisabledL3EndPointJob job = new EnabledOrDisabledL3EndPointJob();
                        job.setL3EndPointUuid(vo.getUuid());
                        job.setJobType(L3EndpointState.Disabled);
                        jobf.execute("中止L3连接点-控制器下发", Platform.getManagementServerId(), job);
                    }

                    completionTask.fail(reply.getError());
                }
            }
        });
    }

    /**
     * 中止连接点控制器下发
     * */
    public void taskDisableL3EndPoint(L3EndPointVO vo, ReturnValueCompletion<L3EndPointInventory> completionTask){
        TaskResourceVO taskResourceVO = new L3NetworkBase().newTaskResourceVO(vo, TaskType.DeleteL3Endpoint);

        DeleteL3EndPointMsg deleteL3EndPointMsg = new DeleteL3EndPointMsg();
        deleteL3EndPointMsg.setL3EndpointUuid(vo.getUuid());
        deleteL3EndPointMsg.setTaskUuid(taskResourceVO.getUuid());
        bus.makeLocalServiceId(deleteL3EndPointMsg, L3NetWorkConstant.SERVICE_ID);
        bus.send(deleteL3EndPointMsg, new CloudBusCallBack(null) {
            @Override
            public void run(MessageReply reply) {
                if (reply.isSuccess()) {
                    jobf.removeJob(vo.getUuid(), EnabledOrDisabledL3EndPointJob.class);

                    completionTask.success(L3EndPointInventory.valueOf(dbf.reload(vo)));
                } else {
                    logger.info("中止L3连接点失败，创建任务：EnabledOrDisabledL3EndPointJob");
                    EnabledOrDisabledL3EndPointJob job = new EnabledOrDisabledL3EndPointJob();
                    job.setL3EndPointUuid(vo.getUuid());
                    job.setJobType(L3EndpointState.Disabled);
                    jobf.execute("中止L3连接点-控制器下发", Platform.getManagementServerId(), job);

                    completionTask.fail(reply.getError());
                }
            }
        });
    }

    /**
     * 添加L3连接点路由下发
     * */
    public void taskAddL3EndpointRoutes(L3RouteVO vo, ReturnValueCompletion<L3EndPointInventory> completionTask){
        L3EndPointVO l3EndPointVO = dbf.findByUuid(vo.getL3EndPointUuid(), L3EndPointVO.class);

        TaskResourceVO taskResourceVO = new L3NetworkBase().newTaskResourceVO(vo, TaskType.AddL3EndpointRoutes);

        CreateL3RouteMsg createL3RouteMsg = new CreateL3RouteMsg();
        createL3RouteMsg.setL3RouteUuid(vo.getUuid());
        createL3RouteMsg.setTaskUuid(taskResourceVO.getUuid());
        bus.makeLocalServiceId(createL3RouteMsg, L3NetWorkConstant.SERVICE_ID);
        bus.send(createL3RouteMsg, new CloudBusCallBack(null) {
            @Override
            public void run(MessageReply reply) {
                if (reply.isSuccess()) {

                    completionTask.success(L3EndPointInventory.valueOf(l3EndPointVO));
                } else {
                    dbf.remove(vo);
                    completionTask.fail(reply.getError());
                }
            }
        });
    }
}

