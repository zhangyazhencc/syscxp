package com.syscxp.header.vpn.vpn;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.vpn.VpnConstant;

@AutoQuery(replyClass = APIQueryVpnReply.class, inventoryClass = VpnInventory.class)
@Action(services = {VpnConstant.ACTION_SERVICE}, category = VpnConstant.ACTION_CATEGORY_VPN, names = {"read"})
public class APIQueryVpnMsg extends APIQueryMessage {
}
