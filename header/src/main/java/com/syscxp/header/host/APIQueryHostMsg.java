package com.syscxp.header.host;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.vpn.VpnConstant;

import java.util.List;

import static java.util.Arrays.asList;

@Action(services = {TunnelConstant.ACTION_SERVICE, VpnConstant.ACTION_SERVICE}, category = HostConstant.ACTION_CATEGORY, names = {"read"}, adminOnly = true)
@AutoQuery(replyClass = APIQueryHostReply.class, inventoryClass = HostInventory.class)
public class APIQueryHostMsg extends APIQueryMessage {

    public static List<String> __example__() {
        return asList("uuid="+uuid());
    }

}
