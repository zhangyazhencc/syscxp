package com.syscxp.header.search;

import com.syscxp.header.message.APIEvent;

public class APIDeleteSearchIndexEvent extends APIEvent {
    public APIDeleteSearchIndexEvent(String apiId) {
        super(apiId);
    }

    public APIDeleteSearchIndexEvent() {
        super(null);
    }
 
    public static APIDeleteSearchIndexEvent __example__() {
        APIDeleteSearchIndexEvent event = new APIDeleteSearchIndexEvent();


        return event;
    }

}
