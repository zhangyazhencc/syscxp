package org.zstack.billing.header.renew;

import org.zstack.billing.manage.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

@Action(category = BillingConstant.ACTION_CATEGORY, names = {"read", "renew"})
@AutoQuery(replyClass = APIQueryRenewReply.class, inventoryClass = RenewInventory.class)
public class APIQueryRenewMsg extends APIQueryMessage {
}
