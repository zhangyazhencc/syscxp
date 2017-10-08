package com.syscxp.core.notification;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

/**
 * Created by xing5 on 2017/3/18.
 */
@RestResponse
public class APIUpdateNotificationsStatusEvent extends APIEvent {
    public APIUpdateNotificationsStatusEvent() {
    }

    public APIUpdateNotificationsStatusEvent(String apiId) {
        super(apiId);
    }

    public static APIUpdateNotificationsStatusEvent __example__() {
        APIUpdateNotificationsStatusEvent msg = new APIUpdateNotificationsStatusEvent();
        return msg;
    }
    
}