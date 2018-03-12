package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.rest.RestRequest;
import com.syscxp.header.tunnel.TunnelConstant;
import org.springframework.http.HttpMethod;

/**
 * Created by DCY on 2017-09-11
 */

@RestRequest(
        method = HttpMethod.GET,
        isAction = true,
        responseClass = APIQueryInterfaceReply.class
)
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = TunnelConstant.ACTION_CATEGORY, names = {"read"})
@AutoQuery(replyClass = APIQueryInterfaceReply.class, inventoryClass = InterfaceInventory.class)
public class APIQueryInterfaceMsg extends APIQueryMessage {
}
