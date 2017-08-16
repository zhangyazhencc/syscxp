package org.zstack.account.header.identity;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APIUpdateAuthorityEvent extends APIEvent {
    private PermissionInventory inventory;

    public APIUpdateAuthorityEvent(String apiId) {
        super(apiId);
    }

    public APIUpdateAuthorityEvent() {
        super(null);
    }

    public PermissionInventory getInventory() {
        return inventory;
    }

    public void setInventory(PermissionInventory inventory) {
        this.inventory = inventory;
    }

}
