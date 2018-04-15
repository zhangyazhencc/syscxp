package com.syscxp.header.vpn.l3vpn;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.vpn.vpn.VpnConstant;

@AutoQuery(replyClass = APIQueryL3VpnReply.class, inventoryClass = L3VpnInventory.class)
@Action(services = {VpnConstant.ACTION_SERVICE}, category = VpnConstant.ACTION_CATEGORY_VPN, names = {"read"})
public class APIQueryL3VpnMsg extends APIQueryMessage {
}
