package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.message.APIEvent;

public class APIAttachResourceByPolicyEvent  extends APIEvent {

    private ResourcePolicyRefInventory inventory;

    public APIAttachResourceByPolicyEvent(String apiId) {super(apiId);}

    public APIAttachResourceByPolicyEvent(){}

    public ResourcePolicyRefInventory getInventory() {
        return inventory;
    }

    public void setInventory(ResourcePolicyRefInventory inventory) {
        this.inventory = inventory;
    }

}
