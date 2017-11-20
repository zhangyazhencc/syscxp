package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-18.
 * @Description: .
 */
@RestResponse(allTo = "inventory")
public class APIUpdateSpeedRecordsEvent extends APIEvent {
    private StartSpeedRecordsInventory inventory;

    public APIUpdateSpeedRecordsEvent(){};

    public APIUpdateSpeedRecordsEvent(String apiId){super(apiId);}

    public StartSpeedRecordsInventory getInventory() {
        return inventory;
    }

    public void setInventory(StartSpeedRecordsInventory inventory) {
        this.inventory = inventory;
    }
}
