package org.zstack.billing.header.receipt;

import org.zstack.header.billing.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

@Action(category = BillingConstant.ACTION_CATEGORY_RECEIPT, names = {"read"})
@AutoQuery(replyClass = APIQueryReceiptInfoReply.class, inventoryClass = ReceiptInfoInventory.class)
public class APIQueryReceiptInfoMsg extends APIQueryMessage {
}
