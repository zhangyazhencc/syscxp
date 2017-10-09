package com.syscxp.header.core.webhooks;

import com.syscxp.header.rest.RestRequest;
import org.springframework.http.HttpMethod;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

/**
 * Created by xing5 on 2017/5/7.
 */
@AutoQuery(inventoryClass = WebhookInventory.class, replyClass = APIQueryWebhookReply.class)
@RestRequest(
        path = "/web-hooks",
        optionalPaths = {"/web-hooks/{uuid}"},
        method = HttpMethod.GET,
        responseClass = APIQueryWebhookReply.class
)
public class APIQueryWebhookMsg extends APIQueryMessage {
}
