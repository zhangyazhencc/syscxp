package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.AliEdgeRouterConstant;
import com.syscxp.header.tunnel.TunnelConstant;

@Action(services = {"tunnel"}, category = AliEdgeRouterConstant.ACTION_CATEGORY, names = {"read"})
@AutoQuery(replyClass = APIQueryAliEdgeRouterReply.class, inventoryClass = AliEdgeRouterInventory.class)
public class APIQueryAliEdgeRouterMsg extends APIQueryMessage{
}
