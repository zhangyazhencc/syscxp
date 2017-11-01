package com.syscxp.vpn.header.vpn;

import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

@SuppressCredentialCheck
public class APIGetVpnPriceMsg extends APISyncCallMessage {
    @APIParam
    private Long bandwidth;
    @APIParam(numberRange = {1,Integer.MAX_VALUE})
    private int duration;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
    }
}
