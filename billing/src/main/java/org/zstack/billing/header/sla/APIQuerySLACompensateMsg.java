package org.zstack.billing.header.sla;

import org.zstack.billing.manage.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;
@Action(category = BillingConstant.ACTION_CATEGORY_SLA, names = {"read"})
@AutoQuery(replyClass = APIQuerySLACompensateReply.class, inventoryClass = SLACompensateInventory.class)
public class APIQuerySLACompensateMsg  extends APIQueryMessage {
}
