package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2017/10/11
 */
@Action(services = {"tunnel"}, category = TunnelConstant.ACTION_CATEGORY, names = {"delete"}, adminOnly = true)
public class APIDeleteForciblyTunnelMsg extends APIMessage {
    @APIParam(emptyString = false,resourceType = TunnelVO.class, checkAccount = true)
    private String uuid;
    @APIParam(emptyString = false,maxLength = 32)
    private String accountUuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }
}
