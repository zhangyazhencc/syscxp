package com.syscxp.account.header.identity;

import com.syscxp.header.message.APIEvent;

public class APIUpdateRoleEvent extends APIEvent {
    private RoleInventory inventory;

    public APIUpdateRoleEvent(String apiId) {
        super(apiId);
    }

    public APIUpdateRoleEvent() {
        super(null);
    }

    public RoleInventory getInventory() {
        return inventory;
    }

    public void setInventory(RoleInventory inventory) {
        this.inventory = inventory;
    }

}
