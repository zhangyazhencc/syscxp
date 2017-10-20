package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.message.APIEvent;

public class APIUpdatePolicyEvent extends APIEvent{

    private PolicyInventory inventory;

    public APIUpdatePolicyEvent(String apiId) {super(apiId);}

    public APIUpdatePolicyEvent(){}

    public PolicyInventory getInventory() {
        return inventory;
    }

    public void setInventory(PolicyInventory inventory) {
        this.inventory = inventory;
    }

}
