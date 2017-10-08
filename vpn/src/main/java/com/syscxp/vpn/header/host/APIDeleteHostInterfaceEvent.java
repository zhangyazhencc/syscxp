package com.syscxp.vpn.header.host;


import com.syscxp.header.message.APIEvent;

public class APIDeleteHostInterfaceEvent extends APIEvent {
    public APIDeleteHostInterfaceEvent() {
    }

    public APIDeleteHostInterfaceEvent(String apiId) {
        super(apiId);
    }
}
