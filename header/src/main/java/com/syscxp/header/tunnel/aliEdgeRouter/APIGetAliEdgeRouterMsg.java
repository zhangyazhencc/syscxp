package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.tunnel.AliEdgeRouterConstant;
import com.syscxp.header.tunnel.TunnelConstant;

@Action(services = {"tunnel"}, category = AliEdgeRouterConstant.ACTION_CATEGORY, names = {"read"})
public class APIGetAliEdgeRouterMsg extends APISyncCallMessage {
    @APIParam(emptyString = false, checkAccount = true, resourceType=AliEdgeRouterVO.class)
    private String uuid;
    @APIParam(emptyString = false,required = false,maxLength = 32)
    private String aliAccessKeyID;
    @APIParam(emptyString = false,required = false,maxLength = 32)
    private String aliAccessKeySecret;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAliAccessKeyID() {
        return aliAccessKeyID;
    }

    public void setAliAccessKeyID(String aliAccessKeyID) {
        this.aliAccessKeyID = aliAccessKeyID;
    }

    public String getAliAccessKeySecret() {
        return aliAccessKeySecret;
    }

    public void setAliAccessKeySecret(String aliAccessKeySecret) {
        this.aliAccessKeySecret = aliAccessKeySecret;
    }
}
