package com.syscxp.billing.header.balance;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

@Action(category = BillingConstant.ACTION_CATEGORY_ACCOUNT, names = {"read"})
@AutoQuery(replyClass = APIQueryAccountDischargeReply.class, inventoryClass = AccountDischargeInventory.class)
public class APIQueryAccountDischargeMsg extends APIQueryMessage {
}
