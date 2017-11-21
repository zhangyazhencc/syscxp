package com.syscxp.header.tunnel.solution;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APIDeleteSolutionInterfaceEvent extends APIEvent {
    public APIDeleteSolutionInterfaceEvent(String apiId) {
        super(apiId);
    }

    public APIDeleteSolutionInterfaceEvent() {
        super(null);
    }
}
