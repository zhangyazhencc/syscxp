package org.zstack.account.header.log;

import org.zstack.account.header.identity.AccountConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;
@Action(category = AccountConstant.ACTION_CATEGORY, names = {"alarm_contact"}, accountOnly = true)
@AutoQuery(replyClass = APIQueryAlarmContactReply.class, inventoryClass = AlarmContactInventory.class)
public class APIQueryAlarmContactMsg extends APIQueryMessage{
}
