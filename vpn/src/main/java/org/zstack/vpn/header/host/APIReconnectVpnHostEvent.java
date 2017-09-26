package org.zstack.vpn.header.host;

import org.zstack.header.message.APIEvent;

public class APIReconnectVpnHostEvent extends APIEvent{

    public APIReconnectVpnHostEvent() {
    }

    public APIReconnectVpnHostEvent(String apiId) {
        super(apiId);
    }

}
