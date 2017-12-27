package com.syscxp.header.tunnel.node;

import com.syscxp.header.message.APIEvent;

/**
 * Created by wangwg on 2017/12/27
 */
public class APIUploadImageUrlEvent extends APIEvent {
    public APIUploadImageUrlEvent() {
    }

    public APIUploadImageUrlEvent(String apiId) {
        super(apiId);
    }
}


