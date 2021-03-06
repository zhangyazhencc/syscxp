package com.syscxp.header.host;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

import java.util.List;

import static java.util.Arrays.asList;

@Action(services = {HostConstant.TUNNEL_ACTION_SERVICE, HostConstant.VPN_ACTION_SERVICE}, category = HostConstant.ACTION_CATEGORY, names = {"read"}, adminOnly = true)
@AutoQuery(replyClass = APIQueryHostReply.class, inventoryClass = HostInventory.class)
public class APIQueryHostMsg extends APIQueryMessage {

    public static List<String> __example__() {
        return asList("uuid="+uuid());
    }

}
