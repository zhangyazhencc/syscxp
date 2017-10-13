package com.syscxp.alarm;

import com.syscxp.header.errorcode.ErrorCode;

public class AlarmServiceException extends RuntimeException{
    private ErrorCode error;

    public AlarmServiceException(ErrorCode err) {
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
