package com.syscxp.header.search;

import com.syscxp.header.message.APIEvent;

public class APICreateSearchIndexEvent extends APIEvent {
    public APICreateSearchIndexEvent() {
    }

    public APICreateSearchIndexEvent(String apiId) {
        super(apiId);
    }
 
    public static APICreateSearchIndexEvent __example__() {
        APICreateSearchIndexEvent event = new APICreateSearchIndexEvent();


        return event;
    }

}
