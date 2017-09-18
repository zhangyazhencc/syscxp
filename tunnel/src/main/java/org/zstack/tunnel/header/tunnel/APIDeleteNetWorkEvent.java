package org.zstack.tunnel.header.tunnel;

import org.zstack.header.message.APIEvent;

/**
 * Created by DCY on 2017-09-14
 */
public class APIDeleteNetWorkEvent extends APIEvent {
    private NetWorkInventory inventory;

    public APIDeleteNetWorkEvent(String apiId) {super(apiId);}

    public APIDeleteNetWorkEvent(){}

    public NetWorkInventory getInventory() {
        return inventory;
    }

    public void setInventory(NetWorkInventory inventory) {
        this.inventory = inventory;
    }
}
