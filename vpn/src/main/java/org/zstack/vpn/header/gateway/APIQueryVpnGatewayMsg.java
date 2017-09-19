package org.zstack.vpn.header.gateway;

import org.zstack.header.identity.Action;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;
import org.zstack.vpn.manage.VpnConstant;

@AutoQuery(replyClass = APIQueryVpnGatewayReply.class, inventoryClass = VpnInventory.class)
@Action(category = VpnConstant.ACTION_CATEGORY_VPN, names = {"read"}, adminOnly = true)
public class APIQueryVpnGatewayMsg extends APIQueryMessage {
}
