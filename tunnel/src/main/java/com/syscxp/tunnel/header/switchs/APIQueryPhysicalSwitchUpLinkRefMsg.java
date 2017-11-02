package com.syscxp.tunnel.header.switchs;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.tunnel.manage.SwitchConstant;

@Action(category = SwitchConstant.ACTION_CATEGORY, names = {"read"}, adminOnly = true)
@AutoQuery(replyClass = APIQueryPhysicalSwitchUpLinkRefReply.class, inventoryClass = PhysicalSwitchUpLinkRefInventory.class)
public class APIQueryPhysicalSwitchUpLinkRefMsg extends APIQueryMessage{
}
