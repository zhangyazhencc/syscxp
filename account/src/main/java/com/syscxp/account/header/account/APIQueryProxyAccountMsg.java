package com.syscxp.account.header.account;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

/**
 * Created by wangeg on 2017/09/26.
 */
@AutoQuery(replyClass = APIQueryProxyAccountReply.class, inventoryClass = ProxyAccountInventory.class)
@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT, names = {"read"})
public class APIQueryProxyAccountMsg extends APIQueryMessage{

}
