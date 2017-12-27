package com.syscxp.rest;

import com.syscxp.header.message.APIEvent;

/**
 * Project: syscxp
 * Package: com.syscxp.rest
 * Date: 2017/12/26 15:06
 * Author: wj
 */
public class AsyncRestQueryResult {
    private String uuid;
    private AsyncRestState state;
    private APIEvent result;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public AsyncRestState getState() {
        return state;
    }

    public void setState(AsyncRestState state) {
        this.state = state;
    }

    public APIEvent getResult() {
        return result;
    }

    public void setResult(APIEvent result) {
        this.result = result;
    }
}

