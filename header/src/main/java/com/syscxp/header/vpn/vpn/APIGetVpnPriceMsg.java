package com.syscxp.header.vpn.vpn;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

@Action(services = {VpnConstant.ACTION_SERVICE}, category = VpnConstant.ACTION_CATEGORY_VPN, names = {"read"})
public class APIGetVpnPriceMsg extends APISyncCallMessage {
    @APIParam
    private String bandwidthOfferingUuid;
    @APIParam(numberRange = {1,Integer.MAX_VALUE})
    private int duration;

    public int getDuration() {
        return duration;
    }
    @APIParam(required = false)
    private String accountUuid;

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getAccountUuid() {
        if (getSession().isAdminSession())
            return accountUuid;
        return getSession().getAccountUuid();
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getBandwidthOfferingUuid() {
        return bandwidthOfferingUuid;
    }

    public void setBandwidthOfferingUuid(String bandwidthOfferingUuid) {
        this.bandwidthOfferingUuid = bandwidthOfferingUuid;
    }
}
