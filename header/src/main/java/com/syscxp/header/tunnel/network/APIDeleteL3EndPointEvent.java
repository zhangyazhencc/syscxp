package com.syscxp.header.tunnel.network;

import com.syscxp.header.message.APIEvent;

public class APIDeleteL3EndPointEvent extends APIEvent {

    public APIDeleteL3EndPointEvent() {
        super(null);
    }

    public APIDeleteL3EndPointEvent(String apiId) {
        super(apiId);
    }

}
