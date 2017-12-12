package com.syscxp.tunnel.tunnel.job;

import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.job.Job;
import com.syscxp.core.job.JobContext;
import com.syscxp.core.job.RestartableJob;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.rest.APINoSee;
import com.syscxp.tunnel.monitor.MonitorManagerImpl;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Create by DCY on 2017/12/7
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
@RestartableJob
public class StopMonitorJob implements Job {
    @APINoSee
    private static final CLogger logger = Utils.getLogger(StopMonitorJob.class);

    @JobContext
    private String tunnelUuid;

    @APINoSee
    @Autowired
    private ErrorFacade errf;

    @APINoSee
    @Autowired
    private MonitorManagerImpl monitorManager;

    @Override
    public void run(ReturnValueCompletion<Object> completion) {

        try {
            logger.info("开始执行JOB【关闭监控】");
            monitorManager.stopControllerMonitor(tunnelUuid);

            completion.success(null);
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
}
