package com.syscxp.header.host;

import com.syscxp.header.message.APIEvent;

public class APIDeleteHostEvent extends APIEvent {
    public APIDeleteHostEvent() {
        super(null);
    }

    public APIDeleteHostEvent(String apiId) {
        super(apiId);
    }

 
    public static APIDeleteHostEvent __example__() {
        APIDeleteHostEvent event = new APIDeleteHostEvent();


        return event;
    }

}
