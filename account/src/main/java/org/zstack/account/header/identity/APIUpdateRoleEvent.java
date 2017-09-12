package org.zstack.account.header.identity;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
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
