package org.zstack.account.header.log;

import org.zstack.account.log.NoticeConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;
@Action(category = NoticeConstant.ACTION_CATEGORY, names = {"notice"})
@AutoQuery(replyClass = APIQueryNoticeReply.class, inventoryClass = NoticeInventory.class)
public class APIQueryNoticeMsg extends APIQueryMessage {
}
