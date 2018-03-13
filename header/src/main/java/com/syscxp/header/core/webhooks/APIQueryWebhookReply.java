package com.syscxp.header.core.webhooks;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by xing5 on 2017/5/7.
 */
public class APIQueryWebhookReply extends APIQueryReply {
    private List<WebhookInventory> inventories;

    public List<WebhookInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<WebhookInventory> inventories) {
        this.inventories = inventories;
    }
}
