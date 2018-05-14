package com.syscxp.header.vpn.monitor;

import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.vpn.vpn.VpnConstant;
import com.syscxp.header.vpn.vpn.VpnVO;

@Action(services = {VpnConstant.ACTION_SERVICE}, category = VpnConstant.ACTION_CATEGORY_VPN, names = {"read"})
public class APIQueryOpentsdbConditionMsg extends APISyncCallMessage {
    @APIParam(resourceType = VpnVO.class)
    private String vpnUuid;

    public String getVpnUuid() {
        return vpnUuid;
    }

    public void setVpnUuid(String vpnUuid) {
        this.vpnUuid = vpnUuid;
    }

}
