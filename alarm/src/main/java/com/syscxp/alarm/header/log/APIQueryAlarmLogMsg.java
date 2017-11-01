package com.syscxp.alarm.header.log;

import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;


@AutoQuery(replyClass = APIQueryAlarmLogReply.class,inventoryClass = AlarmLogInventory.class)
public class APIQueryAlarmLogMsg extends APIQueryMessage{
}
