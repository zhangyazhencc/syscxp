package org.zstack.account.header.identity;

import org.zstack.header.identity.Action;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

/**
 * Created by wangwg on 2017/08/15.
 */
@AutoQuery(replyClass = APIQueryPolicyReply.class, inventoryClass = PolicyInventory.class)
@Action(category = AccountConstant.ACTION_CATEGORY, names = {"read"})
public class APIQueryPolicyMsg extends APIQueryMessage {

}
