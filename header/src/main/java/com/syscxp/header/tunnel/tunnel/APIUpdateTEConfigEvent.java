package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIEvent;

/**
 * Create by DCY on 2018/5/9
 */
public class APIUpdateTEConfigEvent extends APIEvent {
    private TEConfigInventory inventory;

    public APIUpdateTEConfigEvent(){}

    public APIUpdateTEConfigEvent(String apiId){super(apiId);}

    public TEConfigInventory getInventory() {
        return inventory;
    }

    public void setInventory(TEConfigInventory inventory) {
        this.inventory = inventory;
    }
}
