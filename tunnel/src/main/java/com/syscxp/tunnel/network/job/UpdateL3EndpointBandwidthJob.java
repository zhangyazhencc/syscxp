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
import com.syscxp.header.tunnel.network.L3EndpointVO;
import com.syscxp.header.tunnel.network.UpdateL3EndpointBandwidthMsg;
import com.syscxp.header.tunnel.tunnel.TaskResourceVO;
import com.syscxp.header.tunnel.tunnel.TaskType;
import com.syscxp.tunnel.network.L3NetworkBase;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Create by DCY on 2018/3/14
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
@RestartableJob
@UniqueResourceJob
public class UpdateL3EndpointBandwidthJob implements Job {
    private static final CLogger logger = Utils.getLogger(UpdateL3EndpointBandwidthJob.class);

    @JobContext
    private String l3EndpointUuid;

    @Autowired
    private ErrorFacade errf;

    @Autowired
    private DatabaseFacade dbf;

    @Autowired
    private CloudBus bus;

    @Override
    public void run(ReturnValueCompletion<Object> completion) {

        try {
            if(dbf.isExist(l3EndpointUuid, L3EndpointVO.class)){
                logger.info("开始执行修改L3带宽 JOB【控制器下发修改L3 带宽】");

                L3EndpointVO vo = dbf.findByUuid(l3EndpointUuid,L3EndpointVO.class);
                //创建任务
                TaskResourceVO taskResourceVO = new L3NetworkBase().newTaskResourceVO(vo, TaskType.UpdateL3EndpointBandwidth);

                UpdateL3EndpointBandwidthMsg updateL3EndpointBandwidthMsg = new UpdateL3EndpointBandwidthMsg();
                updateL3EndpointBandwidthMsg.setL3EndpointUuid(vo.getUuid());
                updateL3EndpointBandwidthMsg.setTaskUuid(taskResourceVO.getUuid());
                bus.makeLocalServiceId(updateL3EndpointBandwidthMsg, L3NetWorkConstant.SERVICE_ID);
                bus.send(updateL3EndpointBandwidthMsg, new CloudBusCallBack(null) {
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
                completion.success(null);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);

            completion.fail(errf.throwableToInternalError(e));
        }

    }

    public String getL3EndpointUuid() {
        return l3EndpointUuid;
    }

    public void setL3EndpointUuid(String l3EndpointUuid) {
        this.l3EndpointUuid = l3EndpointUuid;
    }

    @Override
    public String getResourceUuid() {
        return l3EndpointUuid;
    }
}
