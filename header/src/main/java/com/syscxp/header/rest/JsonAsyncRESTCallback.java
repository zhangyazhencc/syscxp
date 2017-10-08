package com.syscxp.header.rest;

import com.syscxp.header.core.AsyncBackup;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.exception.CloudRuntimeException;
import org.springframework.http.HttpEntity;

public abstract class JsonAsyncRESTCallback<T> extends AsyncRESTCallback {
    public JsonAsyncRESTCallback(AsyncBackup one, AsyncBackup... others) {
        super(one, others);
    }

    public abstract void fail(ErrorCode err);

    public abstract void success(T ret);

    public abstract Class<T> getReturnClass();

    @Override
    public final void success(HttpEntity<String> entity) {
        throw new CloudRuntimeException("this method should not be called");
    }
}
