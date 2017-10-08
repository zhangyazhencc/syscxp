package com.syscxp.account.header.identity;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

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
