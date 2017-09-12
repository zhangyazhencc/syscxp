package org.zstack.account.header.account;

import org.zstack.header.message.APIEvent;

public class APIRegisterAccountEvent extends APIEvent {
    private AccountInventory inventory;

    public APIRegisterAccountEvent(String apiId) {
        super(apiId);
    }

    public APIRegisterAccountEvent() {
        super(null);
    }

    public AccountInventory getInventory() {
        return inventory;
    }

    public void setInventory(AccountInventory inventory) {
        this.inventory = inventory;
    }
}
