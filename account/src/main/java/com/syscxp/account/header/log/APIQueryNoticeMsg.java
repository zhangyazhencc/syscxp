package com.syscxp.account.header.log;

import com.syscxp.account.header.account.AccountConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.SuppressUserCredentialCheck;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

@Action(services = {AccountConstant.ACTION_SERVICE}, category = AccountConstant.ACTION_CATEGORY_ACCOUNT, names = {"read"})
@SuppressUserCredentialCheck
@AutoQuery(replyClass = APIQueryNoticeReply.class, inventoryClass = NoticeInventory.class)
public class APIQueryNoticeMsg extends APIQueryMessage {
}
