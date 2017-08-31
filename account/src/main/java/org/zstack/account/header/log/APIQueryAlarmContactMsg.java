package org.zstack.account.header.log;

import org.zstack.account.log.NoticeConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;
@Action(category = NoticeConstant.ACTION_CATEGORY, names = {"alarm_contact"}, adminOnly = true)
@AutoQuery(replyClass = APIQueryAlarmContactReply.class, inventoryClass = AlarmContactInventory.class)
public class APIQueryAlarmContactMsg extends APIQueryMessage{
}
