package com.syscxp.header.tunnel.node;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.NodeConstant;

/**
 * Created by DCY on 2017-08-21
 */
@Action(category = NodeConstant.ACTION_CATEGORY, names = {"read"})

@AutoQuery(replyClass = APIQueryNodeReply.class, inventoryClass = NodeInventory.class)
public class APIQueryNodeMsg extends APIQueryMessage {
}