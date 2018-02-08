package com.syscxp.tunnel.network;

import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.message.APIMessage;

public class L3NetworkApiInterceptor implements ApiMessageInterceptor {
    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        return msg;
    }
}
