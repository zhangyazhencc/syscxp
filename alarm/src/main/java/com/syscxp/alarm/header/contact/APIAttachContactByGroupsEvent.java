package com.syscxp.alarm.header.contact;

import com.syscxp.header.message.APIEvent;

public class APIAttachContactByGroupsEvent extends APIEvent {
    private ContactInventory inventory;

    public APIAttachContactByGroupsEvent(String apiId) {super(apiId);}

    public APIAttachContactByGroupsEvent(){}

    public ContactInventory getInventory() {
        return inventory;
    }

    public void setInventory(ContactInventory inventory) {
        this.inventory = inventory;
    }

}