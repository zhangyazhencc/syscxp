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
    private Integer switchA_vlan;
    @JobContext
    private String switchA_ip;
    @JobContext
    private Integer switchB_vlan;
    @JobContext
    private String switchB_ip;
    @JobContext
    private Long bandwidth;
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
            msg.setSwitchAIp(switchA_ip);
            msg.setSwitchAVlan(switchA_vlan);
            msg.setSwitchBIp(switchB_ip);
            msg.setSwitchBVlan(switchB_vlan);

            String url = URLBuilder.buildUrlFromBase(CoreGlobalProperty.ALARM_SERVER_URL, RESTConstant.REST_API_CALL);
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

    public Integer getSwitchA_vlan() {
        return switchA_vlan;
    }

    public void setSwitchA_vlan(Integer switchA_vlan) {
        this.switchA_vlan = switchA_vlan;
    }

    public String getSwitchA_ip() {
        return switchA_ip;
    }

    public void setSwitchA_ip(String switchA_ip) {
        this.switchA_ip = switchA_ip;
    }

    public Integer getSwitchB_vlan() {
        return switchB_vlan;
    }

    public void setSwitchB_vlan(Integer switchB_vlan) {
        this.switchB_vlan = switchB_vlan;
    }

    public String getSwitchB_ip() {
        return switchB_ip;
    }

    public void setSwitchB_ip(String switchB_ip) {
        this.switchB_ip = switchB_ip;
    }

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
    }
}
