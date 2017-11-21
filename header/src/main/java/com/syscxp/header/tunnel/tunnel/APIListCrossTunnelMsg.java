package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2017/11/20
 */
@Action(category = TunnelConstant.ACTION_CATEGORY, names = {"read"})
public class APIListCrossTunnelMsg extends APISyncCallMessage {
    @APIParam(emptyString = false, resourceType = InterfaceVO.class)
    private String uuid;
    @APIParam
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
