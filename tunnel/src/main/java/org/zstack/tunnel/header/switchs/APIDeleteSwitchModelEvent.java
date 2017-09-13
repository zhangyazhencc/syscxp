package org.zstack.tunnel.header.switchs;

import org.zstack.header.message.APIEvent;

/**
 * Created by DCY on 2017-09-13
 */
public class APIDeleteSwitchModelEvent extends APIEvent {

    private SwitchModelInventory inventory;

    public APIDeleteSwitchModelEvent(String apiId) {super(apiId);}

    public APIDeleteSwitchModelEvent(){}

    public SwitchModelInventory getInventory() {
        return inventory;
    }

    public void setInventory(SwitchModelInventory inventory) {
        this.inventory = inventory;
    }
}
