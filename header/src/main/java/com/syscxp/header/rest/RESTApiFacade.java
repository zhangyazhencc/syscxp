package com.syscxp.header.rest;

import com.syscxp.header.message.APIMessage;

public interface RESTApiFacade {
    RestAPIResponse send(APIMessage msg);

    RestAPIResponse call(APIMessage msg);

    RestAPIResponse getResult(String uuid);
}
