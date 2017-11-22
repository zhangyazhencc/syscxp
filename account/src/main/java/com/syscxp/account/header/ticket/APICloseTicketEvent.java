package com.syscxp.account.header.ticket;

import com.syscxp.header.message.APIEvent;

public class APICloseTicketEvent extends APIEvent {
    private TicketInventory inventory;

    public APICloseTicketEvent(String apiId) {
        super(apiId);
    }

    public APICloseTicketEvent() {
        super(null);
    }

    public TicketInventory getInventory() {
        return inventory;
    }

    public void setInventory(TicketInventory inventory) {
        this.inventory = inventory;
    }
}
