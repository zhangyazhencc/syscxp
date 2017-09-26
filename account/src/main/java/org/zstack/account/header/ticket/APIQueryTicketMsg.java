package org.zstack.account.header.ticket;

import org.zstack.account.header.account.AccountConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

/**
 * Created by wangwg on 2017/09/26.
 */
@AutoQuery(replyClass = APIQueryTicketReply.class, inventoryClass = TicketInventory.class)
@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT, names = {"read"})
public class APIQueryTicketMsg extends APIQueryMessage {

}
