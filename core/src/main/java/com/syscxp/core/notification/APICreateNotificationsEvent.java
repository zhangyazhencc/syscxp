package com.syscxp.core.notification;

import com.syscxp.header.message.APIEvent;

/**
 * Created by xing5 on 2017/3/18.
 */
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