package org.zstack.tunnel.header.node;

import org.zstack.header.query.APIQueryMessage;
import org.zstack.header.query.AutoQuery;

/**
 * Created by DCY on 2017-08-21
 */
@AutoQuery(replyClass = APIQueryNodeReply.class, inventoryClass = NodeInventory.class)
public class APIQueryNodeMsg extends APIQueryMessage {
}
