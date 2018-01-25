package com.syscxp.tunnel.tunnel.job;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.identity.InnerMessageHelper;
import com.syscxp.core.job.Job;
import com.syscxp.core.job.JobContext;
import com.syscxp.core.job.RestartableJob;
import com.syscxp.core.job.UniqueResourceJob;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.header.alarm.APIStartResourceAlarmMsg;
import com.syscxp.header.alarm.APIStopResourceAlarmMsg;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.message.APIReply;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.RestAPIResponse;
import com.syscxp.header.tunnel.tunnel.TunnelVO;
import com.syscxp.utils.URLBuilder;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Create by DCY on 2018/1/25
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
@RestartableJob
@UniqueResourceJob
public class StartOrStopResourceAlarmJob implements Job {
    private static final CLogger logger = Utils.getLogger(StartOrStopResourceAlarmJob.class);

    @JobContext
    private String tunnelUuid;

    @JobContext
    private AlarmJobType jobType;

    @Autowired
    private ErrorFacade errf;

    @Autowired
    private RESTFacade restf;

    @Autowired
    private DatabaseFacade dbf;

    @Override
    public void run(ReturnValueCompletion<Object> completion) {

        try {
            if(dbf.isExist(tunnelUuid, TunnelVO.class)){

                if(jobType == AlarmJobType.Start){
                    logger.info("开始执行JOB【开启告警】");

                    APIStartResourceAlarmMsg msg = new APIStartResourceAlarmMsg();
                    msg.setTunnelUuid(tunnelUuid);

                    String url = URLBuilder.buildUrlFromBase(CoreGlobalProperty.ALARM_SERVER_URL, "/alarm/api");
                    InnerMessageHelper.setMD5(msg);

                    RestAPIResponse rsp = restf.syncJsonPost(url, RESTApiDecoder.dump(msg), RestAPIResponse.class);
                    APIReply reply = (APIReply) RESTApiDecoder.loads(rsp.getResult());

                    if (!reply.isSuccess()){
                        logger.warn("【开启告警】失败");
                        completion.fail(reply.getError());
                    }else{
                        completion.success(null);
                    }

                }else{
                    logger.info("开始执行JOB【关闭告警】");

                    APIStopResourceAlarmMsg msg = new APIStopResourceAlarmMsg();
                    msg.setTunnelUuid(tunnelUuid);

                    String url = URLBuilder.buildUrlFromBase(CoreGlobalProperty.ALARM_SERVER_URL, "/alarm/api");
                    InnerMessageHelper.setMD5(msg);

                    RestAPIResponse rsp = restf.syncJsonPost(url, RESTApiDecoder.dump(msg), RestAPIResponse.class);
                    APIReply reply = (APIReply) RESTApiDecoder.loads(rsp.getResult());

                    if (!reply.isSuccess()){
                        logger.warn("【关闭告警】失败");
                        completion.fail(reply.getError());
                    }else{
                        completion.success(null);
                    }
                }

            }else{
                completion.success(null);
            }

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);

            completion.fail(errf.throwableToInternalError(e));
        }

    }

    public AlarmJobType getJobType() {
        return jobType;
    }

    public void setJobType(AlarmJobType jobType) {
        this.jobType = jobType;
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
