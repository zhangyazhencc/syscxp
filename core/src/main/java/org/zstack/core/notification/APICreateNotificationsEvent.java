package org.zstack.core.notification;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Created by xing5 on 2017/3/18.
 */
@RestResponse(allTo = "inventory")
public class APICreateNotificationsEvent extends APIEvent {

    private NotificationInventory inventory;

    public APICreateNotificationsEvent() {
        super(null);
    }

    public APICreateNotificationsEvent(String apiId) {
        super(apiId);
    }

    public NotificationInventory getInventory() {
        return inventory;
    }

    public void setInventory(NotificationInventory inventory) {
        this.inventory = inventory;
    }

    public static APICreateNotificationsEvent __example__() {
        APICreateNotificationsEvent msg = new APICreateNotificationsEvent();
        return msg;
    }
    
}