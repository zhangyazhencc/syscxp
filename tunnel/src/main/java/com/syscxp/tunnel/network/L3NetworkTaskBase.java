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

                    logger.info("开通L3连接点失败，创建任务：EnabledOrDisabledL3EndPointJob");
                    EnabledOrDisabledL3EndPointJob job = new EnabledOrDisabledL3EndPointJob();
                    job.setL3EndPointUuid(vo.getUuid());
                    job.setJobType(L3EndpointState.Enabled);
                    jobf.execute("开通L3连接点-控制器下发", Platform.getManagementServerId(), job);

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


}

