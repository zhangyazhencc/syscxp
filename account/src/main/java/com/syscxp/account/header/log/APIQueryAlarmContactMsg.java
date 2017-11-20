package com.syscxp.account.header.log;

import com.syscxp.account.header.account.AccountConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

@AutoQuery(replyClass = APIQueryAlarmContactReply.class, inventoryClass = AlarmContactInventory.class)
public class APIQueryAlarmContactMsg extends APIQueryMessage{
}
