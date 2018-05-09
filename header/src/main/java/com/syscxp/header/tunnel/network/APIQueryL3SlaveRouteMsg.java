package com.syscxp.header.tunnel.network;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.L3NetWorkConstant;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2018/4/19
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = L3NetWorkConstant.ACTION_CATEGORY, names = {"read"})
@AutoQuery(replyClass = APIQueryL3SlaveRouteReply.class, inventoryClass = L3SlaveRouteInventory.class)
public class APIQueryL3SlaveRouteMsg extends APIQueryMessage {
}
