package com.syscxp.account.header.user;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

/**
 * Created by frank on 7/9/2015.
 */
@RestResponse
public class APIDetachPolicyFromUserEvent extends APIEvent {
    public APIDetachPolicyFromUserEvent() {
    }

    public APIDetachPolicyFromUserEvent(String apiId) {
        super(apiId);
    }
 
    public static APIDetachPolicyFromUserEvent __example__() {
        APIDetachPolicyFromUserEvent event = new APIDetachPolicyFromUserEvent();


        return event;
    }

}
