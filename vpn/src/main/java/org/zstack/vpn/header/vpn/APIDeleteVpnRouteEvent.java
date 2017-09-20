package org.zstack.vpn.header.vpn;


import org.zstack.header.message.APIEvent;

public class APIDeleteVpnRouteEvent extends APIEvent {
    public APIDeleteVpnRouteEvent() {
    }

    public APIDeleteVpnRouteEvent(String apiId) {
        super(apiId);
    }
}
