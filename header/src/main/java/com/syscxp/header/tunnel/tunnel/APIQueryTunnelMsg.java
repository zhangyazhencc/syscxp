package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.rest.RestRequest;
import com.syscxp.header.tunnel.TunnelConstant;
import org.springframework.http.HttpMethod;

/**
 * Created by DCY on 2017-09-17
 */

@RestRequest(
        method = HttpMethod.GET,
        isAction = true,
        responseClass = APIQueryTunnelReply.class
)
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = TunnelConstant.ACTION_CATEGORY, names = {"read"})
@AutoQuery(replyClass = APIQueryTunnelReply.class, inventoryClass = TunnelInventory.class)
public class APIQueryTunnelMsg  extends APIQueryMessage {
}
