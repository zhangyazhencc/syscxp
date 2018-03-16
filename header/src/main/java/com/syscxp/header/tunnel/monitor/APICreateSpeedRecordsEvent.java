package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.message.APIEvent;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-18.
 * @Description: .
 */
public class APICreateSpeedRecordsEvent extends APIEvent {
    private StartSpeedRecordsInventory inventory;

    public APICreateSpeedRecordsEvent(){};

    public APICreateSpeedRecordsEvent(String apiId){super(apiId);}

    public StartSpeedRecordsInventory getInventory() {
        return inventory;
    }

    public void setInventory(StartSpeedRecordsInventory inventory) {
        this.inventory = inventory;
    }
}
