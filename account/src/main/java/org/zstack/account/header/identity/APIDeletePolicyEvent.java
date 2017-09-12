package org.zstack.account.header.identity;

import org.zstack.header.message.APIEvent;

/**
 * Created by wangwg on 2017/08/15.
 */

public class APIDeletePolicyEvent extends APIEvent {
    public APIDeletePolicyEvent() {
    }

    public APIDeletePolicyEvent(String apiId) {
        super(apiId);
    }
 
    public static APIDeletePolicyEvent __example__() {
        APIDeletePolicyEvent event = new APIDeletePolicyEvent();


        return event;
    }

}
