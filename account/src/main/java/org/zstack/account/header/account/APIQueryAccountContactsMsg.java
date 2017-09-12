package org.zstack.account.header.account;

import org.zstack.header.identity.Action;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

/**
 * Created by wangwg on 2017/08/21.
 */
@AutoQuery(replyClass = APIQueryAccountContactsReply.class, inventoryClass = AccountContactsInventory.class)
@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT, names = {"read"})
public class APIQueryAccountContactsMsg extends APIQueryMessage {

}
