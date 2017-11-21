package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2017/11/13
 */
@Action(services = {"tunnel"}, category = TunnelConstant.ACTION_CATEGORY, names = {"read"},adminOnly = true)
@AutoQuery(replyClass = APIQueryQinqReply.class, inventoryClass = QinqInventory.class)
public class APIQueryQinqMsg  extends APIQueryMessage {
}
