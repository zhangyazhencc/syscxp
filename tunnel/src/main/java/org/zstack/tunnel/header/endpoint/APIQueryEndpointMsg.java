package org.zstack.tunnel.header.endpoint;

import org.zstack.header.identity.Action;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;
import org.zstack.tunnel.manage.TunnelConstant;

/**
 * Created by DCY on 2017-08-23
 */

@Action(category = TunnelConstant.ACTION_CATEGORY, names = {"endpoint"}, adminOnly = true)
@AutoQuery(replyClass = APIQueryEndpointReply.class, inventoryClass = EndpointNodeInventory.class)
public class APIQueryEndpointMsg extends APIQueryMessage {
}
