package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.AliEdgeRouterConstant;

@Action(services = {"tunnel"}, category = AliEdgeRouterConstant.ACTION_CATEGORY, names = {"read"})
@AutoQuery(replyClass = APIQueryAliUserReply.class, inventoryClass = AliUserInventory.class)
public class APIQueryAliUserMsg extends APIQueryMessage{
}
