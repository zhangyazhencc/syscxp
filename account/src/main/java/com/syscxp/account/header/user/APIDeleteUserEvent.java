package com.syscxp.account.header.user;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

/**
 * Created by frank on 7/9/2015.
 */
@RestResponse
public class APIDeleteUserEvent extends APIEvent {
    public APIDeleteUserEvent() {
    }

    public APIDeleteUserEvent(String apiId) {
        super(apiId);
    }
 
    public static APIDeleteUserEvent __example__() {
        APIDeleteUserEvent event = new APIDeleteUserEvent();


        return event;
    }

}
