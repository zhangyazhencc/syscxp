package com.syscxp.alarm.header.contact;

import com.syscxp.header.message.APIEvent;

public class APIDeleteContactEvent extends APIEvent{
        private ContactInventory inventory;

        public APIDeleteContactEvent(String apiId) {super(apiId);}

        public APIDeleteContactEvent(){}

        public ContactInventory getInventory() {
            return inventory;
        }

        public void setInventory(ContactInventory inventory) {
            this.inventory = inventory;
        }
}