package com.syscxp.header.tunnel.network;

import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

@AutoQuery(replyClass = APIQueryL3NetworkReply.class, inventoryClass = L3NetworkInventory.class)
public class APIQueryL3NetworkMsg extends APIQueryMessage {

}
