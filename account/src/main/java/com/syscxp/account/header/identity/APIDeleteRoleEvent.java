package com.syscxp.account.header.identity;

import com.syscxp.header.message.APIEvent;

/**
 * Created by frank on 7/9/2015.
 */
public class APIDeleteRoleEvent extends APIEvent {
    public APIDeleteRoleEvent() {
    }

    public APIDeleteRoleEvent(String apiId) {
        super(apiId);
    }
 
    public static APIDeleteRoleEvent __example__() {
        APIDeleteRoleEvent event = new APIDeleteRoleEvent();


        return event;
    }

}
