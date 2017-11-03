package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.message.APIEvent;

import java.util.List;

public class APIAttachResourceByPoliciesEvent extends APIEvent {

    private List<PolicyInventory> inventory;

    public APIAttachResourceByPoliciesEvent(String apiId) {super(apiId);}

    public APIAttachResourceByPoliciesEvent(){}

    public List<PolicyInventory> getInventory() {
        return inventory;
    }

    public void setInventory(List<PolicyInventory> inventory) {
        this.inventory = inventory;
    }

}
