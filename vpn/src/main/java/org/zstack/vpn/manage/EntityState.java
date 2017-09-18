package org.zstack.vpn.manage;

import org.zstack.header.exception.CloudRuntimeException;

import java.util.HashMap;
import java.util.Map;

public enum EntityState {
    Enabled,
    Disabled,
    PreMaintenance,
    Maintenance;

    static {
        Enabled.transactions(
                new Transaction(EntityStateEvent.disable, EntityState.Disabled),
                new Transaction(EntityStateEvent.enable, EntityState.Enabled),
                new Transaction(EntityStateEvent.preMaintain, EntityState.PreMaintenance)
        );

        Disabled.transactions(
                new Transaction(EntityStateEvent.disable, EntityState.Disabled),
                new Transaction(EntityStateEvent.enable, EntityState.Enabled),
                new Transaction(EntityStateEvent.preMaintain, EntityState.PreMaintenance)
        );

        PreMaintenance.transactions(
                new Transaction(EntityStateEvent.disable, EntityState.Disabled),
                new Transaction(EntityStateEvent.enable, EntityState.Enabled),
                new Transaction(EntityStateEvent.maintain, EntityState.Maintenance)
        );

        Maintenance.transactions(
                new Transaction(EntityStateEvent.disable, EntityState.Disabled),
                new Transaction(EntityStateEvent.enable, EntityState.Enabled)
        );
    }

    private static class Transaction {
        EntityStateEvent event;
        EntityState nextState;

        private Transaction(EntityStateEvent event, EntityState nextState) {
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

    public EntityState nextState(EntityStateEvent event) {
        Transaction tran = transactionMap.get(event);
        if (tran == null) {
            throw new CloudRuntimeException(String.format("cannot find next state for current state[%s] on transaction event[%s]",
                    this, event));
        }

        return tran.nextState;
    }
}
