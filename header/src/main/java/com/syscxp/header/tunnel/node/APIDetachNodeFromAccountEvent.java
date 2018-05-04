package com.syscxp.header.tunnel.node;

import com.syscxp.header.message.APIEvent;

/**
 * Create by DCY on 2018/5/3
 */
public class APIDetachNodeFromAccountEvent extends APIEvent {
    public APIDetachNodeFromAccountEvent(String apiId) {
        super(apiId);
    }

    public APIDetachNodeFromAccountEvent() {
    }
}
