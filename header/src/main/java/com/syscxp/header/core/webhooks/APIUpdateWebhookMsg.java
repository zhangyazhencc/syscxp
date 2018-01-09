package com.syscxp.header.core.webhooks;

import com.syscxp.header.message.APIParam;
import com.syscxp.header.rest.RestRequest;
import org.springframework.http.HttpMethod;
import com.syscxp.header.message.APIMessage;

/**
 * Created by xing5 on 2017/5/7.
 */
@RestRequest(
        method = HttpMethod.GET,
        isAction = true,
        responseClass = APIUpdateWebhookEvent.class
)
public class APIUpdateWebhookMsg extends APIMessage {
    @APIParam(resourceType = WebhookVO.class)
    private String uuid;
    @APIParam(maxLength = 255, required = false)
    private String name;
    @APIParam(maxLength = 2048, required = false)
    private String description;
    @APIParam(maxLength = 2048, required = false)
    private String url;
    @APIParam(maxLength = 255, required = false)
    private String type;
    private String opaque;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOpaque() {
        return opaque;
    }

    public void setOpaque(String opaque) {
        this.opaque = opaque;
    }
}
