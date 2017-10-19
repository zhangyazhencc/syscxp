package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.tunnel.manage.TunnelConstant;

/**
 * Create by DCY on 2017/10/11
 */
@Action(category = TunnelConstant.ACTION_CATEGORY, names = {"update"})
public class APIUpdateTunnelStateMsg extends APIMessage {
    @APIParam(emptyString = false,resourceType = TunnelVO.class, checkAccount = true)
    private String uuid;
    @APIParam(emptyString = false,validValues = {"Enabled", "Disabled","Unpaid"})
    private TunnelState state;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public TunnelState getState() {
        return state;
    }

    public void setState(TunnelState state) {
        this.state = state;
    }
}
