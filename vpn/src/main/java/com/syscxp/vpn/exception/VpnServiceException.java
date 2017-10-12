package com.syscxp.vpn.exception;

import com.syscxp.header.errorcode.ErrorCode;

public class VpnServiceException extends RuntimeException{
    private ErrorCode errorCode;

    @Override
    public String getMessage() {
        return String.format("%s: %s", VpnServiceException.class.getName(), errorCode);
    }

    public VpnServiceException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
