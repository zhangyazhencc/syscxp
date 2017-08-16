package org.zstack.account.header.log;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

@RestResponse
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
