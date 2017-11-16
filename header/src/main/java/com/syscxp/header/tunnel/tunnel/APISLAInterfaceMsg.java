package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.tunnel.TunnelConstant;

@InnerCredentialCheck
@Action(category = TunnelConstant.ACTION_CATEGORY, names = {"update"})
public class APISLAInterfaceMsg extends APISyncCallMessage {

    @APIParam(emptyString = false, resourceType = InterfaceVO.class)
    private String uuid;

    @APIParam
    private Integer duration;

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

}
