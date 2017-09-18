package org.zstack.vpn.manage;

import org.zstack.header.exception.CloudRuntimeException;

import java.util.HashMap;
import java.util.Map;

public enum HostState {
    Enabled,
    Disabled,
    PreMaintenance,
    Maintenance;

    static {
        Enabled.transactions(
                new Transaction(EntityStateEvent.disable, HostState.Disabled),
                new Transaction(EntityStateEvent.enable, HostState.Enabled),
                new Transaction(EntityStateEvent.preMaintain, HostState.PreMaintenance)
        );

        Disabled.transactions(
                new Transaction(EntityStateEvent.disable, HostState.Disabled),
                new Transaction(EntityStateEvent.enable, HostState.Enabled),
                new Transaction(EntityStateEvent.preMaintain, HostState.PreMaintenance)
        );

        PreMaintenance.transactions(
                new Transaction(EntityStateEvent.disable, HostState.Disabled),
                new Transaction(EntityStateEvent.enable, HostState.Enabled),
                new Transaction(EntityStateEvent.maintain, HostState.Maintenance)
        );

        Maintenance.transactions(
                new Transaction(EntityStateEvent.disable, HostState.Disabled),
                new Transaction(EntityStateEvent.enable, HostState.Enabled)
        );
    }

    private static class Transaction {
        EntityStateEvent event;
        HostState nextState;

        private Transaction(EntityStateEvent event, HostState nextState) {
            this.event = event;
            this.nextState = nextState;
        }
    }

    private void transactions(Transaction... transactions) {
        for (Transaction tran : transactions) {
            transactionMap.put(tran.event, tran);
        }
    }

    private Map<EntityStateEvent, Transaction> transactionMap = new HashMap<EntityStateEvent, Transaction>();

    public HostState nextState(EntityStateEvent event) {
        Transaction tran = transactionMap.get(event);
        if (tran == null) {
            throw new CloudRuntimeException(String.format("cannot find next state for current state[%s] on transaction event[%s]",
                    this, event));
        }

        return tran.nextState;
    }
}
