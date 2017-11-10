package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-18.
 * @Description: .
 */
@RestResponse(allTo = "inventory")
public class APICreateSpeedRecordsEvent extends APIEvent {
    private SpeedRecordsInventory inventory;

    public APICreateSpeedRecordsEvent(){};

    public APICreateSpeedRecordsEvent(String apiId){super(apiId);}

    public SpeedRecordsInventory getInventory() {
        return inventory;
    }

    public void setInventory(SpeedRecordsInventory inventory) {
        this.inventory = inventory;
    }
}
