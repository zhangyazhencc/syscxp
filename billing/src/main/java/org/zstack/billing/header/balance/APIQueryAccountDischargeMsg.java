package org.zstack.billing.header.balance;

import org.zstack.header.billing.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

@Action(category = BillingConstant.ACTION_CATEGORY_ACCOUNT, names = {"read"})
@AutoQuery(replyClass = APIQueryAccountDischargeReply.class, inventoryClass = AccountDischargeInventory.class)
public class APIQueryAccountDischargeMsg extends APIQueryMessage {
}
