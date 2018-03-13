package com.syscxp.header.core.webhooks;

import com.syscxp.header.message.APIEvent;

/**
 * Created by xing5 on 2017/5/7.
 */
public class APIDeleteWebhookEvent extends APIEvent {
    public APIDeleteWebhookEvent() {
    }

    public APIDeleteWebhookEvent(String apiId) {
        super(apiId);
    }
}
