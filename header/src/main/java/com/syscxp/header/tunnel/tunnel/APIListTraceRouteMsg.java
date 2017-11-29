package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2017/11/28
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = TunnelConstant.ACTION_CATEGORY, names = {"read"}, adminOnly = true)
public class APIListTraceRouteMsg extends APISyncCallMessage {

    @APIParam(emptyString = false, resourceType = TunnelVO.class)
    private String tunnelUuid;

    @APIParam
    private boolean traceAgain;

    public boolean isTraceAgain() {
        return traceAgain;
    }

    public void setTraceAgain(boolean traceAgain) {
        this.traceAgain = traceAgain;
    }

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }
}
