package org.zstack.account.header.log;

import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

@AutoQuery(replyClass = APIQueryNoticeReply.class, inventoryClass = NoticeInventory.class)
public class APIQueryNoticeMsg extends APIQueryMessage {
}
