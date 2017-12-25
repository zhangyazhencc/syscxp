package com.syscxp.vpn.job;

import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.CloudBusCallBack;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.job.Job;
import com.syscxp.core.job.JobContext;
import com.syscxp.core.job.RestartableJob;
import com.syscxp.core.job.UniqueResourceJob;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.message.MessageReply;
import com.syscxp.header.vpn.agent.DestroyVpnMsg;
import com.syscxp.header.vpn.vpn.VpnConstant;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * @author wangjie
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
@RestartableJob
@UniqueResourceJob
public class DestroyVpnJob implements Job {
    private static final CLogger LOGGER = Utils.getLogger(DestroyVpnJob.class);

    @JobContext
    private String vpnUuid;
    @Autowired
    private CloudBus bus;

    @Autowired
    private ErrorFacade errf;

    @Override
    public void run(ReturnValueCompletion<Object> completion) {

        try {
            LOGGER.info("开始执行JOB【Destroy Vpn】");
            DestroyVpnMsg destroyVpnMsg = new DestroyVpnMsg();
            destroyVpnMsg.setVpnUuid(vpnUuid);
            bus.makeLocalServiceId(destroyVpnMsg, VpnConstant.SERVICE_ID);
            bus.send(destroyVpnMsg, new CloudBusCallBack(completion) {
                @Override
                public void run(MessageReply reply) {
                    if (reply.isSuccess()) {
                        completion.success(null);
                    } else {
                        completion.fail(reply.getError());
                    }
                }
            });
            completion.success(null);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);

            completion.fail(errf.throwableToInternalError(e));
        }

    }

    public String getVpnUuid() {
        return vpnUuid;
    }

    public void setVpnUuid(String vpnUuid) {
        this.vpnUuid = vpnUuid;
    }

    @Override
    public String getResourceUuid() {
        return vpnUuid;
    }
}
