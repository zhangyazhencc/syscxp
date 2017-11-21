package com.syscxp.billing.header.balance;

import com.syscxp.billing.header.APIQueryExpendMessage;
import com.syscxp.header.billing.AccountDiscountInventory;
import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.query.AutoQuery;

@Action(services = {BillingConstant.ACTION_SERVICE}, category = BillingConstant.ACTION_CATEGORY_BILLING, names = {"read"})
@AutoQuery(replyClass = APIQueryAccountDiscountReply.class, inventoryClass = AccountDiscountInventory.class)
public class APIQueryAccountDiscountMsg extends APIQueryExpendMessage {
}
