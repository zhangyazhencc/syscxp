package com.syscxp.header.tunnel.network;

import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

@AutoQuery(replyClass = APIQueryL3EndPointReply.class, inventoryClass = L3EndPointInventory.class)
public class APIQueryL3EndPointMsg extends APIQueryMessage {

}
