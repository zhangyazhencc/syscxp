package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2017/11/15
 */
@InnerCredentialCheck
@Action(services = {"tunnel"}, category = TunnelConstant.ACTION_CATEGORY, names = {"update"})
public class APISalTunnelMsg extends APISyncCallMessage {
    @APIParam(emptyString = false, resourceType = TunnelVO.class)
    private String uuid;
    @APIParam
    private Integer duration;
    @APIParam(emptyString = false,maxLength = 32)
    private String accountUuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }
}
