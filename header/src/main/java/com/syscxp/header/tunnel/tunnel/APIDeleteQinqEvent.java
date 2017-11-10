package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIEvent;

/**
 * Create by DCY on 2017/10/31
 */
public class APIDeleteQinqEvent extends APIEvent {
    private QinqInventory inventory;

    public APIDeleteQinqEvent(){}

    public APIDeleteQinqEvent(String apiId){super(apiId);}

    public QinqInventory getInventory() {
        return inventory;
    }

    public void setInventory(QinqInventory inventory) {
        this.inventory = inventory;
    }
}
