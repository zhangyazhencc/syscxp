package org.zstack.tunnel.header.tunnel;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Created by DCY on 2017-09-17
 */
@RestResponse(allTo = "inventory")
public class APIDeleteTunnelEvent extends APIEvent {
    private TunnelInventory inventory;

    public APIDeleteTunnelEvent(String apiId) {
        super(apiId);
    }

    public APIDeleteTunnelEvent() {}

    public TunnelInventory getInventory() {
        return inventory;
    }

    public void setInventory(TunnelInventory inventory) {
        this.inventory = inventory;
    }
}