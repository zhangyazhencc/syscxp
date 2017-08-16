package org.zstack.account.header.identity;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APICreateAuthorityEvent extends APIEvent {
    private AuthorityInventory inventory;

    public APICreateAuthorityEvent(String apiId) {
        super(apiId);
    }

    public APICreateAuthorityEvent() {
        super(null);
    }

    public AuthorityInventory getInventory() {
        return inventory;
    }

    public void setInventory(AuthorityInventory inventory) {
        this.inventory = inventory;
    }

}
