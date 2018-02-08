package com.syscxp.header.tunnel.network;

import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

@AutoQuery(replyClass = APIQueryL3RouteReply.class, inventoryClass = L3RouteInventory.class)
public class APIQueryL3RouteMsg extends APIQueryMessage {

}
