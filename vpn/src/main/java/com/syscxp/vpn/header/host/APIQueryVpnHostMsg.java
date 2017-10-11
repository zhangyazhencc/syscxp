package com.syscxp.vpn.header.host;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.vpn.vpn.VpnConstant;

@AutoQuery(replyClass = APIQueryVpnHostReply.class, inventoryClass = VpnHostInventory.class)
@Action(category = VpnConstant.ACTION_CATEGORY_VPN, names = {"read"}, adminOnly = true)
public class APIQueryVpnHostMsg extends APIQueryMessage {
}