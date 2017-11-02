package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.message.APIEvent;

public class APIDeleteResourceEvent extends APIEvent{
    private String msg;
    private Boolean success;

    public APIDeleteResourceEvent() {}

    public APIDeleteResourceEvent(String apiId) {
        super(apiId);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
