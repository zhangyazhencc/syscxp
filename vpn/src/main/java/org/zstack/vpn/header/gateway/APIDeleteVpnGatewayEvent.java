package org.zstack.vpn.header.gateway;


import org.zstack.header.message.APIEvent;

public class APIDeleteVpnGatewayEvent extends APIEvent {
    public APIDeleteVpnGatewayEvent() {
    }

    public APIDeleteVpnGatewayEvent(String apiId) {
        super(apiId);
    }
}
