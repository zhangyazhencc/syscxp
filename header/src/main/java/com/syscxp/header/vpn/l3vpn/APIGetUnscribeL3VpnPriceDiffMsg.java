package com.syscxp.header.vpn.l3vpn;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.vpn.vpn.VpnConstant;

/**
 * Create by DCY on 2017/11/20
 */
@Action(services = {VpnConstant.ACTION_SERVICE}, category = VpnConstant.ACTION_CATEGORY_VPN, names = {"read"})
public class APIGetUnscribeL3VpnPriceDiffMsg extends APISyncCallMessage {
    @APIParam(emptyString = false, resourceType = L3VpnVO.class, checkAccount = true)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
