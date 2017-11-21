package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2017/10/30
 */
@Action(services = {"tunnel"}, category = TunnelConstant.ACTION_CATEGORY, names = {"read"})
@AutoQuery(replyClass = APIQueryPortOfferingReply.class, inventoryClass = PortOfferingInventory.class)
public class APIQueryPortOfferingMsg extends APIQueryMessage {
}
