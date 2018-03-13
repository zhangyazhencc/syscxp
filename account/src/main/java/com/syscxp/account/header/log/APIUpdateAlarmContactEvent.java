package com.syscxp.account.header.log;

import com.syscxp.header.message.APIEvent;

public class APIUpdateAlarmContactEvent extends APIEvent {
    private AlarmContactInventory inventory;

    public APIUpdateAlarmContactEvent() {
        super(null);
    }

    public APIUpdateAlarmContactEvent(String apiId) {
        super(apiId);
    }

    public AlarmContactInventory getInventory() {
        return inventory;
    }

    public void setInventory(AlarmContactInventory inventory) {
        this.inventory = inventory;
    }
}
