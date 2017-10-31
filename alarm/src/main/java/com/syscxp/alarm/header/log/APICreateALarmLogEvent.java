package com.syscxp.alarm.header.log;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APICreateALarmLogEvent extends APIEvent {
    public APICreateALarmLogEvent() {}

    public APICreateALarmLogEvent(String apiId) {
        super(apiId);
    }

    private AlarmLogInventory inventory;

    public AlarmLogInventory getInventory() {
        return inventory;
    }

    public void setInventory(AlarmLogInventory inventory) {
        this.inventory = inventory;
    }
}
