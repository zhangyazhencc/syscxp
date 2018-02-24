package com.syscxp.vpn.job;

import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.CloudBusCallBack;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.job.*;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.message.MessageReply;
import com.syscxp.header.vpn.agent.DeleteVpnMsg;
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
public class DeleteVpnJob implements Job {
    private static final CLogger LOGGER = Utils.getLogger(DeleteVpnJob.class);

    @JobContext
    private String vpnUuid;
    @Autowired
    private CloudBus bus;
    @Autowired
    private ErrorFacade errf;
    @JobContext
    private boolean deleteRenew;

    @Override
    public void run(ReturnValueCompletion<Object> completion) {

        try {
            LOGGER.info("开始执行JOB【Delete Vpn】");
            DeleteVpnMsg deleteVpnMsg = new DeleteVpnMsg();
            deleteVpnMsg.setVpnUuid(vpnUuid);
            deleteVpnMsg.setDeleteRenew(deleteRenew);
            bus.makeLocalServiceId(deleteVpnMsg, VpnConstant.SERVICE_ID);
            bus.send(deleteVpnMsg, new CloudBusCallBack(completion) {
                @Override
                public void run(MessageReply reply) {
                    if (reply.isSuccess()) {
                        LOGGER.debug(String.format("VPN[UUID:%s]删除成功", vpnUuid));
                        completion.success(null);
                    } else {
                        LOGGER.debug(String.format("VPN[UUID:%s]删除失败", vpnUuid));
                        completion.fail(reply.getError());
                    }
                }
            });
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            completion.fail(errf.throwableToInternalError(e));
        }

    }

    public boolean isDeleteRenew() {
        return deleteRenew;
    }

    public void setDeleteRenew(boolean deleteRenew) {
        this.deleteRenew = deleteRenew;
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

    public static DeleteVpnJob executeJob(JobQueueFacade jobf, String vpnUuid, boolean deleteRenew){
        DeleteVpnJob job = new DeleteVpnJob();
        job.setVpnUuid(vpnUuid);
        job.setDeleteRenew(deleteRenew);
        jobf.execute("删除VPN", Platform.getManagementServerId(), job);
        return job;
    }
}
