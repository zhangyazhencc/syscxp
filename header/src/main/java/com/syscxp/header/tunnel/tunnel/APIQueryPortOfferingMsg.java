package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.rest.RestRequest;
import com.syscxp.header.tunnel.TunnelConstant;
import org.springframework.http.HttpMethod;

/**
 * Create by DCY on 2017/10/30
 */

@RestRequest(
        method = HttpMethod.GET,
        isAction = true,
        responseClass = APIQueryPortOfferingReply.class
)
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = TunnelConstant.ACTION_CATEGORY, names = {"read"})
@AutoQuery(replyClass = APIQueryPortOfferingReply.class, inventoryClass = PortOfferingInventory.class)
public class APIQueryPortOfferingMsg extends APIQueryMessage {
}
