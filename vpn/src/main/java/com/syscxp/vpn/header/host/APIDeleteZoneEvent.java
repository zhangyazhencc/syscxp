package com.syscxp.vpn.header.host;


import com.syscxp.header.message.APIEvent;

public class APIDeleteZoneEvent extends APIEvent {
    public APIDeleteZoneEvent() {
    }

    public APIDeleteZoneEvent(String apiId) {
        super(apiId);
    }
}
