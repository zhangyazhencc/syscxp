package com.syscxp.vpn.header.vpn;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.vpn.vpn.VpnConstant;

@AutoQuery(replyClass = APIQueryVpnReply.class, inventoryClass = VpnInventory.class)
@Action(services = {"vpn"}, category = VpnConstant.ACTION_CATEGORY_VPN, names = {"read"})
public class APIQueryVpnMsg extends APIQueryMessage {
}
