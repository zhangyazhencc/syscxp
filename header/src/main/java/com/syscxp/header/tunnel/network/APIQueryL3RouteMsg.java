package com.syscxp.header.tunnel.network;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.L3NetWorkConstant;
import com.syscxp.header.tunnel.TunnelConstant;

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = L3NetWorkConstant.ACTION_CATEGORY, names = {"read"})
@AutoQuery(replyClass = APIQueryL3RouteReply.class, inventoryClass = L3RouteInventory.class)
public class APIQueryL3RouteMsg extends APIQueryMessage {

}
