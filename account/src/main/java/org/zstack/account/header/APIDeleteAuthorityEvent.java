package org.zstack.account.header;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Created by wangwg on 2017/08/15.
 */

public class APIDeleteAuthorityEvent extends APIEvent {
    public APIDeleteAuthorityEvent() {
    }

    public APIDeleteAuthorityEvent(String apiId) {
        super(apiId);
    }
 
    public static APIDeleteAuthorityEvent __example__() {
        APIDeleteAuthorityEvent event = new APIDeleteAuthorityEvent();


        return event;
    }

}
