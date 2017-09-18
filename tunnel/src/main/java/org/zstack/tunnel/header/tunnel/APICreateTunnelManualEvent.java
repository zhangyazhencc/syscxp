package org.zstack.tunnel.header.tunnel;

import org.zstack.header.message.APIEvent;

/**
 * Created by DCY on 2017-09-15
 */
public class APICreateTunnelManualEvent extends APIEvent {
    private TunnelInventory inventory;

    public APICreateTunnelManualEvent(){}

    public APICreateTunnelManualEvent(String apiId){super(apiId);}

    public TunnelInventory getInventory() {
        return inventory;
    }

    public void setInventory(TunnelInventory inventory) {
        this.inventory = inventory;
    }
}
