package com.syscxp.header.tunnel.network;

import com.syscxp.header.message.APIEvent;

public class APIDeleteL3RouteEvent extends APIEvent {

    public APIDeleteL3RouteEvent() {
        super(null);
    }

    public APIDeleteL3RouteEvent(String apiId) {
        super(apiId);
    }

}
