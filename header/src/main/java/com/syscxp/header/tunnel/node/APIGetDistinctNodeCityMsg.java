package com.syscxp.header.tunnel.node;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.NodeConstant;

/**
 * Created by DCY on 2017-08-21
 */
@Action(services = {"tunnel"}, category = NodeConstant.ACTION_CATEGORY, names = {"read"}, adminOnly = true)

@AutoQuery(replyClass = APIQueryNodeReply.class, inventoryClass = NodeInventory.class)
public class APIGetDistinctNodeCityMsg extends APISyncCallMessage {
}
