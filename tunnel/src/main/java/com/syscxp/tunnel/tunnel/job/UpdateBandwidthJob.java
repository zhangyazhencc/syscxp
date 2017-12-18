package com.syscxp.tunnel.tunnel.job;

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
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.tunnel.*;
import com.syscxp.tunnel.tunnel.TunnelBase;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Create by DCY on 2017/12/18
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
@RestartableJob
@UniqueResourceJob
public class UpdateBandwidthJob implements Job {
    private static final CLogger logger = Utils.getLogger(UpdateBandwidthJob.class);

    @JobContext
    private String tunnelUuid;

    @Autowired
    private ErrorFacade errf;

    @Autowired
    private DatabaseFacade dbf;

    @Autowired
    private CloudBus bus;

    @Override
    public void run(ReturnValueCompletion<Object> completion) {

        try {
            logger.info("开始执行JOB【修改带宽】");

            TunnelVO vo = dbf.findByUuid(tunnelUuid,TunnelVO.class);
            //创建任务
            TaskResourceVO taskResourceVO = new TunnelBase().newTaskResourceVO(vo, TaskType.ModifyBandwidth);

            ModifyTunnelBandwidthMsg modifyTunnelBandwidthMsg = new ModifyTunnelBandwidthMsg();
            modifyTunnelBandwidthMsg.setTunnelUuid(vo.getUuid());
            modifyTunnelBandwidthMsg.setTaskUuid(taskResourceVO.getUuid());
            bus.makeLocalServiceId(modifyTunnelBandwidthMsg, TunnelConstant.SERVICE_ID);
            bus.send(modifyTunnelBandwidthMsg, new CloudBusCallBack(null) {
                @Override
                public void run(MessageReply reply) {
                    if (reply.isSuccess()) {

                        new TunnelBase().updateTunnelBandwidthJob(vo, "调整专线带宽");
                        completion.success(null);
                    } else {
                        completion.fail(reply.getError());
                    }
                }
            });


        } catch (Exception e) {
            logger.warn(e.getMessage(), e);

            completion.fail(errf.throwableToInternalError(e));
        }

    }

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    @Override
    public String getResourceUuid() {
        return tunnelUuid;
    }
}