package com.syscxp.header.idc.solution;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

public class APIDeleteSolutionTunnelEvent extends APIEvent {
    public APIDeleteSolutionTunnelEvent(String apiId) {
        super(apiId);
    }

    public APIDeleteSolutionTunnelEvent() {
        super(null);
    }

}
