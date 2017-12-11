package com.syscxp.vpn.exception;

import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.errorcode.OperationFailureException;

public class VpnServiceException extends OperationFailureException{
    private ErrorCode errorCode;

    @Override
    public String getMessage() {
        return String.format("%s: %s", VpnServiceException.class.getName(), errorCode);
    }

    public VpnServiceException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
