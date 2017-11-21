package com.syscxp.header.vpn.vpn;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.NoJsonSchema;
import com.syscxp.header.rest.APINoSee;

public class APIVpnMessage extends APIMessage {

    @NoJsonSchema
    @APINoSee
    private String hostUuid;

    @APIParam(required = false)
    private String accountUuid;

    public String getHostUuid() {
        return hostUuid;
    }

    public void setHostUuid(String hostUuid) {
        this.hostUuid = hostUuid;
    }

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
