package com.syscxp.account.header.identity;

import com.syscxp.account.header.account.AccountConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

/**
 * Created by wangwg on 2017/08/15.
 */
@AutoQuery(replyClass = APIQueryPolicyReply.class, inventoryClass = PolicyInventory.class)
@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT, names = {"read"}, accountOnly = true)
public class APIQueryPolicyMsg extends APIQueryMessage {

}
