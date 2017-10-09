package com.syscxp.billing.header.receipt;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

@Action(category = BillingConstant.ACTION_CATEGORY_RECEIPT, names = {"read"})
@AutoQuery(replyClass = APIQueryReceiptInfoReply.class, inventoryClass = ReceiptInfoInventory.class)
public class APIQueryReceiptInfoMsg extends APIQueryMessage {
}
