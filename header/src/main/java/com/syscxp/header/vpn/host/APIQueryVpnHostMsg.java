package com.syscxp.header.vpn.host;

import com.syscxp.header.host.APIQueryHostMsg;
import com.syscxp.header.identity.Action;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.rest.RestRequest;
import com.syscxp.header.vpn.vpn.VpnConstant;
import org.springframework.http.HttpMethod;

@Action(services = {VpnConstant.ACTION_SERVICE}, category = VpnConstant.ACTION_CATEGORY_HOST, names = {"read"}, adminOnly = true)
@AutoQuery(replyClass = APIQueryVpnHostReply.class, inventoryClass = VpnHostInventory.class)
public class APIQueryVpnHostMsg extends APIQueryHostMsg {

}
