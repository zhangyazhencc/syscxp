package com.syscxp.account.header.log;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APICreateAlarmContactEvent extends APIEvent {
    private AlarmContactInventory inventory;

    public APICreateAlarmContactEvent() {
        super(null);
    }

    public APICreateAlarmContactEvent(String apiId) {
        super(apiId);
    }

    public AlarmContactInventory getInventory() {
        return inventory;
    }

    public void setInventory(AlarmContactInventory inventory) {
        this.inventory = inventory;
    }
}
