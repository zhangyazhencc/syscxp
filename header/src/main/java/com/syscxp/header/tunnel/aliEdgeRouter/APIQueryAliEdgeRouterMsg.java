package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

@AutoQuery(replyClass = APIQueryAliEdgeRouterReply.class, inventoryClass = AliEdgeRouterInventory.class)
public class APIQueryAliEdgeRouterMsg extends APIQueryMessage{
}
