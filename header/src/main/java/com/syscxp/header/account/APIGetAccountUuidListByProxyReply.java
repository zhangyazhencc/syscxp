package com.syscxp.header.account;

import com.syscxp.header.message.APIReply;

import java.util.List;

public class APIGetAccountUuidListByProxyReply extends APIReply {

    private List<String> accountUuidBoundToProxy;

    public List<String> getAccountUuidBoundToProxy() {
        return accountUuidBoundToProxy;
    }

    public void setAccountUuidBoundToProxy(List<String> accountUuidBoundToProxy) {
        this.accountUuidBoundToProxy = accountUuidBoundToProxy;
    }
}
