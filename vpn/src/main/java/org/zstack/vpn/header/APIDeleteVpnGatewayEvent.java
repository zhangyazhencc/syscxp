package org.zstack.vpn.header;


import org.zstack.header.message.APIEvent;

public class APIDeleteVpnGatewayEvent extends APIEvent {
    public APIDeleteVpnGatewayEvent() {
    }

    public APIDeleteVpnGatewayEvent(String apiId) {
        super(apiId);
    }
}
