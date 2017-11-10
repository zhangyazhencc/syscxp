package com.syscxp.billing.header;

import com.syscxp.header.query.APIQueryMessage;

import java.util.List;

public class APIQueryExpendMessage extends APIQueryMessage {

    private List<String> accountUuids;

    public List<String> getAccountUuids() {
        return accountUuids;
    }

    public void setAccountUuids(List<String> accountUuids) {
        this.accountUuids = accountUuids;
    }
}
