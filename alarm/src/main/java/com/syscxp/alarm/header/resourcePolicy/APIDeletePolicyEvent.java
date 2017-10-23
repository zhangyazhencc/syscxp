package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.message.APIEvent;

public class APIDeletePolicyEvent  extends APIEvent {

    private PolicyInventory inventory;

    public APIDeletePolicyEvent(String apiId) {super(apiId);}

    public APIDeletePolicyEvent(){}

    public PolicyInventory getInventory() {
        return inventory;
    }

    public void setInventory(PolicyInventory inventory) {
        this.inventory = inventory;
    }

}