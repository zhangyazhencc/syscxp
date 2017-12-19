package com.syscxp.tunnel.tunnel.job;

import com.syscxp.core.db.Q;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.job.Job;
import com.syscxp.core.job.JobContext;
import com.syscxp.core.job.RestartableJob;
import com.syscxp.core.job.UniqueResourceJob;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.tunnel.aliEdgeRouter.AliEdgeRouterVO;
import com.syscxp.header.tunnel.aliEdgeRouter.AliEdgeRouterVO_;
import com.syscxp.tunnel.aliEdgeRouter.AliEdgeRouterManagerImpl;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.List;

/**
 * Create by DCY on 2017/12/19
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
@RestartableJob
@UniqueResourceJob
public class TerminateAliEdgeRouterJob implements Job {
    private static final CLogger logger = Utils.getLogger(TerminateAliEdgeRouterJob.class);

    @JobContext
    private String tunnelUuid;

    @Autowired
    private ErrorFacade errf;

    @Autowired
    private AliEdgeRouterManagerImpl aliEdgeRouterManager;

    @Override
    public void run(ReturnValueCompletion<Object> completion) {

        try {
            List<AliEdgeRouterVO> aliEdgeRouterVOList = Q.New(AliEdgeRouterVO.class).eq(AliEdgeRouterVO_.tunnelUuid,tunnelUuid).list();
            if(!aliEdgeRouterVOList.isEmpty()){
                logger.info("开始执行JOB【中止阿里边界路由器】");
                for(AliEdgeRouterVO aliEdgeRouterVO : aliEdgeRouterVOList){
                    aliEdgeRouterManager.TerminateAliEdgeRouter(aliEdgeRouterVO.getUuid());
                }

                completion.success(null);
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

    @Override
    public String getResourceUuid() {
        return tunnelUuid;
    }
}
