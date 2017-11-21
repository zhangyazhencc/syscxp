package com.syscxp.billing.header.receipt;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

@Action(services = {BillingConstant.ACTION_SERVICE}, category = BillingConstant.ACTION_CATEGORY_RECEIPT, names = {"read"})
@AutoQuery(replyClass = APIQueryReceiptReply.class, inventoryClass = ReceiptInventory.class)
public class APIQueryReceiptMsg extends APIQueryMessage {
}
