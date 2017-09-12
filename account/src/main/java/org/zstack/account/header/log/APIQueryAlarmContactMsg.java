package org.zstack.account.header.log;

import org.zstack.account.header.account.AccountConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;
@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT, adminOnly = true)
@AutoQuery(replyClass = APIQueryAlarmContactReply.class, inventoryClass = AlarmContactInventory.class)
public class APIQueryAlarmContactMsg extends APIQueryMessage{
}
