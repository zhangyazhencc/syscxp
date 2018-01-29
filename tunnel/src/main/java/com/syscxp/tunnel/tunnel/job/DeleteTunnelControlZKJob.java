package com.syscxp.tunnel.tunnel.job;

import com.syscxp.core.Platform;
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
 * Create by DCY on 2018/1/18
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
@RestartableJob
@UniqueResourceJob
public class DeleteTunnelControlZKJob implements Job {
    private static final CLogger logger = Utils.getLogger(DeleteTunnelControlZKJob.class);

    @JobContext
    private String tunnelUuid;

    @JobContext
    private String commands;

    @JobContext
    private String accountUuid;

    @Autowired
    private ErrorFacade errf;

    @Autowired
    private DatabaseFacade dbf;

    @Autowired
    private CloudBus bus;

    @Override
    public void run(ReturnValueCompletion<Object> completion) {

        try {

            logger.info("开始执行JOB【控制器下发删除ZK数据】");
            //创建任务
            TaskResourceVO taskResourceVO = new TaskResourceVO();
            taskResourceVO.setUuid(Platform.getUuid());
            taskResourceVO.setAccountUuid(accountUuid);
            taskResourceVO.setResourceUuid(tunnelUuid);
            taskResourceVO.setResourceType(TunnelVO.class.getSimpleName());
            taskResourceVO.setTaskType(TaskType.DeleteZK);
            taskResourceVO.setBody(null);
            taskResourceVO.setResult(null);
            taskResourceVO.setStatus(TaskStatus.Preexecute);
            taskResourceVO = dbf.persistAndRefresh(taskResourceVO);

            DeleteTunnelZKMsg deleteTunnelZKMsg = new DeleteTunnelZKMsg();
            deleteTunnelZKMsg.setTunnelUuid(null);
            deleteTunnelZKMsg.setCommands(commands);
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

    public String getCommands() {
        return commands;
    }

    public void setCommands(String commands) {
        this.commands = commands;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    @Override
    public String getResourceUuid() {
        return tunnelUuid;
    }
}
