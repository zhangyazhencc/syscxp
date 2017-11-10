package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

@AutoQuery(replyClass = APIQueryAliUserReply.class, inventoryClass = AliUserInventory.class)
public class APIQueryAliUserMsg extends APIQueryMessage{
}
