package org.zstack.vpn.header.host;


import org.zstack.header.message.APIEvent;

public class APIDeleteHostInterfaceEvent extends APIEvent {
    public APIDeleteHostInterfaceEvent() {
    }

    public APIDeleteHostInterfaceEvent(String apiId) {
        super(apiId);
    }
}
