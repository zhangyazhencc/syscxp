package com.syscxp.header.host;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.vpn.vpn.VpnConstant;

@Action(services = {TunnelConstant.ACTION_SERVICE, VpnConstant.ACTION_SERVICE}, category = HostConstant.ACTION_CATEGORY, names = {"read"}, adminOnly = true)
public class APIGetHostMsg extends APISyncCallMessage {

    @APIParam(resourceType = HostVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
 
    public static APIGetHostMsg __example__() {
        APIGetHostMsg msg = new APIGetHostMsg();


        return msg;
    }

}
