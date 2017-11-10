package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;


@Action(category = AlarmConstant.ACTION_CATEGORY_RESOURCE_POLICY)
@AutoQuery(replyClass = APIQueryPolicyReply.class, inventoryClass = PolicyInventory.class)
public class APIQueryPolicyMsg extends APIQueryMessage {
}
