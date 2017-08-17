package org.zstack.billing.manage;

import org.zstack.header.errorcode.ErrorCode;

public class BillingServiceException extends RuntimeException{
    private ErrorCode error;

    public BillingServiceException(ErrorCode err) {
        this.error = err;
    }

    @Override
    public String getMessage() {
        return error.toString();
    }

    public ErrorCode getError() {
        return error;
    }

    public void setError(ErrorCode error) {
        this.error = error;
    }
}
