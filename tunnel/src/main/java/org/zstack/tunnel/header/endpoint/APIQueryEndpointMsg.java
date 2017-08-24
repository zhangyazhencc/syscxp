package org.zstack.tunnel.header.endpoint;

import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

/**
 * Created by DCY on 2017-08-23
 */
@AutoQuery(replyClass = APIQueryEndpointReply.class, inventoryClass = EndpointInventory.class)
public class APIQueryEndpointMsg extends APIQueryMessage {
}
