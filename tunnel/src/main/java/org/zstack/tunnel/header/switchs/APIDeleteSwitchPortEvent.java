package org.zstack.tunnel.header.switchs;

import org.zstack.header.message.APIEvent;

/**
 * Created by DCY on 2017-09-13
 */
public class APIDeleteSwitchPortEvent extends APIEvent {
    private SwitchPortInventory inventory;

    public APIDeleteSwitchPortEvent(String apiId) {super(apiId);}

    public APIDeleteSwitchPortEvent(){}

    public SwitchPortInventory getInventory() {
        return inventory;
    }

    public void setInventory(SwitchPortInventory inventory) {
        this.inventory = inventory;
    }
}
