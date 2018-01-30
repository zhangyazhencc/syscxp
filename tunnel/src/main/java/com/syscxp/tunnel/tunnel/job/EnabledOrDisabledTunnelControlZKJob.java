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
 * Create by DCY on 2018/1/24
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
@RestartableJob
@UniqueResourceJob
public class EnabledOrDisabledTunnelControlZKJob implements Job {
    private static final CLogger logger = Utils.getLogger(EnabledOrDisabledTunnelControlZKJob.class);

    @JobContext
    private String tunnelUuid;

    @JobContext
    private TunnelState jobType;

    @Autowired
    private ErrorFacade errf;

    @Autowired
    private DatabaseFacade dbf;

    @Autowired
    private CloudBus bus;

    @Override
    public void run(ReturnValueCompletion<Object> completion) {

        try {
            if(dbf.isExist(tunnelUuid, TunnelVO.class)){
                TunnelVO vo = dbf.findByUuid(tunnelUuid,TunnelVO.class);

                if(jobType == TunnelState.Enabled){
                    logger.info("开始执行JOB【控制器下发保存ZK数据】");

                    TaskResourceVO taskResourceVO = new TunnelBase().newTaskResourceVO(vo, TaskType.EnabledZK);

                    CreateTunnelZKMsg createTunnelZKMsg = new CreateTunnelZKMsg();
                    createTunnelZKMsg.setTunnelUuid(vo.getUuid());
                    createTunnelZKMsg.setTaskUuid(taskResourceVO.getUuid());
                    bus.makeLocalServiceId(createTunnelZKMsg, TunnelConstant.SERVICE_ID);
                    bus.send(createTunnelZKMsg, new CloudBusCallBack(null) {
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
                    logger.info("开始执行JOB【控制器下发删除ZK数据】");

                    TaskResourceVO taskResourceVO = new TunnelBase().newTaskResourceVO(vo, TaskType.DisabledZK);

                    DeleteTunnelZKMsg deleteTunnelZKMsg = new DeleteTunnelZKMsg();
                    deleteTunnelZKMsg.setTunnelUuid(tunnelUuid);
                    deleteTunnelZKMsg.setCommands(null);
                    deleteTunnelZKMsg.setTaskUuid(taskResourceVO.getUuid());
                    bus.makeLocalServiceId(deleteTunnelZKMsg, TunnelConstant.SERVICE_ID);
                    bus.send(deleteTunnelZKMsg, new CloudBusCallBack(null) {
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

    public TunnelState getJobType() {
        return jobType;
    }

    public void setJobType(TunnelState jobType) {
        this.jobType = jobType;
    }

    @Override
    public String getResourceUuid() {
        return tunnelUuid;
    }
}
