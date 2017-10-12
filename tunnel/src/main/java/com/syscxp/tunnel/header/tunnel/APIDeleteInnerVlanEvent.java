package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.message.APIEvent;

/**
 * Create by DCY on 2017/10/11
 */
public class APIDeleteInnerVlanEvent extends APIEvent {
    private QinqInventory inventory;

    public APIDeleteInnerVlanEvent(String apiId) {super(apiId);}

    public APIDeleteInnerVlanEvent(){}

    public QinqInventory getInventory() {
        return inventory;
    }

    public void setInventory(QinqInventory inventory) {
        this.inventory = inventory;
    }
}
