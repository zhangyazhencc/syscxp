package com.syscxp.billing.header.balance;

import com.syscxp.billing.header.APIQueryExpendMessage;
import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.query.AutoQuery;

@Action(category = BillingConstant.ACTION_CATEGORY_BILLING, names = {"read"})
@AutoQuery(replyClass = APIQueryDealDetailReply.class, inventoryClass = DealDetailInventory.class)
public class APIQueryDealDetailMsg extends APIQueryExpendMessage {

}
