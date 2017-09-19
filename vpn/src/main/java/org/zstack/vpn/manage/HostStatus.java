package org.zstack.vpn.manage;

import org.zstack.header.exception.CloudRuntimeException;

import java.util.HashMap;
import java.util.Map;

public enum HostStatus {
    Connecting,
    Connected,
    Disconnected;

    static {
        Connecting.transactions(
                new Transaction(RunningStatusEvent.connecting, Connecting),
                new Transaction(RunningStatusEvent.connected, Connected),
                new Transaction(RunningStatusEvent.disconnected, Disconnected)
        );

        Connected.transactions(
                new Transaction(RunningStatusEvent.connecting, Connecting),
                new Transaction(RunningStatusEvent.disconnected, Disconnected),
                new Transaction(RunningStatusEvent.connected, Connected)
        );

        Disconnected.transactions(
                new Transaction(RunningStatusEvent.connecting, Connecting),
                new Transaction(RunningStatusEvent.connected, Connected),
                new Transaction(RunningStatusEvent.disconnected, Disconnected)
        );
    }

    private static class Transaction {
        RunningStatusEvent event;
        HostStatus nextStatus;

        private Transaction(RunningStatusEvent event, HostStatus nextStatus) {
            this.event = event;
            this.nextStatus = nextStatus;
        }
    }

    private void transactions(Transaction... transactions) {
        for (Transaction tran : transactions) {
            transactionMap.put(tran.event, tran);
        }
    }

    private Map<RunningStatusEvent, Transaction> transactionMap = new HashMap<RunningStatusEvent, Transaction>();

    public HostStatus nextStatus(RunningStatusEvent event) {
        Transaction tran = transactionMap.get(event);
        if (tran == null) {
            throw new CloudRuntimeException(String.format("cannot find next status for current status[%s] on transaction event[%s]",
                    this, event));
        }

        return tran.nextStatus;
    }
}
