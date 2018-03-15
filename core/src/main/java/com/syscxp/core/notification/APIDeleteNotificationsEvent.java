package com.syscxp.core.notification;

import com.syscxp.header.message.APIEvent;

/**
 * Created by xing5 on 2017/3/18.
 */
public class APIDeleteNotificationsEvent extends APIEvent {
    public APIDeleteNotificationsEvent() {
    }

    public APIDeleteNotificationsEvent(String apiId) {
        super(apiId);
    }

    public static APIDeleteNotificationsEvent __example__() {
        APIDeleteNotificationsEvent msg = new APIDeleteNotificationsEvent();
        return msg;
    }
    
}