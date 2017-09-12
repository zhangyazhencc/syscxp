package org.zstack.billing.header.receipt;

import org.zstack.billing.manage.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

@Action(category = BillingConstant.ACTION_CATEGORY_RECEIPT, names = {"read"})
@AutoQuery(replyClass = APIQueryReceiptPostAddressReply.class, inventoryClass = ReceiptPostAddressInventory.class)
public class APIQueryReceiptPostAddressMsg extends APIQueryMessage {
}