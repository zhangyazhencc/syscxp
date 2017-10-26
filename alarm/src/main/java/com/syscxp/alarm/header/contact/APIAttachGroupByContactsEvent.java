package com.syscxp.alarm.header.contact;

import com.syscxp.header.message.APIEvent;

import java.util.List;

public class APIAttachGroupByContactsEvent extends APIEvent {
    private ContactGroupInventory inventory;

    public APIAttachGroupByContactsEvent(String apiId) {super(apiId);}

    public APIAttachGroupByContactsEvent(){}

    public ContactGroupInventory getInventory() {
        return inventory;
    }

    public void setInventory(ContactGroupInventory inventory) {
        this.inventory = inventory;
    }

}
