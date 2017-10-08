package com.syscxp.account.header.log;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse
public class APIDeleteAlarmContactEvent extends APIEvent {
    public APIDeleteAlarmContactEvent() {
    }

    public APIDeleteAlarmContactEvent(String apiId) {
        super(apiId);
    }

    public static APIDeleteAlarmContactEvent __example__() {
        APIDeleteAlarmContactEvent event = new APIDeleteAlarmContactEvent();


        return event;
    }

}
