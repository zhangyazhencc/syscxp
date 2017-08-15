package org.zstack.billing.header.identity.receipt;

import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

@AutoQuery(replyClass = APIQueryReceiptReply.class, inventoryClass = ReceiptInventory.class)
public class APIQueryReceiptMsg extends APIQueryMessage {
}
