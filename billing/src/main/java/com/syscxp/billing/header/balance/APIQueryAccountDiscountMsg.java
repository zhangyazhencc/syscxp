package com.syscxp.billing.header.balance;

import com.syscxp.billing.header.APIQueryExpendMessage;
import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.query.AutoQuery;

@Action(category = BillingConstant.ACTION_CATEGORY_ACCOUNT, names = {"read"})
@AutoQuery(replyClass = APIQueryAccountDiscountReply.class, inventoryClass = AccountDiscountInventory.class)
public class APIQueryAccountDiscountMsg extends APIQueryExpendMessage {
}
