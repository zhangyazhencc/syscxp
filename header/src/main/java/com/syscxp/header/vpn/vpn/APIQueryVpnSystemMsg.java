package com.syscxp.header.vpn.vpn;

import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

@AutoQuery(replyClass = APIQueryVpnSystemReply.class, inventoryClass = VpnSystemInventory.class)
public class APIQueryVpnSystemMsg extends APIQueryMessage {
}
