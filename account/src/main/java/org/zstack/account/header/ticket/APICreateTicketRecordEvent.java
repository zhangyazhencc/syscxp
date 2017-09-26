package org.zstack.account.header.ticket;

import org.zstack.header.message.APIEvent;

public class APICreateTicketRecordEvent extends APIEvent {
    private TicketRecordInventory inventory;

    public APICreateTicketRecordEvent(String apiId) {
        super(apiId);
    }

    public APICreateTicketRecordEvent() {
        super(null);
    }

    public TicketRecordInventory getInventory() {
        return inventory;
    }

    public void setInventory(TicketRecordInventory inventory) {
        this.inventory = inventory;
    }
}
