package com.syscxp.header.billing;

import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;


public class APICreateOrderMsg extends APISyncCallMessage {

    @APIParam(emptyString = false)
    private String accountUuid;

    @APIParam(emptyString = false,required = false)
    private String opAccountUuid;

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
