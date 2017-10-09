package com.syscxp.header.core.webhooks;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

/**
 * Created by xing5 on 2017/5/7.
 */
@RestResponse(allTo = "inventory")
public class APIUpdateWebhookEvent extends APIEvent {
    private WebhookInventory inventory;

    public APIUpdateWebhookEvent() {
    }

    public APIUpdateWebhookEvent(String apiId) {
        super(apiId);
    }

    public WebhookInventory getInventory() {
        return inventory;
    }

    public void setInventory(WebhookInventory inventory) {
        this.inventory = inventory;
    }
}
