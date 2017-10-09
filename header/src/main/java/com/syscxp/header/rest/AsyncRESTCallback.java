package com.syscxp.header.rest;

import com.syscxp.header.core.AbstractCompletion;
import com.syscxp.header.core.AsyncBackup;
import com.syscxp.header.errorcode.ErrorCode;
import org.springframework.http.HttpEntity;

public abstract class AsyncRESTCallback extends AbstractCompletion {

    public AsyncRESTCallback(AsyncBackup one, AsyncBackup... others) {
        super(one, others);
    }

    public abstract void fail(ErrorCode err);

    public abstract void success(HttpEntity<String> responseEntity);
}
