package org.zstack.billing.header.order;

import org.zstack.billing.APIQueryExpendMessage;
import org.zstack.header.billing.BillingConstant;
import org.zstack.header.billing.OrderInventory;
import org.zstack.header.identity.Action;
import org.zstack.header.query.AutoQuery;

@Action(category = BillingConstant.ACTION_CATEGORY_ORDER, names = {"read"})
@AutoQuery(replyClass = APIQueryOrderReply.class, inventoryClass = OrderInventory.class)
public class APIQueryOrderMsg extends APIQueryExpendMessage {



}
