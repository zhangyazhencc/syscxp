package com.syscxp.core.debug;

import com.syscxp.header.message.APIEvent;

/**
 * Created by xing5 on 2016/7/25.
 */
public class APIDebugSignalEvent extends APIEvent {
    public APIDebugSignalEvent() {
    }

    public APIDebugSignalEvent(String apiId) {
        super(apiId);
    }
 
    public static APIDebugSignalEvent __example__() {
        APIDebugSignalEvent event = new APIDebugSignalEvent();


        return event;
    }

}
