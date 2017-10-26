package com.syscxp.alarm.header.contact;

import com.syscxp.header.message.APIEvent;

public class APIDeleteContactGroupEvent  extends APIEvent {
    private ContactGroupInventory inventory;

    public APIDeleteContactGroupEvent(String apiId) {super(apiId);}

    public APIDeleteContactGroupEvent(){}

    public ContactGroupInventory getInventory() {
        return inventory;
    }

    public void setInventory(ContactGroupInventory inventory) {
        this.inventory = inventory;
    }
}