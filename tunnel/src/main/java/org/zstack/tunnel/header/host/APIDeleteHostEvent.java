package org.zstack.tunnel.header.host;

import org.zstack.header.message.APIEvent;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-14.
 * @Description: .
 */
public class APIDeleteHostEvent extends APIEvent {
    private HostInventory inventory;
    public APIDeleteHostEvent() {
    }

    public APIDeleteHostEvent(String apiId) {
        super(apiId);
    }

    public void setInventory(HostInventory inventory) {
        this.inventory = inventory;
    }

    public HostInventory getInventory() {
        return inventory;
    }
}
