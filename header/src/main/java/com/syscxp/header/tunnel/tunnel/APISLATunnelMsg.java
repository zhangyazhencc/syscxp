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
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = TunnelConstant.ACTION_CATEGORY, names = {"update"})
public class APISLATunnelMsg extends APISyncCallMessage {
    @APIParam(emptyString = false, resourceType = TunnelVO.class)
    private String uuid;

    @APIParam
    private String slaUuid;
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

    public String getSlaUuid() {
        return slaUuid;
    }

    public void setSlaUuid(String slaUuid) {
        this.slaUuid = slaUuid;
    }
}
