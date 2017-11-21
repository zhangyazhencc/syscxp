package com.syscxp.header.tunnel.solution;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APIDeleteSolutionVpnEvent extends APIEvent {
    public APIDeleteSolutionVpnEvent(String apiId) {
        super(apiId);
    }

    public APIDeleteSolutionVpnEvent() {
        super(null);
    }
}
