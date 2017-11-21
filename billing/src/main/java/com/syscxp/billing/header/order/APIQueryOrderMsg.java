package com.syscxp.billing.header.order;

import com.syscxp.billing.header.APIQueryExpendMessage;
import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.billing.OrderInventory;
import com.syscxp.header.identity.Action;
import com.syscxp.header.query.AutoQuery;

@Action(services = {BillingConstant.ACTION_SERVICE}, category = BillingConstant.ACTION_CATEGORY_ORDER, names = {"read"})
@AutoQuery(replyClass = APIQueryOrderReply.class, inventoryClass = OrderInventory.class)
public class APIQueryOrderMsg extends APIQueryExpendMessage {



}
