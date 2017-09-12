package org.zstack.account.header.account;

import org.zstack.header.message.APIEvent;

/**
 * Created by wangwg on 2017/08/21.
 */

public class APIDeleteAccountContactsEvent extends APIEvent {
    public APIDeleteAccountContactsEvent() {
    }

    public APIDeleteAccountContactsEvent(String apiId) {
        super(apiId);
    }
 
    public static APIDeleteAccountContactsEvent __example__() {
        APIDeleteAccountContactsEvent event = new APIDeleteAccountContactsEvent();


        return event;
    }

}
