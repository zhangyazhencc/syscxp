package com.syscxp.header.host;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.rest.RestRequest;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.vpn.vpn.VpnConstant;
import org.springframework.http.HttpMethod;

@Action(services = {TunnelConstant.ACTION_SERVICE, VpnConstant.ACTION_SERVICE}, category = HostConstant.ACTION_CATEGORY, names = {"read"}, adminOnly = true)
public class APIGetHostTypesMsg extends APISyncCallMessage {
 
    public static APIGetHostTypesMsg __example__() {
        APIGetHostTypesMsg msg = new APIGetHostTypesMsg();


        return msg;
    }

}
