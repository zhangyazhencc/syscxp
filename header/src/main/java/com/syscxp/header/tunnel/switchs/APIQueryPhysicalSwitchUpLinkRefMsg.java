package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.SwitchConstant;

@Action(services = {"tunnel"}, category = SwitchConstant.ACTION_CATEGORY, names = {"read"}, adminOnly = true)
@AutoQuery(replyClass = APIQueryPhysicalSwitchUpLinkRefReply.class, inventoryClass = PhysicalSwitchUpLinkRefInventory.class)
public class APIQueryPhysicalSwitchUpLinkRefMsg extends APIQueryMessage{
}
