package com.syscxp.header.core.webhooks;

import com.syscxp.header.message.APIDeleteMessage;
import com.syscxp.header.rest.RestRequest;
import org.springframework.http.HttpMethod;
import com.syscxp.header.message.APIParam;

/**
 * Created by xing5 on 2017/5/7.
 */

public class APIDeleteWebhookMsg extends APIDeleteMessage {
    @APIParam(resourceType = WebhookVO.class, successIfResourceNotExisting = true)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
