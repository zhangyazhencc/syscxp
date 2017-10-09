package com.syscxp.vpn.header.host;

import com.syscxp.header.message.APIEvent;

public class APIReconnectVpnHostEvent extends APIEvent{

    public APIReconnectVpnHostEvent() {
    }

    public APIReconnectVpnHostEvent(String apiId) {
        super(apiId);
    }

}
