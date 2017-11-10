package com.syscxp.header.tunnel.endpoint;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.NodeConstant;

/**
 * Create by DCY on 2017/11/2
 */
@Action(category = NodeConstant.ACTION_CATEGORY, names = {"read"})
@AutoQuery(replyClass = APIQueryInnerEndpointReply.class, inventoryClass = InnerConnectedEndpointInventory.class)
public class APIQueryInnerEndpointMsg extends APIQueryMessage {
}
