package com.syscxp.tunnel.monitor;

import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.job.Job;
import com.syscxp.core.job.JobContext;
import com.syscxp.core.job.RestartableJob;
import com.syscxp.core.job.UniqueResourceJob;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.tunnel.monitor.L3NetworkMonitorVO;
import com.syscxp.header.tunnel.network.L3EndpointVO;
import com.syscxp.tunnel.tunnel.job.MonitorJobType;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Create by DCY on 2017/12/6
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
@RestartableJob
@UniqueResourceJob
public class L3NetworkMonitorJob implements Job {
    private static final CLogger logger = Utils.getLogger(L3NetworkMonitorJob.class);

    @JobContext
    private L3EndpointVO l3EndpointVO;

    @JobContext
    private L3NetworkMonitorVO l3NetworkMonitorVO;

    @JobContext
    private MonitorJobType jobType;

    @Autowired
    private ErrorFacade errf;

    @Autowired
    private L3NetworkMonitorBase l3NetworkMonitor;

    @Override
    public void run(ReturnValueCompletion<Object> completion) {

		try {
		    if(jobType == MonitorJobType.CONTROLLER_START){
                logger.info("开始执行JOB【L3开启控制器监控】");
                l3NetworkMonitor.startControllerMonitor(l3EndpointVO);

                completion.success(null);
            }else if(jobType == MonitorJobType.CONTROLLER_STOP){
                logger.info("开始执行JOB【L3关闭控制器监控】");
                l3NetworkMonitor.stopControllerMonitor(l3EndpointVO);

                completion.success(null);
            }else if(jobType == MonitorJobType.AGENT_ADD_ROUTE){
                logger.info("开始执行JOB【L3监控机添加路由】");
                l3NetworkMonitor.addAgentRoute(l3EndpointVO);

                completion.success(null);
            }else if(jobType == MonitorJobType.AGENT_DELETE_ROUTE){
                logger.info("开始执行JOB【L3监控机删除路由】");
                l3NetworkMonitor.deleteAgentRoute(l3EndpointVO);

                completion.success(null);
            }else if(jobType == MonitorJobType.AGENT_START){
                logger.info("开始执行JOB【L3监控机开启监控】");
                l3NetworkMonitor.startAgentMonitor(l3NetworkMonitorVO);

                completion.success(null);
            }else if(jobType == MonitorJobType.AGENT_STOP){
                logger.info("开始执行JOB【L3监控机关闭监控】");
                l3NetworkMonitor.stopAgentMonitor(l3NetworkMonitorVO);

                completion.success(null);
            }else if(jobType == MonitorJobType.AGENT_MODIFY){
                logger.info("开始执行JOB【L3监控机修改监控】");
                l3NetworkMonitor.updateAgentMonitor(l3NetworkMonitorVO);

                completion.success(null);
            }

		} catch (Exception e) {
			logger.warn(e.getMessage(), e);

            completion.fail(errf.throwableToInternalError(e));
		}

    }

    public L3EndpointVO getL3EndpointVO() {
        return l3EndpointVO;
    }

    public void setL3EndpointVO(L3EndpointVO l3EndpointVO) {
        this.l3EndpointVO = l3EndpointVO;
    }

    public L3NetworkMonitorVO getL3NetworkMonitorVO() {
        return l3NetworkMonitorVO;
    }

    public void setL3NetworkMonitorVO(L3NetworkMonitorVO l3NetworkMonitorVO) {
        this.l3NetworkMonitorVO = l3NetworkMonitorVO;
    }

    public MonitorJobType getJobType() {
        return jobType;
    }

    public void setJobType(MonitorJobType jobType) {
        this.jobType = jobType;
    }

    @Override
    public String getResourceUuid() {
        return l3EndpointVO.getUuid();
    }
}
