package com.syscxp.header.tunnel.node;

import com.syscxp.header.message.APIEvent;

/**
 * Create by DCY on 2018/5/3
 */
public class APIAttachNodeToAccountEvent extends APIEvent {

    private AccountNodeRefInventory inventory;

    public APIAttachNodeToAccountEvent(String apiId) {
        super(apiId);
    }

    public AccountNodeRefInventory getInventory() {
        return inventory;
    }

    public void setInventory(AccountNodeRefInventory inventory) {
        this.inventory = inventory;
    }

    public APIAttachNodeToAccountEvent() {
    }
}
