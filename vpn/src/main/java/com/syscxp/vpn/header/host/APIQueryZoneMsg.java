package com.syscxp.vpn.header.host;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.vpn.vpn.VpnConstant;

@AutoQuery(replyClass = APIQueryZoneReply.class, inventoryClass = ZoneInventory.class)
@Action(category = VpnConstant.ACTION_CATEGORY_VPN, names = {"read"})
public class APIQueryZoneMsg extends APIQueryMessage {
}
