package com.syscxp.idc.header;

import com.syscxp.header.message.APIEvent;

public class APICreateIdcDetailEvent extends APIEvent {

    private IdcDetailInventory inventory;

    public APICreateIdcDetailEvent(){}

    public APICreateIdcDetailEvent(String apiId){super(apiId);}

    public IdcDetailInventory getInventory() {
        return inventory;
    }

    public void setInventory(IdcDetailInventory inventory) {
        this.inventory = inventory;
    }

}
