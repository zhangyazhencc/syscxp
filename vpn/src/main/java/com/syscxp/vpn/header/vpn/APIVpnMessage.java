package com.syscxp.vpn.header.vpn;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

public class APIVpnMessage extends APIMessage {
    @APIParam(required = false)
    private String accountUuid;

    public String getAccountUuid() {
        if (accountUuid == null || accountUuid.isEmpty())
            return getOpAccountUuid();
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getOpAccountUuid() {
        return getSession().getAccountUuid();
    }
}
