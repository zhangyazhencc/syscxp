package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

@AutoQuery(replyClass = APIQueryAliUserReply.class,inventoryClass = AliEdgeRouterConfigInventory.class)
public class APIQueryAliEdgeRouterConfigMsg extends APIQueryMessage {
}
