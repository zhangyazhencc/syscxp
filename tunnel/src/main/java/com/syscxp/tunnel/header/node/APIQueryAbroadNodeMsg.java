package com.syscxp.tunnel.header.node;

import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

@AutoQuery(replyClass = APIQueryAbroadNodeReply.class, inventoryClass = AbroadNodeInventory.class)
public class APIQueryAbroadNodeMsg extends APIQueryMessage{
}
