package com.syscxp.vpn.header.vpn;


import com.syscxp.header.message.APIEvent;

public class APIDeleteVpnInterfaceEvent extends APIEvent {
    public APIDeleteVpnInterfaceEvent() {
    }

    public APIDeleteVpnInterfaceEvent(String apiId) {
        super(apiId);
    }
}
