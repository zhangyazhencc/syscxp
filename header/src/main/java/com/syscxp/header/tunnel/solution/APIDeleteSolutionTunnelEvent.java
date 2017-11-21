package com.syscxp.header.tunnel.solution;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APIDeleteSolutionTunnelEvent extends APIEvent {
    public APIDeleteSolutionTunnelEvent(String apiId) {
        super(apiId);
    }

    public APIDeleteSolutionTunnelEvent() {
        super(null);
    }
}
