package com.syscxp.vpn.host;

import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.CloudBusCallBack;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.header.core.Completion;
import com.syscxp.header.core.NoErrorCompletion;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.message.MessageReply;
import com.syscxp.header.vpn.VpnConstant;
import com.syscxp.header.vpn.agent.CheckVpnStatusMsg;
import com.syscxp.header.vpn.agent.CheckVpnStatusReply;
import com.syscxp.header.vpn.host.VpnHostInventory;
import com.syscxp.header.vpn.vpn.VpnStatus;
import com.syscxp.header.vpn.vpn.VpnVO;
import com.syscxp.header.vpn.vpn.VpnVO_;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class VpnSyncPingTask implements VpnHostPingAgentNoFailureExtensionPoint {
    private static final CLogger logger = Utils.getLogger(VpnSyncPingTask.class);

    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private CloudBus bus;
    @Autowired
    private ErrorFacade errf;

    private List<String> getVpnUuids(String hostUuid) {
        return Q.New(VpnVO.class).select(VpnVO_.uuid).eq(VpnVO_.hostUuid, hostUuid).list();
    }

    private void checkState(final String hostUuid, final Completion completion) {
        CheckVpnStatusMsg msg = new CheckVpnStatusMsg();
        msg.setHostUuid(hostUuid);
        List<String> vpnUuids = getVpnUuids(hostUuid);
        msg.setVpnUuids(vpnUuids);
        bus.makeLocalServiceId(msg, VpnConstant.SERVICE_ID);
        bus.send(msg, new CloudBusCallBack(completion) {
            @Override
            public void run(MessageReply reply) {
                if (!reply.isSuccess()) {
                    //TODO
                    logger.warn(String.format("unable to check state of the vpn on the host[uuid:%s], %s." +
                            " Put the vm to Unknown state", hostUuid, reply.getError()));
                    completion.fail(reply.getError());
                    return;
                }

                CheckVpnStatusReply ret = reply.castReply();

                for (String vpnUuid : vpnUuids) {
                    String state = ret.getStates().get(vpnUuid);
                    VpnVO vpn = dbf.findByUuid(vpnUuid, VpnVO.class);
                    if (VpnStatus.Connected.toString().equals(state)) {
                        changVpnSatus(vpn, VpnStatus.Connected);
                    } else if (VpnStatus.Disconnected.toString().equals(state)) {
                        changVpnSatus(vpn, VpnStatus.Disconnected);
                    } else {
                        completion.fail(errf.stringToOperationError(String.format("CheckVpnStatusMsg should only report " +
                                        "states[UP or DOWN], but it reports %s for the vpn[uuid:%s] on the host[uuid:%s]",
                                state, vpn.getUuid(), hostUuid)));
                    }
                }
                completion.success();
            }
        });
    }

    private VpnVO changVpnSatus(VpnVO vpn, VpnStatus next) {
        VpnStatus before = vpn.getStatus();
        if (before == next) {
            return vpn;
        }
        vpn.setStatus(next);
        vpn = dbf.updateAndRefresh(vpn);
        logger.debug(String.format("Vpn %s [uuid:%s] changed status from %s to %s",
                vpn.getName(), vpn.getUuid(), before, next));
        return vpn;
    }


    @Override
    public void vpnPingAgentNoFailure(VpnHostInventory host, NoErrorCompletion completion) {

        checkState(host.getUuid(), new Completion(completion) {
            @Override
            public void success() {
                completion.done();
            }

            @Override
            public void fail(ErrorCode errorCode) {
                //TODO
                logger.warn(String.format("failed to sync vpn states on the host[uuid:%s, name:%s, ip:%s], %s",
                        host.getUuid(), host.getName(), host.getHostIp(), errorCode));
                completion.done();
            }
        });
    }
}
