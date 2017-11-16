package com.syscxp.sms.header;

import com.syscxp.header.message.APIEvent;

public class APISendAlarmSmsEvent extends APIEvent {
    private SmsInventory inventory;

    public APISendAlarmSmsEvent(String apiId) {
        super(apiId);
    }

    public APISendAlarmSmsEvent() {}

    public SmsInventory getInventory() {
        return inventory;
    }

    public void setInventory(SmsInventory inventory) {
        this.inventory = inventory;
    }
}
