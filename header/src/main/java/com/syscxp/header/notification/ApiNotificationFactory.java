package com.syscxp.header.notification;

import com.syscxp.header.message.APIMessage;

/**
 * Created by xing5 on 2017/3/18.
 */
public interface ApiNotificationFactory {
    ApiNotification createApiNotification(APIMessage msg);
}
