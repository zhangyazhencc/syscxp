package com.syscxp.vpn.header.vpn;


import com.syscxp.header.message.APIEvent;

public class APIDeleteVpnEvent extends APIEvent {
    public APIDeleteVpnEvent() {
    }

    public APIDeleteVpnEvent(String apiId) {
        super(apiId);
    }
}
