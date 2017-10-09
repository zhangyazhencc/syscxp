package com.syscxp.vpn.header.vpn;


import com.syscxp.header.message.APIEvent;

public class APIDeleteVpnRouteEvent extends APIEvent {
    public APIDeleteVpnRouteEvent() {
    }

    public APIDeleteVpnRouteEvent(String apiId) {
        super(apiId);
    }
}
