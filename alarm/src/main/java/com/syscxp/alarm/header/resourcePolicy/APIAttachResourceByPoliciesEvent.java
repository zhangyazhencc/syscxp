package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.message.APIEvent;

import java.util.List;

public class APIAttachResourceByPoliciesEvent extends APIEvent {

    private List<PolicyInventory> inventories;

    public APIAttachResourceByPoliciesEvent(String apiId) {super(apiId);}

    public APIAttachResourceByPoliciesEvent(){}

    public List<PolicyInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<PolicyInventory> inventories) {
        this.inventories = inventories;
    }
}
