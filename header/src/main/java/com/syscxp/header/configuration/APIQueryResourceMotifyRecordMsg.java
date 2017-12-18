package com.syscxp.header.configuration;

import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

@AutoQuery(replyClass = APIQueryResourceMotifyRecordReply.class, inventoryClass = ResourceMotifyRecordInventory.class)
public class APIQueryResourceMotifyRecordMsg extends APIQueryMessage {
}
