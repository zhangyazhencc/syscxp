package com.syscxp.idc.header;

import com.syscxp.header.message.APIEvent;

public class APICreateIdcEvent extends APIEvent {

    private IdcInventory inventory;

    public APICreateIdcEvent(){}

    public APICreateIdcEvent(String apiId){super(apiId);}

    public IdcInventory getInventory() {
        return inventory;
    }

    public void setInventory(IdcInventory inventory) {
        this.inventory = inventory;
    }

}
