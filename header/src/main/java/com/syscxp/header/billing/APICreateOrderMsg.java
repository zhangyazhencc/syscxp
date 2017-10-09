package com.syscxp.header.billing;

import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;


public class APICreateOrderMsg extends APISyncCallMessage {

    @APIParam(emptyString = false)
    private String accountUuid;

    @APIParam(emptyString = false,required = false)
    private String opAccountUuid;

    @APIParam(emptyString = false,required = true)
    private String notifyUrl;

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getOpAccountUuid() {
        return opAccountUuid;
    }

    public void setOpAccountUuid(String opAccountUuid) {
        this.opAccountUuid = opAccountUuid;
    }
}
