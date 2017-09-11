package org.zstack.billing.header.balance;

import org.zstack.billing.manage.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

@Action(category = BillingConstant.ACTION_CATEGORY, names = {"recharge", "read"})
@AutoQuery(replyClass = APIQueryDisChargeReply.class, inventoryClass = AccountDischargeInventory.class)
public class APIQueryDisChargeMsg  extends APIQueryMessage {
}
