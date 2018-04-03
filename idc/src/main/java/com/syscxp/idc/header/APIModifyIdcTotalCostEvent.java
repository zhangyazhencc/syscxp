package com.syscxp.idc.header;

import com.syscxp.header.message.APIEvent;

public class APIModifyIdcTotalCostEvent extends APIEvent{
    private IdcInventory inventory;

    public APIModifyIdcTotalCostEvent(){}

    public APIModifyIdcTotalCostEvent(String apiId){super(apiId);}

    public IdcInventory getInventory() {
        return inventory;
    }

    public void setInventory(IdcInventory inventory) {
        this.inventory = inventory;
    }
}
