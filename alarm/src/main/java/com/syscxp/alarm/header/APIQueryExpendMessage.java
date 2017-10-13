package com.syscxp.alarm.header;

import com.syscxp.header.query.APIQueryMessage;

public class APIQueryExpendMessage extends APIQueryMessage {

    private String  accountUuid;

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }
}
