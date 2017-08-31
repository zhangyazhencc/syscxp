package org.zstack.billing.header.identity.receipt;

import org.zstack.billing.manage.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

@Action(category = BillingConstant.ACTION_CATEGORY, names = {"read", "receipt"})
@AutoQuery(replyClass = APIQueryReceiptPostAddressReply.class, inventoryClass = ReceiptPostAddressInventory.class)
public class APIQueryReceiptPostAddressMsg extends APIQueryMessage {
}
