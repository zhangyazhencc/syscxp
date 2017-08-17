package org.zstack.account.header.identity;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APICreatePermissionEvent extends APIEvent {
    private PermissionInventory inventory;

    public APICreatePermissionEvent(String apiId) {
        super(apiId);
    }

    public APICreatePermissionEvent() {
        super(null);
    }

    public PermissionInventory getInventory() {
        return inventory;
    }

    public void setInventory(PermissionInventory inventory) {
        this.inventory = inventory;
    }

}
