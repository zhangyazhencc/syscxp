package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.message.APIEvent;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-18.
 * @Description: .
 */
public class APIUpdateSpeedRecordsEvent extends APIEvent {
    private SpeedRecordsInventory inventory;

    public APIUpdateSpeedRecordsEvent(){};

    public APIUpdateSpeedRecordsEvent(String apiId){super(apiId);}

    public SpeedRecordsInventory getInventory() {
        return inventory;
    }

    public void setInventory(SpeedRecordsInventory inventory) {
        this.inventory = inventory;
    }
}
