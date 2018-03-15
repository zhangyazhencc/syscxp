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
import com.syscxp.header.tunnel.network.DeleteL3EndPointMsg;
import com.syscxp.header.tunnel.network.L3EndPointVO;
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
public class CreateL3EndpointRollBackJob implements Job {
    private static final CLogger logger = Utils.getLogger(CreateL3EndpointRollBackJob.class);

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
            if(dbf.isExist(l3EndpointUuid, L3EndPointVO.class)){
                logger.info("开始执行回滚创建L3 JOB【控制器下发删除L3Endpoint】");

                L3EndPointVO vo = dbf.findByUuid(l3EndpointUuid,L3EndPointVO.class);
                //创建任务
                TaskResourceVO taskResourceVO = new L3NetworkBase().newTaskResourceVO(vo, TaskType.RollBackCreateL3Endpoint);

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
