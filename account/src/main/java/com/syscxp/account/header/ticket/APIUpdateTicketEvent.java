package com.syscxp.account.header.ticket;

import com.syscxp.header.message.APIEvent;

public class APIUpdateTicketEvent extends APIEvent {
    private TicketInventory inventory;

    public APIUpdateTicketEvent(String apiId) {
        super(apiId);
    }

    public APIUpdateTicketEvent() {
        super(null);
    }

    public TicketInventory getInventory() {
        return inventory;
    }

    public void setInventory(TicketInventory inventory) {
        this.inventory = inventory;
    }
}
