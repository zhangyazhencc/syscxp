package com.syscxp.account.header.log;

import com.syscxp.account.header.account.AccountConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

@Action(services = {"account"}, category = AccountConstant.ACTION_CATEGORY_ACCOUNT, names = {"read"})
@AutoQuery(replyClass = APIQueryNoticeReply.class, inventoryClass = NoticeInventory.class)
public class APIQueryNoticeMsg extends APIQueryMessage {
}
