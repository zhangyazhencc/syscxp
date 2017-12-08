package com.syscxp.vpn.host;

import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.CloudBusCallBack;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.header.core.Completion;
import com.syscxp.header.core.NoErrorCompletion;
import com.syscxp.header.core.workflow.Flow;
import com.syscxp.header.core.workflow.FlowTrigger;
import com.syscxp.header.core.workflow.NoRollbackFlow;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.message.MessageReply;
import com.syscxp.header.vpn.VpnConstant;
import com.syscxp.header.vpn.agent.CheckVpnStatusMsg;
import com.syscxp.header.vpn.agent.CheckVpnStatusReply;
import com.syscxp.header.vpn.agent.StartAllMsg;
import com.syscxp.header.vpn.host.VpnHostInventory;
import com.syscxp.header.vpn.vpn.VpnState;
import com.syscxp.header.vpn.vpn.VpnStatus;
import com.syscxp.header.vpn.vpn.VpnVO;
import com.syscxp.header.vpn.vpn.VpnVO_;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VpnSyncPingTask implements VpnHostPingAgentNoFailureExtensionPoint, VpnHostConnectExtensionPoint {
    private static final CLogger logger = Utils.getLogger(VpnSyncPingTask.class);

    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private CloudBus bus;
    @Autowired
    private ErrorFacade errf;

    private List<String> getVpnUuids(String hostUuid) {
        return Q.New(VpnVO.class).select(VpnVO_.uuid).eq(VpnVO_.state, VpnState.Enabled).eq(VpnVO_.hostUuid, hostUuid).list();
    }

    private void checkState(final String hostUuid, final Completion completion) {
        CheckVpnStatusMsg msg = new CheckVpnStatusMsg();
        msg.setNoStatusCheck(true);
        msg.setHostUuid(hostUuid);
        List<String> vpnUuids = getVpnUuids(hostUuid);
        if (vpnUuids.isEmpty())
            return;
        msg.setVpnUuids(vpnUuids);
        bus.makeLocalServiceId(msg, VpnConstant.SERVICE_ID);
        bus.send(msg, new CloudBusCallBack(completion) {
            @Override
            public void run(MessageReply reply) {
                if (!reply.isSuccess()) {
                    //TODO
                    logger.warn(String.format("unable to check status of the vpn on the host[uuid:%s], %s." +
                            " Put the vm to Unknown state", hostUuid, reply.getError()));
                    completion.fail(reply.getError());
                    return;
                }

                CheckVpnStatusReply ret = reply.castReply();

                List<StartAllMsg> msgs = new ArrayList<>();
                for (String vpnUuid : vpnUuids) {
                    String status = ret.getStates().get(vpnUuid);

                    if (VpnStatus.Disconnected.toString().equals(status)) {
                        StartAllMsg vmsg = new StartAllMsg();
                        vmsg.setVpnUuid(vpnUuid);
                        bus.makeLocalServiceId(vmsg, VpnConstant.SERVICE_ID);
                        msgs.add(vmsg);
                    } else if ( VpnStatus.Connected.toString().equals(status)) {
                        changVpnSatus(vpnUuid, VpnStatus.Connected);
                    }  else {
                        completion.fail(errf.stringToOperationError(String.format("CheckVpnStatusMsg should only report " +
                                        "states[UP or DOWN], but it reports %s for the vpn[uuid:%s] on the host[uuid:%s]",
                                status, vpnUuid, hostUuid)));
                        return;
                    }
                }
                bus.send(msgs);
                completion.success();
            }
        });
    }

    private VpnVO changVpnSatus(String vpnUuid, VpnStatus next) {
        VpnVO vpn = dbf.findByUuid(vpnUuid, VpnVO.class);
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

    @Override
    public Flow createHostConnectingFlow(VpnHostConnectedContext context) {
        return new NoRollbackFlow() {
            @Override
            public void run(final FlowTrigger trigger, Map data) {
                checkState(context.getInventory().getUuid(), new Completion(trigger) {
                    String __name__ = "sync-vpn-status";

                    @Override
                    public void success() {
                        trigger.next();
                    }

                    @Override
                    public void fail(ErrorCode errorCode) {
                        trigger.fail(errorCode);
                    }
                });
            }
        };
    }
}
