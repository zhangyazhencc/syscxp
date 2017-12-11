package com.syscxp.header.vpn.host;

import com.syscxp.header.host.APIQueryHostMsg;
import com.syscxp.header.identity.Action;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.vpn.VpnConstant;

@Action(services = {VpnConstant.ACTION_SERVICE}, category = VpnConstant.ACTION_CATEGORY_HOST, names = {"read"}, adminOnly = true)
@AutoQuery(replyClass = APIQueryVpnHostMsg.class, inventoryClass = VpnHostInventory.class)
public class APIQueryVpnHostMsg extends APIQueryHostMsg {

}
