package com.syscxp.header.apimediator;

import com.syscxp.header.errorcode.ErrorCode;

public class CloudNoRouteFoundException extends Exception {
    private ErrorCode error;

    public CloudNoRouteFoundException() {
    }

    public CloudNoRouteFoundException(ErrorCode error) {
        this.error = error;
    }

    public ErrorCode getError() {
        return error;
    }

    public void setError(ErrorCode error) {
        this.error = error;
    }
}
