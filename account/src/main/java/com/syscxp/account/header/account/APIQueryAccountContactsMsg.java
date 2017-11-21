package com.syscxp.account.header.account;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

/**
 * Created by wangwg on 2017/08/21.
 */
@AutoQuery(replyClass = APIQueryAccountContactsReply.class, inventoryClass = AccountContactsInventory.class)
@Action(services = {AccountConstant.ACTION_SERVICE}, category = AccountConstant.ACTION_CATEGORY_ACCOUNT, names = {"read"})
public class APIQueryAccountContactsMsg extends APIQueryMessage {

}
