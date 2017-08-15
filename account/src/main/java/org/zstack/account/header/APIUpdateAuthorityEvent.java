package org.zstack.account.header;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APIUpdateAuthorityEvent extends APIEvent {
    private AuthorityInventory inventory;

    public APIUpdateAuthorityEvent(String apiId) {
        super(apiId);
    }

    public APIUpdateAuthorityEvent() {
        super(null);
    }

    public AuthorityInventory getInventory() {
        return inventory;
    }

    public void setInventory(AuthorityInventory inventory) {
        this.inventory = inventory;
    }

}
