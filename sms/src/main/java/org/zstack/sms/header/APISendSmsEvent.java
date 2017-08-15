package org.zstack.sms.header;

import org.zstack.header.message.APIEvent;

/**
 * Created by zxhread on 17/8/14.
 */
public class APISendSmsEvent extends APIEvent {

    private SmsInventory inventory;

    public APISendSmsEvent(String apiId) {
        super(apiId);
    }

    public APISendSmsEvent() {
        super(null);
    }

    public SmsInventory getInventory() {
        return inventory;
    }

    public void setInventory(SmsInventory inventory) {
        this.inventory = inventory;
    }
}
