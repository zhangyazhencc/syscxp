package com.syscxp.header.tunnel.solution;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APIDeleteSolutionEvent extends APIEvent {
    public APIDeleteSolutionEvent(String apiId) {
        super(apiId);
    }

    public APIDeleteSolutionEvent() {
        super(null);
    }
}
