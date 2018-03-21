package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.message.APIEvent;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-18.
 * @Description: .
 */
public class APIDeleteSpeedTestTunnelEvent extends APIEvent {
    private SpeedTestTunnelInventory inventory;

    public APIDeleteSpeedTestTunnelEvent(){};

    public APIDeleteSpeedTestTunnelEvent(String apiId){super(apiId);}

    public SpeedTestTunnelInventory getInventory() {
        return inventory;
    }

    public void setInventory(SpeedTestTunnelInventory inventory) {
        this.inventory = inventory;
    }
}
