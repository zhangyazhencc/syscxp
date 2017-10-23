package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

@Action(adminOnly = true,category = AlarmConstant.ACTION_CATEGORY_RESOURCE_POLICY)
@AutoQuery(replyClass = APIQueryResourceReply.class, inventoryClass = ResourceInventory.class)
public class APIQueryResourceMsg  extends APIQueryMessage{
}