package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.message.APIEvent;

import java.util.List;

public class APIAttachPolicyByResourceEvent  extends APIEvent {

    private List<ResourcePolicyRefInventory> inventory;

    public APIAttachPolicyByResourceEvent(String apiId) {super(apiId);}

    public APIAttachPolicyByResourceEvent(){}

    public List<ResourcePolicyRefInventory> getInventory() {
        return inventory;
    }

    public void setInventory(List<ResourcePolicyRefInventory> inventory) {
        this.inventory = inventory;
    }

}
