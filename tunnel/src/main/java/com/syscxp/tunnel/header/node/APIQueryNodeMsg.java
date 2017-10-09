package com.syscxp.tunnel.header.node;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.tunnel.manage.NodeConstant;

/**
 * Created by DCY on 2017-08-21
 */
@Action(category = NodeConstant.ACTION_CATEGORY, names = {"read"}, adminOnly = true)

@AutoQuery(replyClass = APIQueryNodeReply.class, inventoryClass = NodeInventory.class)
public class APIQueryNodeMsg extends APIQueryMessage {
}
