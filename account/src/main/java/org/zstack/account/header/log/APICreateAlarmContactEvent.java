package org.zstack.account.header.log;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

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
