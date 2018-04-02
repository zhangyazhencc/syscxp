package com.syscxp.idc.header;

import com.syscxp.header.message.APIEvent;

public class APIIdcRenewalEvent extends APIEvent{

    private IdcInventory inventory;

    public APIIdcRenewalEvent(){}

    public APIIdcRenewalEvent(String apiId){super(apiId);}

    public IdcInventory getInventory() {
        return inventory;
    }

    public void setInventory(IdcInventory inventory) {
        this.inventory = inventory;
    }
}
