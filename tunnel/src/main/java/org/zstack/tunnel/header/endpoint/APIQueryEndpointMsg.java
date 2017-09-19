package org.zstack.tunnel.header.endpoint;

import org.zstack.header.identity.Action;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;
import org.zstack.tunnel.manage.NodeConstant;
import org.zstack.tunnel.manage.TunnelConstant;

/**
 * Created by DCY on 2017-08-23
 */

@Action(category = NodeConstant.ACTION_CATEGORY, names = {"read"}, adminOnly = true)
@AutoQuery(replyClass = APIQueryEndpointReply.class, inventoryClass = EndpointInventory.class)
public class APIQueryEndpointMsg extends APIQueryMessage {
}
