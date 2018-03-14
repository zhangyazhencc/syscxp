package com.syscxp.core.notification;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by xing5 on 2017/3/18.
 */
public class APIQueryNotificationSubscriptionReply extends APIQueryReply {
    private List<NotificationSubscriptionInventory> inventories;

    public List<NotificationSubscriptionInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<NotificationSubscriptionInventory> inventories) {
        this.inventories = inventories;
    }
}
