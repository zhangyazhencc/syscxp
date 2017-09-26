package org.zstack.billing.header.balance;

import org.zstack.billing.APIQueryExpendMessage;
import org.zstack.header.billing.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.query.AutoQuery;

@Action(category = BillingConstant.ACTION_CATEGORY_ACCOUNT, names = {"read"})
@AutoQuery(replyClass = APIQueryDealDetailReply.class, inventoryClass = DealDetailInventory.class)
public class APIQueryDealDetailMsg extends APIQueryExpendMessage {

}
