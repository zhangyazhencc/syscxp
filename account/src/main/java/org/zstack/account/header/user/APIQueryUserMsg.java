package org.zstack.account.header.user;

import org.zstack.account.header.account.AccountConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

/**
 * Created by frank on 7/14/2015.
 */
@AutoQuery(replyClass = APIQueryUserReply.class, inventoryClass = UserInventory.class)
@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT, names = {"read"}, accountOnly = true)
public class APIQueryUserMsg extends APIQueryMessage {

}
