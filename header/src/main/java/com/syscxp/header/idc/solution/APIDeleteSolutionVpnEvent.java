package com.syscxp.header.idc.solution;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

public class APIDeleteSolutionVpnEvent extends APIEvent {
    public APIDeleteSolutionVpnEvent(String apiId) {
        super(apiId);
    }

    public APIDeleteSolutionVpnEvent() {
        super(null);
    }
}
