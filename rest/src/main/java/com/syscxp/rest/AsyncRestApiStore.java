package com.syscxp.rest;

import com.syscxp.header.message.APIEvent;

/**
 * Project: syscxp
 * Package: com.syscxp.rest
 * Date: 2017/12/26 15:05
 * Author: wj
 */
public interface AsyncRestApiStore {
    void save(RequestData data);

    RequestData complete(APIEvent evt);

    AsyncRestQueryResult query(String uuid);
}
