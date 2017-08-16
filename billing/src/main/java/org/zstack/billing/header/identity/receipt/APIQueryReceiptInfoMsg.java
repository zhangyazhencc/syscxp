package org.zstack.billing.header.identity.receipt;

import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

@AutoQuery(replyClass = APIQueryReceiptInfoReply.class, inventoryClass = ReceiptInfoInventory.class)
public class APIQueryReceiptInfoMsg extends APIQueryMessage{
}
