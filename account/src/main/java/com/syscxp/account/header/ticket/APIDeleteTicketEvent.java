package com.syscxp.account.header.ticket;

import com.syscxp.header.message.APIEvent;

/**
 * Created by wangwg on 2017/09/26.
 */

public class APIDeleteTicketEvent extends APIEvent {
    public APIDeleteTicketEvent() {
    }

    public APIDeleteTicketEvent(String apiId) {
        super(apiId);
    }

}
