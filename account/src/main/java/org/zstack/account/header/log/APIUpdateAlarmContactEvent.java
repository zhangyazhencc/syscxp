package org.zstack.account.header.log;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
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
