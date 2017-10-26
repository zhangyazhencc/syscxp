package com.syscxp.alarm.header.contact;

import com.syscxp.header.message.APIEvent;

public class APIUpdateContactGroupEvent extends APIEvent {
    private ContactGroupInventory inventory;

    public APIUpdateContactGroupEvent(String apiId) {super(apiId);}

    public APIUpdateContactGroupEvent(){}

    public ContactGroupInventory getInventory() {
        return inventory;
    }

    public void setInventory(ContactGroupInventory inventory) {
        this.inventory = inventory;
    }
}