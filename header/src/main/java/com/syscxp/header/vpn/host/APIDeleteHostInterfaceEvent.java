package com.syscxp.header.vpn.host;


import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse()
public class APIDeleteHostInterfaceEvent extends APIEvent {
    public APIDeleteHostInterfaceEvent() {
    }

    public APIDeleteHostInterfaceEvent(String apiId) {
        super(apiId);
    }
}
