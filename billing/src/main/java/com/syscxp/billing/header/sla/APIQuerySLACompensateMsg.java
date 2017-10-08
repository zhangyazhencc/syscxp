package com.syscxp.billing.header.sla;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
@Action(category = BillingConstant.ACTION_CATEGORY_SLA, names = {"read"})
@AutoQuery(replyClass = APIQuerySLACompensateReply.class, inventoryClass = SLACompensateInventory.class)
public class APIQuerySLACompensateMsg  extends APIQueryMessage {
}
