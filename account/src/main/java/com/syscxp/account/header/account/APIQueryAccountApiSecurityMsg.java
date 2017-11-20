package com.syscxp.account.header.account;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

/**
 * Created by wangwg on 2017/08/18.
 */
@AutoQuery(replyClass = APIQueryAccountApiSecurityReply.class, inventoryClass = AccountApiSecurityInventory.class)
@Action(services = {"account"}, category = AccountConstant.ACTION_CATEGORY_ACCOUNT, names = {"read"})
public class APIQueryAccountApiSecurityMsg extends APIQueryMessage {
}
