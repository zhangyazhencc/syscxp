package com.syscxp.header.configuration;

import com.syscxp.header.message.APIEvent;

/**
 */
public class APIGenerateSqlIndexEvent extends APIEvent {
    public APIGenerateSqlIndexEvent(String apiId) {
        super(apiId);
    }

    public APIGenerateSqlIndexEvent() {
        super(null);
    }
 
    public static APIGenerateSqlIndexEvent __example__() {
        APIGenerateSqlIndexEvent event = new APIGenerateSqlIndexEvent();


        return event;
    }

}
