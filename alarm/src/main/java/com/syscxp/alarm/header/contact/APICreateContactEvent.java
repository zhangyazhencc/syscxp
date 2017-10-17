package com.syscxp.alarm.header.contact;

import com.syscxp.header.message.APIEvent;

public class APICreateContactEvent extends APIEvent{

    private ContactInventory inventory;

    public APICreateContactEvent(String apiId) {super(apiId);}

    public APICreateContactEvent(){}

    public ContactInventory getInventory() {
        return inventory;
    }

    public void setInventory(ContactInventory inventory) {
        this.inventory = inventory;
    }
}
