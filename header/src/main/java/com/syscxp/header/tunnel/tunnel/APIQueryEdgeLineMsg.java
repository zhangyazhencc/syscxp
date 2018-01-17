package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2018/1/11
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = TunnelConstant.ACTION_CATEGORY, names = {"read"})
@AutoQuery(replyClass = APIQueryEdgeLineReply.class, inventoryClass = EdgeLineInventory.class)
public class APIQueryEdgeLineMsg extends APIQueryMessage {
}
