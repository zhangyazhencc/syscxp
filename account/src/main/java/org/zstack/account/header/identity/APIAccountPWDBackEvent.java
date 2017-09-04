package org.zstack.account.header.identity;

import org.zstack.header.message.APIEvent;

public class APIAccountPWDBackEvent extends APIEvent {

    public APIAccountPWDBackEvent(String apiId) {
        super(apiId);
    }

    public APIAccountPWDBackEvent() {
        super(null);
    }

}
