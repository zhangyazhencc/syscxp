package org.zstack.account.header.identity;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Created by frank on 7/9/2015.
 */
@RestResponse
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
