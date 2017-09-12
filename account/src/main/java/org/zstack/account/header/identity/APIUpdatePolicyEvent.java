package org.zstack.account.header.identity;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APIUpdatePolicyEvent extends APIEvent {
    private PolicyInventory inventory;

    public APIUpdatePolicyEvent(String apiId) {
        super(apiId);
    }

    public APIUpdatePolicyEvent() {
        super(null);
    }

    public PolicyInventory getInventory() {
        return inventory;
    }

    public void setInventory(PolicyInventory inventory) {
        this.inventory = inventory;
    }

}
