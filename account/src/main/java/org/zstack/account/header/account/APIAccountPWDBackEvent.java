package org.zstack.account.header.account;

import org.zstack.header.message.APIEvent;

public class APIAccountPWDBackEvent extends APIEvent {

    private AccountInventory inventory;

    public AccountInventory getInventory() {
        return inventory;
    }

    public void setInventory(AccountInventory inventory) {
        this.inventory = inventory;
    }

    public APIAccountPWDBackEvent(String apiId) {
        super(apiId);
    }

    public APIAccountPWDBackEvent() {
        super(null);
    }

}
