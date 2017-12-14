package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.AliEdgeRouterConstant;
import com.syscxp.header.tunnel.TunnelConstant;

@Action(adminOnly = true,services = {TunnelConstant.ACTION_SERVICE}, category = AliEdgeRouterConstant.ACTION_CATEGORY )
public class APIRecoverAliEdgeRouterMsg extends APIMessage {
    @APIParam(emptyString = false, checkAccount = true, resourceType = AliEdgeRouterVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
