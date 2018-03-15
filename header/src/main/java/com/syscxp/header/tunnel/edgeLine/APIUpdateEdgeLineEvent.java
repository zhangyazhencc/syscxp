package com.syscxp.header.tunnel.edgeLine;

import com.syscxp.header.message.APIEvent;

/**
 * Create by DCY on 2018/1/11
 */
public class APIUpdateEdgeLineEvent extends APIEvent {
    private EdgeLineInventory inventory;

    public APIUpdateEdgeLineEvent(){}

    public APIUpdateEdgeLineEvent(String apiId){super(apiId);}

    public EdgeLineInventory getInventory() {
        return inventory;
    }

    public void setInventory(EdgeLineInventory inventory) {
        this.inventory = inventory;
    }
}
