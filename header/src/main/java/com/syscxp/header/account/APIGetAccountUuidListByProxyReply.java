package com.syscxp.header.account;

import com.syscxp.header.message.APIReply;

import java.util.List;

public class APIGetAccountUuidListByProxyReply extends APIReply {

    private List<String> accountUuids;

    public List<String> getAccountUuids() {
        return accountUuids;
    }

    public void setAccountUuids(List<String> accountUuids) {
        this.accountUuids = accountUuids;
    }
}
