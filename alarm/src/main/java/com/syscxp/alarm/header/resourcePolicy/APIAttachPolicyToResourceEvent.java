package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.message.APIEvent;

import java.util.List;

public class APIAttachPolicyToResourceEvent extends APIEvent {

    public APIAttachPolicyToResourceEvent(String apiId) {super(apiId);}

    public APIAttachPolicyToResourceEvent(){}

    private PolicyInventory inventory;

    public PolicyInventory getInventory() {
        return inventory;
    }

    public void setInventory(PolicyInventory inventory) {
        this.inventory = inventory;
    }
}
