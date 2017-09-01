package org.zstack.billing.header.renew;

import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

@AutoQuery(replyClass = APIQueryRenewReply.class, inventoryClass = RenewInventory.class)
public class APIQueryRenewMsg extends APIQueryMessage {
}
