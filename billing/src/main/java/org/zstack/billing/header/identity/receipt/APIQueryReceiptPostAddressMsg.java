package org.zstack.billing.header.identity.receipt;

import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

@AutoQuery(replyClass = APIQueryReceiptPostAddressReply.class, inventoryClass = ReceiptPostAddressInventory.class)
public class APIQueryReceiptPostAddressMsg  extends APIQueryMessage {
}
