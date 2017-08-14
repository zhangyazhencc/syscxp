package org.zstack.account.header.log;

import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

@AutoQuery(replyClass = APIQueryOperLogReply.class, inventoryClass = OperLogInventory.class)
public class APIQueryOperLogMsg extends APIQueryMessage {
}
