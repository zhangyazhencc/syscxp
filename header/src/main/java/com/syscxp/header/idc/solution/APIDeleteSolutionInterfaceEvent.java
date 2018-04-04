package com.syscxp.header.idc.solution;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

public class APIDeleteSolutionInterfaceEvent extends APIEvent {
    public APIDeleteSolutionInterfaceEvent(String apiId) {
        super(apiId);
    }

    public APIDeleteSolutionInterfaceEvent() {
        super(null);
    }


}
