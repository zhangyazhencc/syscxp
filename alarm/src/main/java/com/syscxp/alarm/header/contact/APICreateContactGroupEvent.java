package com.syscxp.alarm.header.contact;

import com.syscxp.header.message.APIEvent;

public class APICreateContactGroupEvent extends APIEvent {
    private ContactGroupInventory inventory;

    public APICreateContactGroupEvent(String apiId) {super(apiId);}

    public APICreateContactGroupEvent(){}

    public ContactGroupInventory getInventory() {
        return inventory;
    }

    public void setInventory(ContactGroupInventory inventory) {
        this.inventory = inventory;
    }
}
