package org.zstack.account.header.account;

import org.zstack.header.identity.Action;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

/**
 * Created by wangeg on 2017/09/26.
 */
@AutoQuery(replyClass = APIQueryProxyAccountReply.class, inventoryClass = ProxyAccountInventory.class)
@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT, names = {"read"})
public class APIQueryProxyAccountMsg extends APIQueryMessage{

}
