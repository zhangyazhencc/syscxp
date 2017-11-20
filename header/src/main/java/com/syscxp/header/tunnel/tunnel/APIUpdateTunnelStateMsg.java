package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2017/10/31
 */
@Action(services = {"tunnel"}, category = TunnelConstant.ACTION_CATEGORY, names = {"update"})
public class APIUpdateTunnelStateMsg extends APIMessage {
    @APIParam(emptyString = false,resourceType = TunnelVO.class, checkAccount = true)
    private String uuid;
    @APIParam(emptyString = false,validValues = {"Enabled", "Disabled"})
    private TunnelState state;
    @APIParam
    private boolean saveOnly;
    @APIParam
    private boolean unsupport;

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

    public boolean isSaveOnly() {
        return saveOnly;
    }

    public void setSaveOnly(boolean saveOnly) {
        this.saveOnly = saveOnly;
    }

    public boolean isUnsupport() {
        return unsupport;
    }

    public void setUnsupport(boolean unsupport) {
        this.unsupport = unsupport;
    }
}
