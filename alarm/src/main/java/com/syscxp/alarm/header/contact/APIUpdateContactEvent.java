package com.syscxp.alarm.header.contact;

import com.syscxp.header.message.APIEvent;

public class APIUpdateContactEvent extends APIEvent {
    private ContactInventory inventory;

    public APIUpdateContactEvent(String apiId) {super(apiId);}

    public APIUpdateContactEvent(){}

    public ContactInventory getInventory() {
        return inventory;
    }

    public void setInventory(ContactInventory inventory) {
        this.inventory = inventory;
    }
}