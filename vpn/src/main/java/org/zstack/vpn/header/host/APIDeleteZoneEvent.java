package org.zstack.vpn.header.host;


import org.zstack.header.message.APIEvent;

public class APIDeleteZoneEvent extends APIEvent {
    public APIDeleteZoneEvent() {
    }

    public APIDeleteZoneEvent(String apiId) {
        super(apiId);
    }
}
