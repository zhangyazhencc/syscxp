package org.zstack.billing.header.balance;

import org.zstack.billing.manage.APIQueryExpendMessage;
import org.zstack.billing.manage.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIParam;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

@Action(category = BillingConstant.ACTION_CATEGORY_ACCOUNT, names = {"read"})
@AutoQuery(replyClass = APIQueryDealDetailReply.class, inventoryClass = DealDetailInventory.class)
public class APIQueryDealDetailMsg extends APIQueryExpendMessage {

}