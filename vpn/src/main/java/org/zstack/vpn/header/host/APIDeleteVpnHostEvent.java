package org.zstack.vpn.header.host;


import org.zstack.header.message.APIEvent;

public class APIDeleteVpnHostEvent extends APIEvent {
    public APIDeleteVpnHostEvent() {
    }

    public APIDeleteVpnHostEvent(String apiId) {
        super(apiId);
    }
}
