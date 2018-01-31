package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIEvent;

import java.util.List;

/**
 * Create by DCY on 2018/1/31
 */
public class APIUpdateQinqEvent extends APIEvent {

    private List<QinqInventory> inventories;

    public APIUpdateQinqEvent(){}

    public APIUpdateQinqEvent(String apiId){super(apiId);}

    public List<QinqInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<QinqInventory> inventories) {
        this.inventories = inventories;
    }
}
