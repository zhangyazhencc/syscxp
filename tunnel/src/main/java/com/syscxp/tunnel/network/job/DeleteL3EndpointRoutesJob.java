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
import com.syscxp.header.tunnel.network.DeleteL3RouteMsg;
import com.syscxp.header.tunnel.network.L3RouteVO;
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
public class DeleteL3EndpointRoutesJob implements Job {
    private static final CLogger logger = Utils.getLogger(DeleteL3EndpointRoutesJob.class);

    @JobContext
    private String l3RouteUuid;

    @Autowired
    private ErrorFacade errf;

    @Autowired
    private DatabaseFacade dbf;

    @Autowired
    private CloudBus bus;

    @Override
    public void run(ReturnValueCompletion<Object> completion) {

        try {
            if(dbf.isExist(l3RouteUuid, L3RouteVO.class)){
                logger.info("开始执行删除L3路由 JOB【控制器下发删除L3Route】");

                L3RouteVO vo = dbf.findByUuid(l3RouteUuid,L3RouteVO.class);
                //创建任务
                TaskResourceVO taskResourceVO = new L3NetworkBase().newTaskResourceVO(vo, TaskType.DeleteL3EndpointRoutes);

                DeleteL3RouteMsg deleteL3RouteMsg = new DeleteL3RouteMsg();
                deleteL3RouteMsg.setL3RouteUuid(vo.getUuid());
                deleteL3RouteMsg.setTaskUuid(taskResourceVO.getUuid());
                bus.makeLocalServiceId(deleteL3RouteMsg, L3NetWorkConstant.SERVICE_ID);
                bus.send(deleteL3RouteMsg, new CloudBusCallBack(null) {
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

    public String getL3RouteUuid() {
        return l3RouteUuid;
    }

    public void setL3RouteUuid(String l3RouteUuid) {
        this.l3RouteUuid = l3RouteUuid;
    }

    @Override
    public String getResourceUuid() {
        return l3RouteUuid;
    }
}
