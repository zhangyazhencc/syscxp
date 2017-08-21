package org.zstack.account.header.log;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

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
