package org.zstack.account.header.identity;

import org.zstack.header.identity.Action;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

/**
 * Created by frank on 7/14/2015.
 */
@AutoQuery(replyClass = APIQueryAccountReply.class, inventoryClass = AccountInventory.class)
@Action(category = AccountConstant.ACTION_CATEGORY, supportOnly = true)
public class APIQueryAccountMsg extends APIQueryMessage {
}
