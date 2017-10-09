package com.syscxp.account.header.identity;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APICreatePolicyEvent extends APIEvent {
    private PolicyInventory inventory;

    public APICreatePolicyEvent(String apiId) {
        super(apiId);
    }

    public APICreatePolicyEvent() {
        super(null);
    }

    public PolicyInventory getInventory() {
        return inventory;
    }

    public void setInventory(PolicyInventory inventory) {
        this.inventory = inventory;
    }

}
