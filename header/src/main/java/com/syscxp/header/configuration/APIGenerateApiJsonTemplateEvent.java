package com.syscxp.header.configuration;

import com.syscxp.header.message.APIEvent;

public class APIGenerateApiJsonTemplateEvent extends APIEvent {
    public APIGenerateApiJsonTemplateEvent(String apiId) {
        super(apiId);
    }

    public APIGenerateApiJsonTemplateEvent() {
        super(null);
    }
 
    public static APIGenerateApiJsonTemplateEvent __example__() {
        APIGenerateApiJsonTemplateEvent event = new APIGenerateApiJsonTemplateEvent();


        return event;
    }

}
