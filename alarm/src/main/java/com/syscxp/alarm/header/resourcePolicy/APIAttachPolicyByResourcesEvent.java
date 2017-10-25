package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.message.APIEvent;

import java.util.List;

public class APIAttachPolicyByResourcesEvent extends APIEvent {

    private List<ResourcePolicyRefInventory> inventories;

    public APIAttachPolicyByResourcesEvent(String apiId) {super(apiId);}

    public APIAttachPolicyByResourcesEvent(){}

    public List<ResourcePolicyRefInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<ResourcePolicyRefInventory> inventories) {
        this.inventories = inventories;
    }
}
