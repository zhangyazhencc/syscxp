package com.syscxp.header.tunnel.node;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.NodeConstant;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2018/5/3
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = NodeConstant.ACTION_CATEGORY, names = {"read"})
@AutoQuery(replyClass = APIQueryAccountNodeRefReply.class, inventoryClass = AccountNodeRefInventory.class)
public class APIQueryAccountNodeRefMsg extends APIQueryMessage {
}
