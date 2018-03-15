package com.syscxp.header.tunnel.edgeLine;

import com.syscxp.header.message.APIEvent;

/**
 * Create by DCY on 2018/1/12
 */
public class APIOpenEdgeLineEvent extends APIEvent {
    private EdgeLineInventory inventory;

    public APIOpenEdgeLineEvent(){}

    public APIOpenEdgeLineEvent(String apiId){super(apiId);}

    public EdgeLineInventory getInventory() {
        return inventory;
    }

    public void setInventory(EdgeLineInventory inventory) {
        this.inventory = inventory;
    }
}
