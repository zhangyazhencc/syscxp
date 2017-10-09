package com.syscxp.core.webhook;

import org.apache.commons.validator.routines.UrlValidator;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.core.webhooks.APICreateWebhookMsg;
import com.syscxp.header.core.webhooks.APIUpdateWebhookMsg;
import com.syscxp.header.message.APIMessage;
import static com.syscxp.core.Platform.argerr;

/**
 * Created by xing5 on 2017/5/7.
 */
public class WebhookApiInterceptor implements ApiMessageInterceptor {
    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if (msg instanceof APICreateWebhookMsg) {
            validate((APICreateWebhookMsg) msg);
        } else if (msg instanceof APIUpdateWebhookMsg) {
            validate((APIUpdateWebhookMsg) msg);
        }
        
        return msg;
    }

    private void validateUrl(String url) {
        if (!new UrlValidator().isValid(url)) {
            throw new ApiMessageInterceptionException(argerr("Invalid url[%s]", url));
        }
    }

    private void validate(APIUpdateWebhookMsg msg) {
        if (msg.getUrl() != null) {
            validateUrl(msg.getUrl());
        }
    }

    private void validate(APICreateWebhookMsg msg) {
        validateUrl(msg.getUrl());
    }
}
