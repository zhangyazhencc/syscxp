package org.zstack.account.header.identity;

import org.zstack.header.message.APIEvent;

public class APIUpdateApiAllowIPEvent extends APIEvent {

    private AccountApiSecurityInventory inventory;

    public APIUpdateApiAllowIPEvent(String apiId) {
        super(apiId);
    }

    public APIUpdateApiAllowIPEvent() {
        super(null);
    }

    public AccountApiSecurityInventory getInventory() {
        return inventory;
    }

    public void setInventory(AccountApiSecurityInventory inventory) {
        this.inventory = inventory;
    }

}
