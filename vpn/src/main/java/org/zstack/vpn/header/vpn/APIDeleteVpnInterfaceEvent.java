package org.zstack.vpn.header.vpn;


import org.zstack.header.message.APIEvent;

public class APIDeleteVpnInterfaceEvent extends APIEvent {
    public APIDeleteVpnInterfaceEvent() {
    }

    public APIDeleteVpnInterfaceEvent(String apiId) {
        super(apiId);
    }
}
