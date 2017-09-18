package org.zstack.vpn.header.vpn;

import org.zstack.header.exception.CloudRuntimeException;
import org.zstack.vpn.header.host.HostStateEvent;

import java.util.HashMap;
import java.util.Map;

public enum VpnState {
    Enabled,
    Disabled,
    PreMaintenance,
    Maintenance;

    static {
        Enabled.transactions(
                new Transaction(HostStateEvent.disable, VpnState.Disabled),
                new Transaction(HostStateEvent.enable, VpnState.Enabled),
                new Transaction(HostStateEvent.preMaintain, VpnState.PreMaintenance)
        );

        Disabled.transactions(
                new Transaction(HostStateEvent.disable, VpnState.Disabled),
                new Transaction(HostStateEvent.enable, VpnState.Enabled),
                new Transaction(HostStateEvent.preMaintain, VpnState.PreMaintenance)
        );

        PreMaintenance.transactions(
                new Transaction(HostStateEvent.disable, VpnState.Disabled),
                new Transaction(HostStateEvent.enable, VpnState.Enabled),
                new Transaction(HostStateEvent.maintain, VpnState.Maintenance)
        );

        Maintenance.transactions(
                new Transaction(HostStateEvent.disable, VpnState.Disabled),
                new Transaction(HostStateEvent.enable, VpnState.Enabled)
        );
    }

    private static class Transaction {
        HostStateEvent event;
        VpnState nextState;

        private Transaction(HostStateEvent event, VpnState nextState) {
            this.event = event;
            this.nextState = nextState;
        }
    }

    private void transactions(Transaction... transactions) {
        for (Transaction tran : transactions) {
            transactionMap.put(tran.event, tran);
        }
    }

    private Map<HostStateEvent, Transaction> transactionMap = new HashMap<HostStateEvent, Transaction>();

    public VpnState nextState(HostStateEvent event) {
        Transaction tran = transactionMap.get(event);
        if (tran == null) {
            throw new CloudRuntimeException(String.format("cannot find next state for current state[%s] on transaction event[%s]",
                    this, event));
        }

        return tran.nextState;
    }
}
