package com.syscxp.core.notification;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by xing5 on 2017/3/18.
 */
public class APIQueryNotificationReply extends APIQueryReply {
    private List<NotificationInventory> inventories;

    public List<NotificationInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<NotificationInventory> inventories) {
        this.inventories = inventories;
    }
}
