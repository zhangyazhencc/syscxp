package com.syscxp.tunnel.header.switchs;

import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

@AutoQuery(replyClass = APIQueryPhysicalSwitchUpLinkRefReply.class, inventoryClass = PhysicalSwitchUpLinkRefInventory.class)
public class APIQueryPhysicalSwitchUpLinkRefMsg extends APIQueryMessage{
}
