package com.syscxp.account.header.identity;

import com.syscxp.header.message.APIEvent;

public class APICreateRoleEvent extends APIEvent {
    private RoleInventory inventory;

    public APICreateRoleEvent(String apiId) {
        super(apiId);
    }

    public APICreateRoleEvent() {
        super(null);
    }

    public RoleInventory getInventory() {
        return inventory;
    }

    public void setInventory(RoleInventory inventory) {
        this.inventory = inventory;
    }

}
