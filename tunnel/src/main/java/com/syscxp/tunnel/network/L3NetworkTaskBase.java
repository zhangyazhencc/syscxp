package com.syscxp.tunnel.network;

import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.CloudBusCallBack;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.job.JobQueueFacade;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.message.MessageReply;
import com.syscxp.header.tunnel.L3NetWorkConstant;
import com.syscxp.header.tunnel.network.CreateL3EndpointMsg;
import com.syscxp.header.tunnel.network.L3EndPointInventory;
import com.syscxp.header.tunnel.network.L3EndPointVO;
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
     * 创建L3连接点失败的回滚任务
     * */
    public void taskRollBackCreateTunnel(String l3EndpointUuid){
        logger.info("下发回滚创建L3Endpoint，创建任务：CreateL3EndpointRollBackJob");
        CreateL3EndpointRollBackJob job = new CreateL3EndpointRollBackJob();
        job.setL3EndpointUuid(l3EndpointUuid);
        jobf.execute("创建L3连接点失败-控制器回滚", Platform.getManagementServerId(), job);
    }

    /**
     * 创建L3连接点下发
     * */
    public void taskCreateL3Endpoint(L3EndPointVO vo, ReturnValueCompletion<L3EndPointInventory> completionTask) {
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

                    completionTask.success(L3EndPointInventory.valueOf(dbf.reload(vo)));
                } else {

                    if(reply.getError().getDetails().contains("failed to execute the command and rollback")){
                        logger.info("创建L3连接点下发失败，控制器回滚失败，开始回滚控制器");
                        taskRollBackCreateTunnel(vo.getUuid());
                    }

                    completionTask.fail(reply.getError());
                }
            }
        });
    }

    /**
     * 修改互联IP下发
     * */
    public void taskUpdateL3EndpointIP(String l3EndpointUuid){
        logger.info("修改L3互联IP，创建任务：UpdateL3EndpointIPJob");
        UpdateL3EndpointIPJob job = new UpdateL3EndpointIPJob();
        job.setL3EndpointUuid(l3EndpointUuid);
        jobf.execute("修改L3互联IP-控制器下发", Platform.getManagementServerId(), job);
    }

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
     * 删除L3连接点下发
     * */
    public void taskDeleteL3Endpoint(String l3EndpointUuid){
        logger.info("删除L3连接点，创建任务：DeleteL3EndpointJob");
        DeleteL3EndpointJob job = new DeleteL3EndpointJob();
        job.setL3EndpointUuid(l3EndpointUuid);
        jobf.execute("删除L3连接点-控制器下发", Platform.getManagementServerId(), job);
    }

    /**
     * 添加L3连接点路由下发
     * */
    public void taskAddL3EndpointRoutes(String l3RouteUuid){
        logger.info("添加L3连接点路由，创建任务：AddL3EndpointRoutesJob");
        AddL3EndpointRoutesJob job = new AddL3EndpointRoutesJob();
        job.setL3RouteUuid(l3RouteUuid);
        jobf.execute("添加L3连接点路由-控制器下发", Platform.getManagementServerId(), job);
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
}
