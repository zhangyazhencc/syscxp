package com.syscxp.header.tunnel.node;

import com.syscxp.header.message.APIEvent;

public class APIDetachNodeFromZoneEvent extends APIEvent{

    public APIDetachNodeFromZoneEvent(String apiId) {
        super(apiId);
    }
}
