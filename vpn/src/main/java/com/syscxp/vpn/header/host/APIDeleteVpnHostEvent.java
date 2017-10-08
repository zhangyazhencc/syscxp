package com.syscxp.vpn.header.host;


import com.syscxp.header.message.APIEvent;

public class APIDeleteVpnHostEvent extends APIEvent {
    public APIDeleteVpnHostEvent() {
    }

    public APIDeleteVpnHostEvent(String apiId) {
        super(apiId);
    }
}
