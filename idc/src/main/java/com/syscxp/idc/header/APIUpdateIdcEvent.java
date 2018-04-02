package com.syscxp.idc.header;

import com.syscxp.header.message.APIEvent;

public class APIUpdateIdcEvent extends APIEvent{

    private IdcInventory inventory;

    public APIUpdateIdcEvent(){}

    public APIUpdateIdcEvent(String apiId){super(apiId);}

    public IdcInventory getInventory() {
        return inventory;
    }

    public void setInventory(IdcInventory inventory) {
        this.inventory = inventory;
    }
}
