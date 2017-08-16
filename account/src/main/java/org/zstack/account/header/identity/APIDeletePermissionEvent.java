package org.zstack.account.header.identity;

import org.zstack.header.message.APIEvent;

/**
 * Created by wangwg on 2017/08/15.
 */

public class APIDeletePermissionEvent extends APIEvent {
    public APIDeletePermissionEvent() {
    }

    public APIDeletePermissionEvent(String apiId) {
        super(apiId);
    }
 
    public static APIDeletePermissionEvent __example__() {
        APIDeletePermissionEvent event = new APIDeletePermissionEvent();


        return event;
    }

}
