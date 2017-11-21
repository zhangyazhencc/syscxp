package com.syscxp.header.vpn.host;


import com.syscxp.header.message.APIEvent;

public class APIDeleteZoneEvent extends APIEvent {
    public APIDeleteZoneEvent() {
    }

    public APIDeleteZoneEvent(String apiId) {
        super(apiId);
    }
}
