package org.zstack.account.header.identity;

import org.zstack.header.identity.Action;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

/**
 * Created by wangwg on 2017/08/18.
 */
@AutoQuery(replyClass = APIQueryAccountApiSecurityReply.class, inventoryClass = AccountApiSecurityInventory.class)
@Action(category = AccountConstant.ACTION_CATEGORY, supportOnly = true)
public class APIQueryAccountApiSecurityMsg extends APIQueryMessage {
}
