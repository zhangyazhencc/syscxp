package com.syscxp.tunnel.header.monitor;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-11.
 * @Description: .
 */
@RestResponse(allTo = "inventory")
public class APIStartTunnelMonitorEvent extends APIEvent {
    private TunnelMonitorInventory inventory;
    private List<TunnelMonitorInventory> inventories;

    public APIStartTunnelMonitorEvent(){};

    public APIStartTunnelMonitorEvent(String apiId){super(apiId);}

    public TunnelMonitorInventory getInventory() {
        return inventory;
    }

    public void setInventory(TunnelMonitorInventory inventory) {
        this.inventory = inventory;
    }

    public List<TunnelMonitorInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<TunnelMonitorInventory> inventories) {
        this.inventories = inventories;
    }
}
