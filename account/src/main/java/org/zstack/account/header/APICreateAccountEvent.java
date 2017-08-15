package org.zstack.account.header;

import org.zstack.header.message.APIEvent;

public class APICreateAccountEvent extends APIEvent {
    private AccountInventory inventory;
    
    public APICreateAccountEvent(String apiId) {
        super(apiId);
    }
    
    public APICreateAccountEvent() {
        super(null);
    }

    public AccountInventory getInventory() {
        return inventory;
    }

    public void setInventory(AccountInventory inventory) {
        this.inventory = inventory;
    }
}
