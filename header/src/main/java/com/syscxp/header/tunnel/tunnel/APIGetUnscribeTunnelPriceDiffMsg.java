package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.identity.AccountType;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2017/11/20
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = TunnelConstant.ACTION_CATEGORY, names = {"read"})
public class APIGetUnscribeTunnelPriceDiffMsg extends APISyncCallMessage {
    @APIParam(emptyString = false,resourceType = TunnelVO.class, checkAccount = true)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
