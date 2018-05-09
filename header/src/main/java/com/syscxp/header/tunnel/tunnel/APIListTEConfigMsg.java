package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2018/5/9
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = TunnelConstant.ACTION_CATEGORY, names = {"read"}, adminOnly = true)
public class APIListTEConfigMsg extends APISyncCallMessage {

    @APIParam(emptyString = false, resourceType = TunnelVO.class)
    private String tunnelUuid;

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }
}
