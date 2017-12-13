package com.syscxp.tunnel.tunnel.job;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.identity.InnerMessageHelper;
import com.syscxp.core.job.Job;
import com.syscxp.core.job.JobContext;
import com.syscxp.core.job.RestartableJob;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.header.alarm.APIUpdateTunnelInfoForFalconMsg;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.message.APIReply;
import com.syscxp.header.message.GsonTransient;
import com.syscxp.header.rest.RESTConstant;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.RestAPIResponse;
import com.syscxp.utils.URLBuilder;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Create by DCY on 2017/12/8
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
@RestartableJob
public class UpdateTunnelInfoForFalconJob implements Job {
    private static final CLogger logger = Utils.getLogger(UpdateTunnelInfoForFalconJob.class);

    @JobContext
    private String tunnelUuid;
    @JobContext
    private Integer switchAVlan;
    @JobContext
    private String switchAIp;
    @JobContext
    private Integer switchBVlan;
    @JobContext
    private String switchBIp;
    @JobContext
    private Long bandwidth;
    @JobContext
    private String accountUuid;

    @Autowired
    private ErrorFacade errf;

    @Autowired
    private RESTFacade restf;

    @Override
    public void run(ReturnValueCompletion<Object> completion) {

        try {
            logger.info("开始执行JOB【策略同步-更新】");

            APIUpdateTunnelInfoForFalconMsg msg = new APIUpdateTunnelInfoForFalconMsg();
            msg.setTunnelUuid(tunnelUuid);
            msg.setBandwidth(bandwidth);
            msg.setSwitchAIp(switchAIp);
            msg.setSwitchAVlan(switchAVlan);
            msg.setSwitchBIp(switchBIp);
            msg.setSwitchBVlan(switchBVlan);
            msg.setAccountUuid(accountUuid);

            String url = URLBuilder.buildUrlFromBase(CoreGlobalProperty.ALARM_SERVER_URL, "/alarm/api");
            InnerMessageHelper.setMD5(msg);

            RestAPIResponse rsp = restf.syncJsonPost(url, RESTApiDecoder.dump(msg), RestAPIResponse.class);
            APIReply reply = (APIReply) RESTApiDecoder.loads(rsp.getResult());

            if (!reply.isSuccess()){
                logger.warn("【策略同步-更新】失败");
                completion.fail(reply.getError());
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

    public Integer getSwitchAVlan() {
        return switchAVlan;
    }

    public void setSwitchAVlan(Integer switchAVlan) {
        this.switchAVlan = switchAVlan;
    }

    public String getSwitchAIp() {
        return switchAIp;
    }

    public void setSwitchAIp(String switchAIp) {
        this.switchAIp = switchAIp;
    }

    public Integer getSwitchBVlan() {
        return switchBVlan;
    }

    public void setSwitchBVlan(Integer switchBVlan) {
        this.switchBVlan = switchBVlan;
    }

    public String getSwitchBIp() {
        return switchBIp;
    }

    public void setSwitchBIp(String switchBIp) {
        this.switchBIp = switchBIp;
    }

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }
}
