package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.message.APIEvent;

import java.util.List;

public class APIAttachResourceByPoliciesEvent extends APIEvent {

    private List<ResourcePolicyRefInventory> inventory;

    public APIAttachResourceByPoliciesEvent(String apiId) {super(apiId);}

    public APIAttachResourceByPoliciesEvent(){}

    public List<ResourcePolicyRefInventory> getInventory() {
        return inventory;
    }

    public void setInventory(List<ResourcePolicyRefInventory> inventory) {
        this.inventory = inventory;
    }

}
