package org.zstack.account.header.identity;

import org.zstack.account.header.account.AccountConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

/**
 * Created by wangwg on 2017/08/15.
 */
@AutoQuery(replyClass = APIQueryRoleReply.class, inventoryClass = RoleInventory.class)
@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT, names = {"read"}, accountOnly = true)
public class APIQueryRoleMsg extends APIQueryMessage {

}