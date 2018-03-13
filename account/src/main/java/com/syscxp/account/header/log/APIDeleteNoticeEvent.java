package com.syscxp.account.header.log;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

public class APIDeleteNoticeEvent extends APIEvent {
    public APIDeleteNoticeEvent() {
    }

    public APIDeleteNoticeEvent(String apiId) {
        super(apiId);
    }

    public static APIDeleteNoticeEvent __example__() {
        APIDeleteNoticeEvent event = new APIDeleteNoticeEvent();


        return event;
    }

}
