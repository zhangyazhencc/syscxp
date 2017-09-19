package org.zstack.vpn.header.gateway;


import org.zstack.header.message.APIEvent;

public class APIDeleteTunnelIfaceEvent extends APIEvent {
    public APIDeleteTunnelIfaceEvent() {
    }

    public APIDeleteTunnelIfaceEvent(String apiId) {
        super(apiId);
    }
}
