package com.syscxp.header.tunnel.endpoint;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.NodeConstant;

/**
 * Created by DCY on 2017-08-23
 */

@Action(category = NodeConstant.ACTION_CATEGORY, names = {"read"})
@AutoQuery(replyClass = APIQueryEndpointReply.class, inventoryClass = EndpointInventory.class)
public class APIQueryEndpointMsg extends APIQueryMessage {
}
