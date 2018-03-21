package com.syscxp.tunnel.network.job;

import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.CloudBusCallBack;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.job.Job;
import com.syscxp.core.job.JobContext;
import com.syscxp.core.job.RestartableJob;
import com.syscxp.core.job.UniqueResourceJob;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.message.MessageReply;
import com.syscxp.header.tunnel.L3NetWorkConstant;
import com.syscxp.header.tunnel.network.*;
import com.syscxp.header.tunnel.tunnel.TaskResourceVO;
import com.syscxp.header.tunnel.tunnel.TaskType;
import com.syscxp.tunnel.network.L3NetworkBase;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Create by DCY on 2018/3/20
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
@RestartableJob
@UniqueResourceJob
public class EnabledOrDisabledL3EndPointJob implements Job {
    private static final CLogger logger = Utils.getLogger(EnabledOrDisabledL3EndPointJob.class);

    @JobContext
    private String l3EndPointUuid;

    @JobContext
    private L3EndpointState jobType;

    @Autowired
    private ErrorFacade errf;

    @Autowired
    private DatabaseFacade dbf;

    @Autowired
    private CloudBus bus;

    @Override
    public void run(ReturnValueCompletion<Object> completion) {

        try {
            if(dbf.isExist(l3EndPointUuid, L3EndPointVO.class)){
                L3EndPointVO vo = dbf.findByUuid(l3EndPointUuid,L3EndPointVO.class);
                if(jobType != vo.getState()){
                    L3NetworkBase l3NetworkBase = new L3NetworkBase();

                    if(jobType == L3EndpointState.Enabled){
                        logger.info("开始执行JOB【控制器下发开通L3连接点】");

                        TaskResourceVO taskResourceVO = l3NetworkBase.newTaskResourceVO(vo, TaskType.CreateL3Endpoint);

                        CreateL3EndpointMsg createL3EndpointMsg = new CreateL3EndpointMsg();
                        createL3EndpointMsg.setL3EndpointUuid(vo.getUuid());
                        createL3EndpointMsg.setTaskUuid(taskResourceVO.getUuid());

                        bus.makeLocalServiceId(createL3EndpointMsg, L3NetWorkConstant.SERVICE_ID);
                        bus.send(createL3EndpointMsg, new CloudBusCallBack(null) {
                            @Override
                            public void run(MessageReply reply) {
                                if (reply.isSuccess()) {

                                    completion.success(null);
                                } else {

                                    completion.fail(reply.getError());
                                }
                            }
                        });
                    }else{
                        logger.info("开始执行JOB【控制器下发中止L3连接点】");

                        TaskResourceVO taskResourceVO = new L3NetworkBase().newTaskResourceVO(vo, TaskType.DeleteL3Endpoint);

                        DeleteL3EndPointMsg deleteL3EndPointMsg = new DeleteL3EndPointMsg();
                        deleteL3EndPointMsg.setL3EndpointUuid(vo.getUuid());
                        deleteL3EndPointMsg.setTaskUuid(taskResourceVO.getUuid());
                        bus.makeLocalServiceId(deleteL3EndPointMsg, L3NetWorkConstant.SERVICE_ID);
                        bus.send(deleteL3EndPointMsg, new CloudBusCallBack(null) {
                            @Override
                            public void run(MessageReply reply) {
                                if (reply.isSuccess()) {

                                    completion.success(null);
                                } else {
                                    completion.fail(reply.getError());
                                }
                            }
                        });
                    }

                }else{
                    completion.success(null);
                }

            }else{
                completion.success(null);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);

            completion.fail(errf.throwableToInternalError(e));
        }

    }

    public String getL3EndPointUuid() {
        return l3EndPointUuid;
    }

    public void setL3EndPointUuid(String l3EndPointUuid) {
        this.l3EndPointUuid = l3EndPointUuid;
    }

    public L3EndpointState getJobType() {
        return jobType;
    }

    public void setJobType(L3EndpointState jobType) {
        this.jobType = jobType;
    }

    @Override
    public String getResourceUuid() {
        return l3EndPointUuid;
    }
}
