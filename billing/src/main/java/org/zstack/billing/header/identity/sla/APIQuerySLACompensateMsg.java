package org.zstack.billing.header.identity.sla;

import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

@AutoQuery(replyClass = APIQuerySLACompensateReply.class, inventoryClass = SLACompensateInventory.class)
public class APIQuerySLACompensateMsg  extends APIQueryMessage {
}
