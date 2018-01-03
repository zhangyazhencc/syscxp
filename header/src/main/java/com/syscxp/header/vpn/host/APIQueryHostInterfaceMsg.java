package com.syscxp.header.vpn.host;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.rest.RestRequest;
import com.syscxp.header.vpn.vpn.VpnConstant;
import org.springframework.http.HttpMethod;

@RestRequest(
        method = HttpMethod.GET,
        isAction = true,
        responseClass = APIQueryHostInterfaceReply.class
)
@AutoQuery(replyClass = APIQueryHostInterfaceReply.class, inventoryClass = HostInterfaceInventory.class)
@Action(services = {VpnConstant.ACTION_SERVICE}, category = VpnConstant.ACTION_CATEGORY_HOST, names = {"read"}, adminOnly = true)
public class APIQueryHostInterfaceMsg extends APIQueryMessage {
}
