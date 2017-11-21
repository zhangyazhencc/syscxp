package com.syscxp.account.header.identity;

import com.syscxp.account.header.account.AccountConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

/**
 * Created by wangwg on 2017/08/15.
 */
@AutoQuery(replyClass = APIQueryRoleReply.class, inventoryClass = RoleInventory.class)
@Action(services = {AccountConstant.ACTION_SERVICE}, category = AccountConstant.ACTION_CATEGORY_ACCOUNT, names = {"read"}, accountOnly = true)
public class APIQueryRoleMsg extends APIQueryMessage {

}
