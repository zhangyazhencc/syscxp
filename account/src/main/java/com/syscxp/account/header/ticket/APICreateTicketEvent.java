package com.syscxp.account.header.ticket;

import com.syscxp.header.message.APIEvent;

public class APICreateTicketEvent extends APIEvent {
    private TicketInventory inventory;
    
    public APICreateTicketEvent(String apiId) {
        super(apiId);
    }
    
    public APICreateTicketEvent() {
        super(null);
    }

    public TicketInventory getInventory() {
        return inventory;
    }

    public void setInventory(TicketInventory inventory) {
        this.inventory = inventory;
    }
}
