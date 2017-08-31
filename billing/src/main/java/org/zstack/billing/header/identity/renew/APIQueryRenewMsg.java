package org.zstack.billing.header.identity.renew;

import org.zstack.billing.manage.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

@Action(category = BillingConstant.ACTION_CATEGORY, names = {"read", "renew"}, accountOnly = true)
@AutoQuery(replyClass = APIQueryRenewReply.class, inventoryClass = RenewInventory.class)
public class APIQueryRenewMsg extends APIQueryMessage {
}
