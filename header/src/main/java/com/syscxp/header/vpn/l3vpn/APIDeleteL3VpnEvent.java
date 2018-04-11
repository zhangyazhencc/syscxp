package com.syscxp.header.vpn.l3vpn;


import com.syscxp.header.message.APIEvent;

public class APIDeleteL3VpnEvent extends APIEvent {
    public APIDeleteL3VpnEvent() {
    }

    public APIDeleteL3VpnEvent(String apiId) {
        super(apiId);
    }
}
