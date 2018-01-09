package com.syscxp.header.tunnel.node;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

/**
 * Created by wangwg on 2017/12/29
 */
@RestResponse(allTo = "inventory")
public class APIReconcileNodeExtensionInfoEvent extends APIEvent {

    public APIReconcileNodeExtensionInfoEvent(String apiId) {
        super(apiId);
    }

    public APIReconcileNodeExtensionInfoEvent() {}
}
