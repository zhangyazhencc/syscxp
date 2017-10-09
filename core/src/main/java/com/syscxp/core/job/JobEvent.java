package com.syscxp.core.job;

import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.message.LocalEvent;
import com.syscxp.utils.JsonWrapper;

public class JobEvent extends LocalEvent {
    private JsonWrapper returnValue;
    private ErrorCode errorCode;
    private boolean success = true;
    private long jobId;
    
    
    public long getJobId() {
        return jobId;
    }


    public void setJobId(long jobId) {
        this.jobId = jobId;
    }


    public ErrorCode getErrorCode() {
        return errorCode;
    }


    public void setErrorCode(ErrorCode errorCode) {
        this.success = false;
        this.errorCode = errorCode;
    }


    public boolean isSuccess() {
        return success;
    }


    public void setSuccess(boolean success) {
        this.success = success;
    }

    public JsonWrapper getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(JsonWrapper returnValue) {
        this.returnValue = returnValue;
    }

    @Override
    public String getSubCategory() {
        return "JobQueueReturnValue";
    }

}
