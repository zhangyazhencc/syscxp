package com.syscxp.header.apimediator;

import com.syscxp.header.errorcode.ErrorCode;

public class CloudApiMessageInterceptorException extends Exception {
    private ErrorCode errorCode;

    public CloudApiMessageInterceptorException(ErrorCode errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
