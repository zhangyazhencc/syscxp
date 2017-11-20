package com.syscxp.billing.header.bill;

import com.syscxp.billing.header.APIQueryExpendMessage;
import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.query.AutoQuery;

@Action(category = BillingConstant.ACTION_CATEGORY_BILLING, names = {"read"})
@AutoQuery(replyClass = APIQueryBillReply.class, inventoryClass = BillInventory.class)
public class APIQueryBillMsg extends APIQueryExpendMessage {

}
