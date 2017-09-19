package org.zstack.vpn.header.vpn;


import org.zstack.header.message.APIEvent;

public class APIDeleteVpnEvent extends APIEvent {
    public APIDeleteVpnEvent() {
    }

    public APIDeleteVpnEvent(String apiId) {
        super(apiId);
    }
}
