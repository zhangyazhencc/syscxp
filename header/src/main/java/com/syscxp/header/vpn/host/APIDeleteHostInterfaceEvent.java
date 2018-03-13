package com.syscxp.header.vpn.host;


import com.syscxp.header.message.APIEvent;

public class APIDeleteHostInterfaceEvent extends APIEvent {
    public APIDeleteHostInterfaceEvent() {
    }

    public APIDeleteHostInterfaceEvent(String apiId) {
        super(apiId);
    }
}
