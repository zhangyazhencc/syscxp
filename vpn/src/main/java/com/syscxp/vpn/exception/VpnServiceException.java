package com.syscxp.vpn.exception;

import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.errorcode.OperationFailureException;

/**
 * @author wangjie
 */
public class VpnServiceException extends OperationFailureException {
    private ErrorCode errorCode;

    @Override
    public String getMessage() {
        return String.format("%s: %s", VpnServiceException.class.getName(), errorCode);
    }

    public VpnServiceException(ErrorCode errorCode) {
        super(errorCode);
    }

    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    @Override
    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
