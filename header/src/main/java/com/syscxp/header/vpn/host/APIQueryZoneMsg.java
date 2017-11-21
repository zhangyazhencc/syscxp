package com.syscxp.header.vpn.host;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.vpn.VpnConstant;

@AutoQuery(replyClass = APIQueryZoneReply.class, inventoryClass = ZoneInventory.class)
@Action(services = {VpnConstant.ACTION_SERVICE}, category = VpnConstant.ACTION_CATEGORY, names = {"read"})
public class APIQueryZoneMsg extends APIQueryMessage {
}
