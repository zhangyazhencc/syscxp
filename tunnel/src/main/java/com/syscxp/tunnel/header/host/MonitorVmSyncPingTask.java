package com.syscxp.tunnel.header.host;

import com.syscxp.core.notification.N;
import com.syscxp.header.core.Completion;
import com.syscxp.header.errorcode.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.core.timeout.ApiTimeoutManager;
import com.syscxp.header.Component;
import com.syscxp.header.core.NoErrorCompletion;
import com.syscxp.header.core.workflow.Flow;
import com.syscxp.header.core.workflow.FlowTrigger;
import com.syscxp.header.core.workflow.NoRollbackFlow;
import com.syscxp.header.host.*;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;

import java.util.Map;


public class MonitorVmSyncPingTask implements MonitorPingAgentNoFailureExtensionPoint, MonitorHostConnectExtensionPoint,
        HostConnectionReestablishExtensionPoint, HostAfterConnectedExtensionPoint, Component {
    private static final CLogger logger = Utils.getLogger(MonitorVmSyncPingTask.class);

    @Autowired
    private RESTFacade restf;
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private CloudBus bus;
    @Autowired
    private ThreadFacade thdf;
    @Autowired
    private ApiTimeoutManager timeoutMgr;


    @Override
    public void connectionReestablished(HostInventory inv) throws HostException {

    }

    @Override
    public HostType getHostTypeForReestablishExtensionPoint() {
        return HostType.valueOf(MonitorConstant.HOST_TYPE);
    }

    @Override
    public boolean start() {

        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

    @Override
    public void afterHostConnected(final HostInventory host) {
        //syncVm has done the same work, so abandon it
    }

    @Override
    public Flow createHostConnectingFlow(final MonitorHostConnectedContext context) {
        return new NoRollbackFlow() {
            @Override
            public void run(final FlowTrigger trigger, Map data) {
                trigger.next();
            }
        };
    }

    @Override
    public void monitorPingAgentNoFailure(MonitorHostInventory host, NoErrorCompletion completion) {
        if (!MonitorGlobalProperty.AGENT_SYNC_ON_HOST_PING) {
            completion.done();
            return;
        }

        syncAgent(host, new Completion(completion) {
            @Override
            public void success() {
                completion.done();
            }

            @Override
            public void fail(ErrorCode errorCode) {
                N.New(HostVO.class, host.getUuid()).warn_("failed to sync Agent states on the host[uuid:%s, " +
                                "name:%s], %s",
                        host.getUuid(), host.getName(), errorCode);
                completion.done();
            }
        });
    }

    private void syncAgent(MonitorHostInventory host, Completion completion) {

    }
}
